package pl.bzowski.platform;

import pl.bzowski.tradingbot.positions.TradeTransactionAbstractResponse;
import pl.bzowski.tradingbot.strategies.StrategyWithLifeCycle;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.records.TradeRecord;
import pro.xstore.api.message.response.APIErrorResponse;

public interface ExternalPlatform {
    ScalpTradeRecord getOrderId(StrategyWithLifeCycle strategy, int endIndex, TradeTransactionAbstractResponse tradeTransactionAbstractResponse) throws APICommandConstructionException, APIReplyParseException, APICommunicationException, APIErrorResponse;

    TradeTransactionAbstractResponse getTradeTransactionResponse(StrategyWithLifeCycle strategy, double stopLoss) throws APICommandConstructionException, APIReplyParseException, APIErrorResponse, APICommunicationException;

    ScalpTradeRecord getTradeRecordToClose(StrategyWithLifeCycle strategy) throws APICommandConstructionException, APIReplyParseException, APICommunicationException, APIErrorResponse;

    void closePosition(StrategyWithLifeCycle strategy, int endIndex, ScalpTradeRecord tradeRecordToClose) throws APICommandConstructionException, APIReplyParseException, APIErrorResponse, APICommunicationException;
}
