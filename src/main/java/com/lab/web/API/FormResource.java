package com.lab.web.API;

import java.util.Locale;
import java.util.Map;
import java.util.MissingFormatArgumentException;

import com.lab.web.data.HitDataBean;
import com.lab.web.data.PointData;
import com.lab.web.utils.Validator;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;

@Provider
@Path("/form")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FormResource { // TODO: use beans mb for nput and output
    @Context
    private UriInfo context;

    @Inject
    private HitDataBean hitDataBean;

    @GET
    public Response getData() {
        try {
            return Response.ok().entity(hitDataBean.getDataAsJson()).type(MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.toString() + "\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    @POST
    public Response postForm(Map<String, Object> requestBody) {
        String xStr = requestBody.get("x").toString();
        String yStr = requestBody.get("y").toString();
        String rStr = requestBody.get("r").toString();
        String graph = requestBody.get("graph").toString();

        try {
            PointData point = Validator.fillPoint(xStr, yStr, rStr, !("true".equals(graph)));
            hitDataBean.addPoint(point);

            String response = String.format(Locale.US,
                    "{\"x\": %.4f, \"y\": %.4f, \"r\": %.4f, \"hit\": %b, \"execTime\": %d, \"date\": \"%s\"}",
                    point.getX(), point.getY(), point.getR(), point.isHit(), point.getExecTime(),
                    point.getdateFormatted());
            System.out.println("Processed point: graph " + graph + " " + response);

            return Response.ok()
                    .entity(hitDataBean.getDataAsJson())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (MissingFormatArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Missing point arguments\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.toString() + "\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Invalid request data: " + e.getMessage() + "\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
}
