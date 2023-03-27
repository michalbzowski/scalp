package pl.bzowski.tradingbot.positions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bzowski.tradingbot.commands.TradeTransactionCommand;
import pl.bzowski.tradingbot.commands.TradeTransactionStatusCommand;
import pl.bzowski.tradingbot.commands.TradesCommand;
import pl.bzowski.tradingbot.strategies.StrategyWithLifeCycle;
import pro.xstore.api.message.codes.TRADE_OPERATION_CODE;
import pro.xstore.api.message.codes.TRADE_TRANSACTION_TYPE;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.records.TradeRecord;
import pro.xstore.api.message.records.TradeTransInfoRecord;
import pro.xstore.api.message.response.APIErrorResponse;
import pro.xstore.api.message.response.TradeTransactionResponse;
import pro.xstore.api.message.response.TradeTransactionStatusResponse;
import pro.xstore.api.message.response.TradesResponse;

public class ClosePosition {

    Logger logger = LoggerFactory.getLogger(ClosePosition.class);
    private final TradesCommand tradesCommand;
    private final TradeTransactionCommand tradeTransactionCommand;
    private final TradeTransactionStatusCommand tradeTransactionStatusCommand;

    public ClosePosition(TradesCommand tradesCommand, TradeTransactionCommand tradeTransactionCommand, TradeTransactionStatusCommand tradeTransactionStatusCommand) {
        this.tradesCommand = tradesCommand;
        this.tradeTransactionCommand = tradeTransactionCommand;
        this.tradeTransactionStatusCommand = tradeTransactionStatusCommand;
    }


    public synchronized long closePosition(StrategyWithLifeCycle strategy) {
        TradesResponse tradesResponse;
        try {
            logger.info("Looking for opened transactions");
            tradesResponse = tradesCommand.execute(true);
            logger.info("TradesResponse: " + tradesResponse.toString());
            TradeRecord tradeRecordToClose = null;
            logger.info("Entering to close loop");
            for (TradeRecord tradeRecord : tradesResponse.getTradeRecords()) {
                logger.info(tradeRecord.toString());
                var order2 = tradeRecord.getOrder2();
                var canBeClosed = strategy.canBeClosed(order2);
                logger.info("Order id " + order2 + "Can be closed: " + canBeClosed);
                if (canBeClosed) {
                    tradeRecordToClose = tradeRecord;
                }
            }
            if (tradeRecordToClose == null) {
                return 0;// If is not found I consder position was closed by SL / TP
            }
            double price = tradeRecordToClose.getClose_price();
            double sl = 0.0;
            double tp = 0.0;
            String symbol = tradeRecordToClose.getSymbol();
            double volume = tradeRecordToClose.getVolume();
            long order = tradeRecordToClose.getOrder();
            String customComment = "Closed by bot";
            long expiration = 0;
            TradeTransInfoRecord ttCloseInfoRecord = new TradeTransInfoRecord(
                    strategy.isShort() ? TRADE_OPERATION_CODE.SELL : TRADE_OPERATION_CODE.BUY,
                    TRADE_TRANSACTION_TYPE.CLOSE,
                    price, sl, tp, symbol, volume, order, customComment, expiration);
            TradeTransactionResponse closeTradeTransactionResponse;

            closeTradeTransactionResponse = tradeTransactionCommand.execute(ttCloseInfoRecord);
            TradeTransactionStatusResponse ttsCloseResponse;
            ttsCloseResponse = tradeTransactionStatusCommand.execute(closeTradeTransactionResponse.getOrder());
            strategy.closePosition();
            logger.info("Closed: {}", ttsCloseResponse);
        } catch (APIErrorResponse | APICommandConstructionException | APIReplyParseException
                | APICommunicationException e1) {
            logger.error("Closing position {} failed: {}", "xxx", e1);
            return 0;
        }
        return 0;
    }
}