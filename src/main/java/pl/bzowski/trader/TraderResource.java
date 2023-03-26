package pl.bzowski.trader;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jboss.resteasy.annotations.jaxrs.PathParam;
import pro.xstore.api.message.codes.PERIOD_CODE;

import javax.ws.rs.core.Response;

@Path("/trader")
public class TraderResource {

    @Inject
    TraderService traderService;

    @POST
    @Path(("/{symbol}"))
    public Response startTrade(@PathParam String symbol) {
        traderService.startTrade(symbol, "nn", PERIOD_CODE.PERIOD_M1);
        return Response.accepted().build();
    }
}
