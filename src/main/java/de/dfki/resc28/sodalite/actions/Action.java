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
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.sparql.core.Prologue;
import org.apache.jena.vocabulary.RDF;
import org.topbraid.spin.model.SPINFactory;
import org.topbraid.spin.vocabulary.SP;

import de.dfki.resc28.sodalite.vocabularies.ACTN;
import de.dfki.resc28.igraphstore.IGraphStore;

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
		final Model description = fGraphStore.getNamedGraph(fURI);
		
		StreamingOutput out = new StreamingOutput() 
		{
			public void write(OutputStream output) throws IOException, WebApplicationException
			{
				RDFDataMgr.write(output, description, RDFDataMgr.determineLang(null, acceptType, null)) ;
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
	public Model updateState(Model consumable) 
	{
		throw new WebApplicationException(Status.NOT_IMPLEMENTED);
	}

	public boolean isApplicable()
	{
		return fGraphStore.getDefaultGraph().contains(null, ACTN.action, ResourceFactory.createResource(fURI));
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
		allowedMethods.add(HttpMethod.OPTIONS);
	    return allowedMethods;
	}
	
	protected String fURI;
	protected IGraphStore fGraphStore;
	protected Resource fRDFType;
}
