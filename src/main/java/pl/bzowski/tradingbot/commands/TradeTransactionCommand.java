package pl.bzowski.tradingbot.commands;

import pro.xstore.api.message.command.APICommandFactory;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.records.TradeTransInfoRecord;
import pro.xstore.api.message.response.APIErrorResponse;
import pro.xstore.api.message.response.TradeTransactionResponse;
import pro.xstore.api.sync.SyncAPIConnector;

public class TradeTransactionCommand {

    private final SyncAPIConnector connector;

    public TradeTransactionCommand(SyncAPIConnector connector) {
        this.connector = connector;
    }

    public TradeTransactionResponse execute(TradeTransInfoRecord tr) throws APICommandConstructionException, APIReplyParseException, APIErrorResponse, APICommunicationException {
        return APICommandFactory.executeTradeTransactionCommand(connector, tr.getCmd(), tr.getType(), tr.getPrice(), tr.getSl(), tr.getTp(), tr.getSymbol(), tr.getVolume(), tr.getOrder(), tr.getCustomComment(), tr.getExpiration());
    }

}
