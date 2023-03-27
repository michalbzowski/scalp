package pl.bzowski.tradingbot.commands;

import pro.xstore.api.message.command.APICommandFactory;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.response.APIErrorResponse;
import pro.xstore.api.message.response.SymbolResponse;
import pro.xstore.api.sync.SyncAPIConnector;

public class SymbolCommand {

    private final SyncAPIConnector connector;

    public SymbolCommand(SyncAPIConnector connector) {
        this.connector = connector;
    }

    public SymbolResponse execute(String symbol) throws APICommandConstructionException, APIReplyParseException, APIErrorResponse, APICommunicationException {
        return APICommandFactory.executeSymbolCommand(connector, symbol);
    }

}
