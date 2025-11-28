package com.lab.web.controllers;

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

@Path("form")
public class FormController {
    @Context
    private UriInfo context;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getFormPage() {
        return Response.seeOther(context.getBaseUri().resolve("../index.html")).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response postForm(MultivaluedMap<String, String> formParams) {
        String x = formParams.getFirst("x");
        String y = formParams.getFirst("y");
        String r = formParams.getFirst("r");

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
