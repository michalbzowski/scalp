package pl.bzowski.tradingbot.commands;

import pro.xstore.api.message.command.APICommandFactory;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.response.APIErrorResponse;
import pro.xstore.api.message.response.TradeTransactionStatusResponse;
import pro.xstore.api.sync.SyncAPIConnector;

public class TradeTransactionStatusCommand {

    private final SyncAPIConnector connector;

    public TradeTransactionStatusCommand(SyncAPIConnector connector) {
        this.connector = connector;
    }

    public TradeTransactionStatusResponse execute(long transactionOrderId) throws APICommandConstructionException, APIReplyParseException, APICommunicationException, APIErrorResponse {
        return APICommandFactory.executeTradeTransactionStatusCommand(connector, transactionOrderId);
    }

}
