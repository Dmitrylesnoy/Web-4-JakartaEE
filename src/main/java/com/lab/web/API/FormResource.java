package com.lab.web.api;

import java.net.URI;
import java.util.Locale;
import java.util.logging.Logger;

import com.lab.web.api.records.Point;
import com.lab.web.data.HitDataBean;
import com.lab.web.data.PointData;
import com.lab.web.utils.UserVetification;
import com.lab.web.utils.Validator;

import jakarta.inject.Inject;
import jakarta.security.auth.message.AuthException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
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
public class FormResource {
    @Context
    private UriInfo context;

    @Inject
    private HitDataBean hitDataBean;

    private static final Logger logger = Logger.getLogger(LoginResource.class.getName());

    @GET
    public Response getData(@HeaderParam("AuthToken") String authTokenHeader) {
        try {
            UserVetification.checkUserByToken(authTokenHeader);

            logger.info("success data fetch");
            return Response.ok().entity(hitDataBean.getDataAsJson(UserVetification.getUserIDbyToken(authTokenHeader)))
                    .type(MediaType.APPLICATION_JSON).build();
        } catch (AuthException e) {
            URI logoutUri = context.getBaseUriBuilder().path("user").path("logout").build();
            return Response.temporaryRedirect(logoutUri).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    @POST
    public Response postForm(@HeaderParam("AuthToken") String authTokenHeader, Point pointBean) {
        try {
            UserVetification.checkUserByToken(authTokenHeader);

            PointData point = Validator.fillPoint(
                    String.valueOf(pointBean.x()),
                    String.valueOf(pointBean.y()),
                    String.valueOf(pointBean.r()),
                    pointBean.graphFlag());

            Long userId = UserVetification.getUserIDbyToken(authTokenHeader);
            point.setUser(userId);
            hitDataBean.addPoint(point);

            String response = String.format(Locale.US,
                    "{\"x\": %.4f, \"y\": %.4f, \"r\": %.4f, \"hit\": %b, \"execTime\": %d, \"date\": \"%s\"}",
                    point.getX(), point.getY(), point.getR(), point.isHit(), point.getExecTime(),
                    point.getdateFormatted());
            logger.info("Processed point: graph " + pointBean.graphFlag() + " User: " + userId + " " + response);

            return Response.ok()
                    .entity(response)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (AuthException e) {
            URI logoutUri = context.getBaseUriBuilder().path("user").path("logout").build();
            return Response.temporaryRedirect(logoutUri).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
}
