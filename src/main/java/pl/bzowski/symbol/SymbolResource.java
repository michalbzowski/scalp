package pl.bzowski.symbol;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import pro.xstore.api.message.codes.PERIOD_CODE;
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

@Path("/symbol")
public class SymbolResource {

    @Inject
    SymbolsService symbolsService;

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance view(SymbolViewModel vm);
    }



    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public TemplateInstance get() throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException {
        var allCurrencyPairs = symbolsService.getAllCurrencyPairs();
        SymbolViewModel symbolViewModel = new SymbolViewModel();
        symbolViewModel.pairs = allCurrencyPairs;
        symbolViewModel.periodCodes = PERIOD_CODE.all();
        return Templates.view(symbolViewModel);
    }
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response hello() throws APIErrorResponse, APICommunicationException, APIReplyParseException, APICommandConstructionException, IOException {
//        return Response.ok(symbolsService.getAllCurrencyPairs()).build();
//    }
}