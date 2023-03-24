package pl.bzowski.symbol;

import io.quarkus.cache.CacheResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bzowski.ConnectorProvider;
import pl.bzowski.PlatformAuthorizationService;
import pro.xstore.api.message.command.APICommandFactory;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.records.SymbolRecord;
import pro.xstore.api.message.response.APIErrorResponse;
import pro.xstore.api.message.response.AllSymbolsResponse;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Singleton
public class SymbolsService {

    Logger logger = LoggerFactory.getLogger(SymbolsService.class);

    @Inject
    PlatformAuthorizationService platformAuthorizationService;
    @Inject
    ConnectorProvider connectorProvider;


    @CacheResult(cacheName = "symbols-cache")
    public Collection<SymbolRecord> getAllCurrencyPairs() throws APIErrorResponse, APICommunicationException, APIReplyParseException, APICommandConstructionException, IOException {
        if (!platformAuthorizationService.authorize()) {
            logger.info("No authorization");
            return Collections.emptyList();
        }
        logger.info("Successful authorization");
        var connector = connectorProvider.get();
        AllSymbolsResponse allSymbolsResponse = APICommandFactory.executeAllSymbolsCommand(connector);
        Collection<SymbolRecord> symbolRecords = allSymbolsResponse.getSymbolRecords()
                .stream()
                .filter(SymbolRecord::isCurrencyPair)
//                .filter(sr -> symbols.contains(sr.getSymbol()))
                .collect(Collectors.toSet());
        logger.info("Symbols count: " + symbolRecords.size());
        return symbolRecords;
    }

}
