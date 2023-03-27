package pl.bzowski.tradingbot.commands;

import pro.xstore.api.message.command.APICommandFactory;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.response.APIErrorResponse;
import pro.xstore.api.message.response.TradesResponse;
import pro.xstore.api.sync.SyncAPIConnector;

public class TradesCommand {

    private final SyncAPIConnector connector;

    public TradesCommand(SyncAPIConnector connector) {
        this.connector = connector;
    }

    public TradesResponse execute(boolean openedOnly) throws APICommandConstructionException, APIReplyParseException, APICommunicationException, APIErrorResponse {
        return APICommandFactory.executeTradesCommand(connector, openedOnly);
    }

}
