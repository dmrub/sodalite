/*
 * This file is part of Sodalite. It is subject to the license terms in
 * the LICENSE file found in the top-level directory of this distribution.
 * You may not use this file except in compliance with the License.
 */

package de.dfki.resc28.sodalite.actions;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.sparql.core.Prologue;
import org.apache.jena.vocabulary.RDF;
import org.topbraid.spin.model.SPINFactory;
import org.topbraid.spin.vocabulary.SP;

import de.dfki.resc28.igraphstore.IGraphStore;
import de.dfki.resc28.sodalite.vocabularies.ACTN;
import de.dfki.resc28.sodalite.vocabularies.LTS;

public class Action implements IAction 
{
	public Action(String actionURI, IGraphStore graphStore)
	{
		this.fURI = actionURI;
		this.fGraphStore = graphStore; 
		this.fRDFType = ACTN.Action;
	}

	public Response describe(final String acceptType) 
	{
		final Model actionModel = fGraphStore.getNamedGraph(fURI);
		
		StreamingOutput out = new StreamingOutput() 
		{
			public void write(OutputStream output) throws IOException, WebApplicationException
			{
				RDFDataMgr.write(output, actionModel, RDFDataMgr.determineLang(null, acceptType, null)) ;
			}
		};
		
		return Response.ok(out)
					   .type(acceptType)
					   .build();
	}
	
	public Response execute(InputStream consumableStream, String contentType, final String acceptType)
	{
		Model consumable = ModelFactory.createDefaultModel(); 
		RDFDataMgr.read(consumable, consumableStream, RDFDataMgr.determineLang(null, acceptType, null));
			
		if (isApplicable() & isValidConsumable(consumable))
		{
			final Model producible = updateState((Model) performTasks(consumable));
						
			// TODO: get Model's base name for the contentLocation header

			StreamingOutput out = new StreamingOutput() 
			{
				public void write(OutputStream output) throws IOException, WebApplicationException
				{
					RDFDataMgr.write(output, producible, RDFDataMgr.determineLang(null, acceptType, null)) ;
				}
			};
			
			// TODO: set the contentLocation header!
			
			return Response.ok(out)
						   .type(acceptType)
						   .build();			
		}
		
		throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
	}
	
	@Override
	public Model performTasks(Model consumable) 
	{
		throw new WebApplicationException(Status.NOT_IMPLEMENTED);
	}

	@Override
	public Model updateState(Model modifiedState) 
	{
		// select nextState from machineModel, depends on currentState and currentAction
		Model machineModel = fGraphStore.getNamedGraph(((Resource) modifiedState.listObjectsOfProperty(LTS.model).next()).getURI().toString());
		Resource device = machineModel.listSubjectsWithProperty(RDF.type, LTS.StateMachine).next().asResource();
		
		// FIXME: nasty hack!
		RDFNode stateIndicatorAsNode = machineModel.listObjectsOfProperty(LTS.stateIndicator).next();
		Property stateIndicator = ResourceFactory.createProperty(stateIndicatorAsNode.asNode().getURI().toString());
		Resource currentState = modifiedState.listObjectsOfProperty(stateIndicator).next().asResource();
		
		PrefixMapping pfxMap = PrefixMapping.Factory.create();
		pfxMap.setNsPrefixes(modifiedState.getNsPrefixMap());
		pfxMap.setNsPrefixes(machineModel.getNsPrefixMap());
		Prologue sparqlPrologue = new Prologue(pfxMap);
		String queryString = String.format("SELECT ?next FROM <%s> WHERE { ?a rdf:type lts:StateMachine ; lts:contains [ rdf:type lts:Transition ; lts:source [ lts:stateID  <%s> ] ; lts:label <%s> ; lts:target [ lts:stateID  ?next ] ] . }", 
				  						   ((Resource) modifiedState.listObjectsOfProperty(LTS.model).next()).getURI().toString(),
				  						   currentState.getURI().toString(), 
				  						   fURI) ;
		Query q = QueryFactory.parse(new Query(sparqlPrologue),
									queryString,
									null,
									null);
		QueryExecution e = QueryExecutionFactory.create(q, machineModel);
		Resource nextState = e.execSelect().next().getResource("?next");

		// remove all actn:action Triples of the currentState
		modifiedState.remove(modifiedState.listStatements(device, ACTN.action, (RDFNode) null));
		modifiedState.remove(modifiedState.listStatements(device, stateIndicator, (RDFNode) null));
		
		
		
		Query nextActions = QueryFactory.parse(new Query(sparqlPrologue),
											   String.format("SELECT ?nextAction FROM <%s> WHERE { ?a rdf:type lts:StateMachine ; lts:contains [ rdf:type lts:Transition ; lts:source [ lts:stateID <%s> ] ; lts:label ?nextAction ] . }", 
													   ((Resource) modifiedState.listObjectsOfProperty(LTS.model).next()).getURI().toString(),
													   nextState.getURI().toString()),
				 							   null,
				 							   null);
		
		// set the nextState and its actn:action triples
		modifiedState.add(device, stateIndicator, nextState);
		ResultSet rs = QueryExecutionFactory.create(nextActions, machineModel).execSelect(); 
		while (rs.hasNext())
		{
			modifiedState.add(device, ACTN.action, rs.next().get("?nextAction").asResource());
		}

		
		Model actionModel = fGraphStore.getNamedGraph(fURI);
		Resource action = ResourceFactory.createResource(fURI);
		Resource actionableResource = actionModel.listSubjectsWithProperty(ACTN.affordedBy, action).next().asResource();
		
		fGraphStore.replaceNamedGraph(actionableResource.getURI(), modifiedState);
		
		return fGraphStore.getNamedGraph(actionableResource.getURI());
	}

	public boolean isApplicable()
	{
		Model actionModel = fGraphStore.getNamedGraph(fURI);
		Resource action = ResourceFactory.createResource(fURI);
		Resource actionableResource = actionModel.listSubjectsWithProperty(ACTN.affordedBy, action).next().asResource();
		
		return fGraphStore.getNamedGraph(actionableResource.getURI()).contains(actionableResource, ACTN.action, action);
	}
	
	public boolean isValidConsumable(Model consumableModel)
	{
		Model actionModel = fGraphStore.getNamedGraph(fURI);
		
		// construct sparqlPrologue
		PrefixMapping pfxMap = PrefixMapping.Factory.create();
		pfxMap.setNsPrefixes(actionModel.getGraph().getPrefixMapping());
		pfxMap.setNsPrefixes(consumableModel.getGraph().getPrefixMapping());
		Prologue sparqlPrologue = new Prologue(pfxMap);
		
		// fetch the consumableQuery from actionModel
		Query q = QueryFactory.parse(new Query(sparqlPrologue),
									 "DESCRIBE ?spin FROM <" + fURI + "> WHERE { _:action actn:consumes ?spin . }",
									 null,
									 null);
		QueryExecution e = QueryExecutionFactory.create(q, actionModel);
		Model spinQueryModel =  e.execDescribe();
		
		// transform the spinQueryModel into Jena query
		Resource instance = SPINFactory.asQuery(spinQueryModel.listResourcesWithProperty(RDF.type, SP.Ask).next());
		org.topbraid.spin.model.Query spinQuery = SPINFactory.asQuery(instance);
		Query jenaQuery = QueryFactory.parse(new Query(sparqlPrologue),
				  							 spinQuery.toString(),
				  							 null,
				  							 null);
		boolean isValid = QueryExecutionFactory.create(jenaQuery, consumableModel).execAsk(); 
		return isValid;
	}
	
	public Set<String> getAllowedMethods() 
	{
		HashSet<String> allowedMethods = new HashSet<String>();
		allowedMethods.add(HttpMethod.GET);
		
		// get bindings from actionModel
		Model actionModel = fGraphStore.getNamedGraph(fURI);
		PrefixMapping pfxMap = PrefixMapping.Factory.create();
		pfxMap.setNsPrefixes(actionModel.getGraph().getPrefixMapping());
		Prologue sparqlPrologue = new Prologue(pfxMap);
		Query q = QueryFactory.parse(new Query(sparqlPrologue),
				 					 "SELECT ?mth FROM <" + fURI + "> WHERE { _:a actn:binding [ http:mthd ?mth ] . }",
				 					 null,
				 					 null);
		ResultSet rs = QueryExecutionFactory.create(q, actionModel).execSelect();
		while (rs.hasNext())
		{
			allowedMethods.add(rs.nextSolution().get("?mth").asResource().getLocalName());
		}
		
	    return allowedMethods;
	}
	
	protected String fURI;
	protected IGraphStore fGraphStore;
	protected Resource fRDFType;
}
