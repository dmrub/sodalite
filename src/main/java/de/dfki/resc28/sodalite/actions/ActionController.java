/*
 * This file is part of Sodalite. It is subject to the license terms in
 * the LICENSE file found in the top-level directory of this distribution.
 * You may not use this file except in compliance with the License.
 */
package de.dfki.resc28.sodalite.actions;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import de.dfki.resc28.igraphstore.IGraphStore;
import de.dfki.resc28.sodalite.vocabularies.ACTN;

/**
 * @author resc01
 *
 */
public class ActionController implements IActionController 
{
	public ActionController(IGraphStore graphStore)
	{
		this.fGraphStore = graphStore;
	}
	
	/* (non-Javadoc)
	 * @see de.dfki.resc28.sodalite.actions.IActionController#get(java.lang.String)
	 */
	@Override
	public IAction get(String actionURI)
	{
		Model actionModel = fGraphStore.getNamedGraph(actionURI);
		
		if (actionModel == null)
			return null;
		
		Resource action = actionModel.getResource(actionURI);
		
		if (action.hasProperty(RDF.type, ACTN.Action))
		{
			return new Action(actionURI, fGraphStore);
		}
		else
		{
			return null;
		}
	}
	
	protected IGraphStore fGraphStore;
}
