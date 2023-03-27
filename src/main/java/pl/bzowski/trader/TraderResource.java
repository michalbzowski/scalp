package pl.bzowski.trader;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jboss.resteasy.annotations.jaxrs.PathParam;
import pro.xstore.api.message.codes.PERIOD_CODE;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.response.APIErrorResponse;

import javax.ws.rs.core.Response;

@Path("/trader")
public class TraderResource {

    @Inject
    TraderService traderService;

    @POST
    @Path(("/{symbol}"))
    public Response startTrade(@PathParam String symbol) throws APIErrorResponse, APICommunicationException, APIReplyParseException, APICommandConstructionException {
        traderService.startTrade(symbol, "nn", PERIOD_CODE.PERIOD_M1);
        return Response.accepted().build();
    }
}
