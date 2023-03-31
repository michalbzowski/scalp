package pl.bzowski.trader;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import pro.xstore.api.message.codes.PERIOD_CODE;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.records.SymbolRecord;
import pro.xstore.api.message.response.APIErrorResponse;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

@Path("/trader")
public class TraderResource {

    @Inject
    TraderService traderService;

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance chart(Object pairs);
    }

    @GET
    @Path("/{symbol}")
    @Produces(MediaType.APPLICATION_JSON)
    public TemplateInstance chart(@PathParam String symbol) {
        var chart = traderService.getChart(symbol);
        return Templates.chart(chart);
    }

    @POST
    @Path(("/{symbol}"))
    public Response startTrade(@PathParam String symbol) throws APIErrorResponse, APICommunicationException, APIReplyParseException, APICommandConstructionException {
        traderService.startTrade(symbol, "nn", PERIOD_CODE.PERIOD_M1);
        return Response.accepted().build();
    }
}
