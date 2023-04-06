package pl.bzowski.tradingbot.positions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bzowski.platform.ExternalPlatform;
import pl.bzowski.platform.ScalpTradeRecord;
import pl.bzowski.tradingbot.commands.SymbolCommand;
import pl.bzowski.tradingbot.commands.TradeTransactionCommand;
import pl.bzowski.tradingbot.commands.TradeTransactionStatusCommand;
import pl.bzowski.tradingbot.commands.TradesCommand;
import pl.bzowski.tradingbot.strategies.StrategyWithLifeCycle;
import pro.xstore.api.message.codes.REQUEST_STATUS;
import pro.xstore.api.message.codes.TRADE_OPERATION_CODE;
import pro.xstore.api.message.codes.TRADE_TRANSACTION_TYPE;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.records.SymbolRecord;
import pro.xstore.api.message.records.TradeRecord;
import pro.xstore.api.message.records.TradeTransInfoRecord;
import pro.xstore.api.message.response.*;

public class OpenPosition {

    private final Logger logger = LoggerFactory.getLogger(OpenPosition.class);


    private final ExternalPlatform externalPlatform;

    public OpenPosition(ExternalPlatform externalPlatform) {
        this.externalPlatform = externalPlatform;
    }

    public synchronized long openPosition(StrategyWithLifeCycle strategy, double stopLoss, int endIndex, double takeProfit) {
        if (strategy.isPositionAlreadyOpened()) {
            logger.info("Position is already opened - skiping");
            return strategy.getPositionId();
        }
        try {
            strategy.positionCreatingPending();
            TradeTransactionAbstractResponse tradeTransactionAbstractResponse = externalPlatform.getTradeTransactionResponse(strategy, stopLoss, takeProfit);
            if (tradeTransactionAbstractResponse.getStatus()) {
                ScalpTradeRecord tradeRecord = externalPlatform.getOrderId(strategy, endIndex, tradeTransactionAbstractResponse);
                if (tradeRecord != null && tradeRecord.getOrder2() > 0) {
                    strategy.positionCreated(tradeRecord.getOrder2(), endIndex, tradeRecord.getOpen_price(), tradeRecord.getVolume());
                    return tradeRecord.getOrder2();
                }
            }
        } catch (APICommandConstructionException | APIReplyParseException | APIErrorResponse
                 | APICommunicationException e) {
            logger.error(e.getLocalizedMessage());
            strategy.positionCreatingFailed();
            return 0;
        }
        logger.error("Open position error - Transaction request not true or trade not found");
        strategy.positionCreatingFailed();
        return 0;
    }


}