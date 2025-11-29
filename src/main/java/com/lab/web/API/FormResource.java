package com.lab.web.API;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;

@Provider
@Path("/form")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FormResource {
    @Context
    private UriInfo context;

    @POST
    public Response postForm(MultivaluedMap<String, String> formParams) {
        String x = formParams.getFirst("x");
        String y = formParams.getFirst("y");
        String r = formParams.getFirst("r");
        String graph = formParams.getFirst("graph");

        boolean hit = false; // TODO Implement hit calculation logic

        String response = String.format(
                "{\"x\": \"%s\", \"y\": \"%s\", \"r\": \"%s\", \"hit\": %s}",
                x, y, r, hit);

        return Response.ok()
                .entity(response)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
