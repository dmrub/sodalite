/*
 * This file is part of Sodalite. It is subject to the license terms in
 * the LICENSE file found in the top-level directory of this distribution.
 * You may not use this file except in compliance with the License.
 */
package de.dfki.resc28.sodalite.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;

import de.dfki.resc28.flapjack.services.BaseService;
import static de.dfki.resc28.flapjack.services.BaseService.RDF_MEDIA_TYPES;
import de.dfki.resc28.igraphstore.Constants;
import de.dfki.resc28.igraphstore.IGraphStore;
import de.dfki.resc28.sodalite.actions.IAction;
import de.dfki.resc28.sodalite.actions.IActionController;
import javax.ws.rs.core.MediaType;

/**
 * @author resc01
 *
 */
public abstract class ActionProvider extends BaseService {

    public ActionProvider(IGraphStore graphStore) {
        this.fGraphStore = graphStore;
    }

    @Path("model")
    @GET
    @Produces({Constants.CT_APPLICATION_JSON_LD, Constants.CT_APPLICATION_NQUADS, Constants.CT_APPLICATION_NTRIPLES, Constants.CT_APPLICATION_RDF_JSON, Constants.CT_APPLICATION_RDFXML, Constants.CT_APPLICATION_TRIX, Constants.CT_APPLICATION_XTURTLE, Constants.CT_TEXT_N3, Constants.CT_TEXT_TRIG, Constants.CT_TEXT_TURTLE})
    public Response describeModel(@HeaderParam(HttpHeaders.ACCEPT) final String acceptType) {
        final MediaType responseMediaType = negotiateRDFMediaType();
        if (responseMediaType == null) {
            return Response.notAcceptable(RDF_MEDIA_TYPES).build();
        }
        final String responseMediaTypeStr = responseMediaType.getType() + "/" + responseMediaType.getSubtype();

        final Model model = fGraphStore.getNamedGraph(getResourceURI(fRequestUrl));
        StreamingOutput out = new StreamingOutput() {
            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
                RDFDataMgr.write(output, model, RDFDataMgr.determineLang(null, responseMediaTypeStr, null));
            }
        };

        return Response.ok(out)
                .type(responseMediaTypeStr)
                .build();
    }

    @Path("actions/{actionID: .+}")
    @GET
    @Produces({Constants.CT_APPLICATION_JSON_LD, Constants.CT_APPLICATION_NQUADS, Constants.CT_APPLICATION_NTRIPLES, Constants.CT_APPLICATION_RDF_JSON, Constants.CT_APPLICATION_RDFXML, Constants.CT_APPLICATION_TRIX, Constants.CT_APPLICATION_XTURTLE, Constants.CT_TEXT_N3, Constants.CT_TEXT_TRIG, Constants.CT_TEXT_TURTLE})
    public Response describeAction(@HeaderParam(HttpHeaders.ACCEPT) final String acceptType) {
        IAction action = getActionController().get(getResourceURI(fRequestUrl));

        if (action == null) {
            return Response.status(Status.NOT_FOUND).build();
        } else {
            final MediaType responseMediaType = negotiateRDFMediaType();
            if (responseMediaType == null) {
                return Response.notAcceptable(RDF_MEDIA_TYPES).build();
            }
            final String responseMediaTypeStr = responseMediaType.getType() + "/" + responseMediaType.getSubtype();

            return action.describe(responseMediaTypeStr);
        }
    }

    @Path("actions/{actionID: .+}")
    @POST
    @Consumes({Constants.CT_APPLICATION_JSON_LD, Constants.CT_APPLICATION_NQUADS, Constants.CT_APPLICATION_NTRIPLES, Constants.CT_APPLICATION_RDF_JSON, Constants.CT_APPLICATION_RDFXML, Constants.CT_APPLICATION_TRIX, Constants.CT_APPLICATION_XTURTLE, Constants.CT_TEXT_N3, Constants.CT_TEXT_TRIG, Constants.CT_TEXT_TURTLE})
    @Produces({Constants.CT_APPLICATION_JSON_LD, Constants.CT_APPLICATION_NQUADS, Constants.CT_APPLICATION_NTRIPLES, Constants.CT_APPLICATION_RDF_JSON, Constants.CT_APPLICATION_RDFXML, Constants.CT_APPLICATION_TRIX, Constants.CT_APPLICATION_XTURTLE, Constants.CT_TEXT_N3, Constants.CT_TEXT_TRIG, Constants.CT_TEXT_TURTLE})
    public Response executeByPOST(@HeaderParam(HttpHeaders.ACCEPT) final String acceptType,
            @HeaderParam(HttpHeaders.ACCEPT) final String contentType,
            InputStream consumableStream) {
        IAction action = getActionController().get(getResourceURI(fRequestUrl));

        if (action == null) {
            return Response.status(Status.NOT_FOUND).build();
        } else if (!(action.getAllowedMethods().contains(HttpMethod.POST))) {
            return Response.status(Status.METHOD_NOT_ALLOWED).build();
        } else if (!action.isApplicable()) {
            return Response.status(Status.FORBIDDEN).build();
        } else {
            return action.execute(consumableStream, contentType, acceptType);
        }
    }

    @Path("actions/{actionID: .+}")
    @PUT
    @Consumes({Constants.CT_APPLICATION_JSON_LD, Constants.CT_APPLICATION_NQUADS, Constants.CT_APPLICATION_NTRIPLES, Constants.CT_APPLICATION_RDF_JSON, Constants.CT_APPLICATION_RDFXML, Constants.CT_APPLICATION_TRIX, Constants.CT_APPLICATION_XTURTLE, Constants.CT_TEXT_N3, Constants.CT_TEXT_TRIG, Constants.CT_TEXT_TURTLE})
    @Produces({Constants.CT_APPLICATION_JSON_LD, Constants.CT_APPLICATION_NQUADS, Constants.CT_APPLICATION_NTRIPLES, Constants.CT_APPLICATION_RDF_JSON, Constants.CT_APPLICATION_RDFXML, Constants.CT_APPLICATION_TRIX, Constants.CT_APPLICATION_XTURTLE, Constants.CT_TEXT_N3, Constants.CT_TEXT_TRIG, Constants.CT_TEXT_TURTLE})
    public Response executeByPUT(@HeaderParam(HttpHeaders.ACCEPT) final String acceptType,
            @HeaderParam(HttpHeaders.ACCEPT) final String contentType,
            InputStream consumableStream) {
        IAction action = getActionController().get(getResourceURI(fRequestUrl));

        if (action == null) {
            return Response.status(Status.NOT_FOUND).build();
        } else if (!(action.getAllowedMethods().contains(HttpMethod.PUT))) {
            return Response.status(Status.METHOD_NOT_ALLOWED).build();
        } else if (!action.isApplicable()) {
            return Response.status(Status.FORBIDDEN).build();
        } else {
            return action.execute(consumableStream, contentType, acceptType);
        }
    }

    @Path("/actions/{actionID: .+}")
    @PATCH
    @Consumes({Constants.CT_APPLICATION_JSON_LD, Constants.CT_APPLICATION_NQUADS, Constants.CT_APPLICATION_NTRIPLES, Constants.CT_APPLICATION_RDF_JSON, Constants.CT_APPLICATION_RDFXML, Constants.CT_APPLICATION_TRIX, Constants.CT_APPLICATION_XTURTLE, Constants.CT_TEXT_N3, Constants.CT_TEXT_TRIG, Constants.CT_TEXT_TURTLE})
    @Produces({Constants.CT_APPLICATION_JSON_LD, Constants.CT_APPLICATION_NQUADS, Constants.CT_APPLICATION_NTRIPLES, Constants.CT_APPLICATION_RDF_JSON, Constants.CT_APPLICATION_RDFXML, Constants.CT_APPLICATION_TRIX, Constants.CT_APPLICATION_XTURTLE, Constants.CT_TEXT_N3, Constants.CT_TEXT_TRIG, Constants.CT_TEXT_TURTLE})
    public Response executeByPATCH(@HeaderParam(HttpHeaders.ACCEPT) final String acceptType,
            @HeaderParam(HttpHeaders.ACCEPT) final String contentType,
            InputStream consumableStream) {
        IAction action = getActionController().get(getResourceURI(fRequestUrl));

        if (action == null) {
            return Response.status(Status.NOT_FOUND).build();
        } else if (!(action.getAllowedMethods().contains("PATCH"))) {
            return Response.status(Status.METHOD_NOT_ALLOWED).build();
        } else if (!action.isApplicable()) {
            return Response.status(Status.FORBIDDEN).build();
        } else {
            return action.execute(consumableStream, contentType, acceptType);
        }
    }

    protected abstract IActionController getActionController();
    protected IGraphStore fGraphStore;
}
