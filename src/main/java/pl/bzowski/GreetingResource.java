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
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/hello")
public class GreetingResource {

    @Inject
    SymbolsService symbolsService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response hello() throws APIErrorResponse, APICommunicationException, APIReplyParseException, APICommandConstructionException, IOException {
        return Response.ok(symbolsService.getAllCurrencyPairs()).build();
    }
}