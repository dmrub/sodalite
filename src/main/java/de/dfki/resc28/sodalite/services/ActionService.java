/*
 * This file is part of Sodalite. It is subject to the license terms in
 * the LICENSE file found in the top-level directory of this distribution.
 * You may not use this file except in compliance with the License.
 */
package de.dfki.resc28.sodalite.services;

import java.io.InputStream;
import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import de.dfki.resc28.igraphstore.Constants;
import de.dfki.resc28.igraphstore.IGraphStore;
import de.dfki.resc28.sodalite.actions.ActionController;
import de.dfki.resc28.sodalite.actions.IAction;



@Path("")
public abstract class ActionService 
{
	public ActionService(IGraphStore graphStore)
	{
		this.fGraphStore = graphStore;
	}
	
	@GET
	@Produces({ Constants.CT_TEXT_TURTLE, Constants.CT_APPLICATION_RDFXML, Constants.CT_APPLICATION_XTURTLE, Constants.CT_APPLICATION_JSON, Constants.CT_APPLICATION_LD_JSON })
	public Response describeAction(@HeaderParam(HttpHeaders.ACCEPT) final String acceptType)
	{
		IAction action = getActionController().get(getCanonicalURL(fRequestUriInfo.getRequestUri()));
		
		if (action == null)
		{
			return Response.status(Status.NOT_FOUND).build();
		}
		else 
		{
			return action.describe(acceptType);
		}
	}
	

	@POST
	@Consumes({ Constants.CT_TEXT_TURTLE, Constants.CT_APPLICATION_RDFXML, Constants.CT_APPLICATION_XTURTLE, Constants.CT_APPLICATION_JSON, Constants.CT_APPLICATION_LD_JSON })
	@Produces({ Constants.CT_TEXT_TURTLE, Constants.CT_APPLICATION_RDFXML, Constants.CT_APPLICATION_XTURTLE, Constants.CT_APPLICATION_JSON, Constants.CT_APPLICATION_LD_JSON })
	public Response executeByPOST(@HeaderParam(HttpHeaders.ACCEPT) final String acceptType,
								  @HeaderParam(HttpHeaders.ACCEPT) final String contentType,
								  InputStream consumableStream)
	{
		IAction action = getActionController().get(getCanonicalURL(fRequestUriInfo.getRequestUri()));
		
		if (action == null)
		{
			return Response.status(Status.NOT_FOUND).build();
		}
		else if (!(action.getAllowedMethods().contains(HttpMethod.POST)))
		{
			return Response.status(Status.METHOD_NOT_ALLOWED).build();
		}
		else if (!action.isApplicable())
		{
			return Response.status(Status.FORBIDDEN).build();
		}
		else {
			return action.execute(consumableStream, contentType, acceptType);
		}
	}
	
	@PUT
	@Consumes({ Constants.CT_TEXT_TURTLE, Constants.CT_APPLICATION_RDFXML, Constants.CT_APPLICATION_XTURTLE, Constants.CT_APPLICATION_JSON, Constants.CT_APPLICATION_LD_JSON })
	@Produces({ Constants.CT_TEXT_TURTLE, Constants.CT_APPLICATION_RDFXML, Constants.CT_APPLICATION_XTURTLE, Constants.CT_APPLICATION_JSON, Constants.CT_APPLICATION_LD_JSON })
	public Response executeByPUT(@HeaderParam(HttpHeaders.ACCEPT) final String acceptType,
								 @HeaderParam(HttpHeaders.ACCEPT) final String contentType,
								 InputStream consumableStream)
	{
		IAction action = getActionController().get(getCanonicalURL(fRequestUriInfo.getRequestUri()));
		
		if (action == null)
		{
			return Response.status(Status.NOT_FOUND).build();
		}
		else if (!(action.getAllowedMethods().contains(HttpMethod.PUT)))
		{
			return Response.status(Status.METHOD_NOT_ALLOWED).build();
		}
		else if (!action.isApplicable())
		{
			return Response.status(Status.FORBIDDEN).build();
		}
		else {
			return action.execute(consumableStream, contentType, acceptType);
		}
	}
	
	@PATCH
	@Consumes({ Constants.CT_TEXT_TURTLE, Constants.CT_APPLICATION_RDFXML, Constants.CT_APPLICATION_XTURTLE, Constants.CT_APPLICATION_JSON, Constants.CT_APPLICATION_LD_JSON })
	@Produces({ Constants.CT_TEXT_TURTLE, Constants.CT_APPLICATION_RDFXML, Constants.CT_APPLICATION_XTURTLE, Constants.CT_APPLICATION_JSON, Constants.CT_APPLICATION_LD_JSON })
	public Response executeByPATCH(@HeaderParam(HttpHeaders.ACCEPT) final String acceptType,
								   @HeaderParam(HttpHeaders.ACCEPT) final String contentType,
								   InputStream consumableStream)
	{
		IAction action = getActionController().get(getCanonicalURL(fRequestUriInfo.getRequestUri()));
		
		if (action == null)
		{
			return Response.status(Status.NOT_FOUND).build();
		}
		else if (!(action.getAllowedMethods().contains("PATCH")))
		{
			return Response.status(Status.METHOD_NOT_ALLOWED).build();
		}
		else if (!action.isApplicable())
		{
			return Response.status(Status.FORBIDDEN).build();
		}
		else {
			return action.execute(consumableStream, contentType, acceptType);
		}
	}	
	
	protected ActionController getActionController() 
	{
		return new ActionController(fGraphStore);
	}
	
    protected String getCanonicalURL (URI url)
    {
    	 if(url.toString().endsWith("/")) 
    	 {
    		 return url.toString().substring(0, url.toString().length() - 1);	
   		 }

    	return url.toString();
    }
	
	protected IGraphStore fGraphStore;
	@Context protected UriInfo fRequestUriInfo;	
}
