package pl.bzowski;

import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.response.APIErrorResponse;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("/hello")
public class GreetingResource {

    @Inject
    PlatformAuthorizationService platformAuthorizationService;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException {
        return String.valueOf(platformAuthorizationService.authorize());
    }
}