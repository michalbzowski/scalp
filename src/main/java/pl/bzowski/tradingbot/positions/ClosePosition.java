package pl.bzowski.tradingbot.positions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bzowski.platform.ExternalPlatform;
import pl.bzowski.platform.ScalpTradeRecord;
import pl.bzowski.tradingbot.commands.TradeTransactionCommand;
import pl.bzowski.tradingbot.commands.TradeTransactionStatusCommand;
import pl.bzowski.tradingbot.commands.TradesCommand;
import pl.bzowski.tradingbot.strategies.StrategyWithLifeCycle;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.records.TradeRecord;
import pro.xstore.api.message.response.APIErrorResponse;
import pro.xstore.api.message.response.TradesResponse;

public class ClosePosition {

    private final ExternalPlatform externalPlatform;
    Logger logger = LoggerFactory.getLogger(ClosePosition.class);

    public ClosePosition(ExternalPlatform externalPlatform) {
        this.externalPlatform = externalPlatform;
    }


    public synchronized long closePosition(StrategyWithLifeCycle strategy, int endIndex) {
        TradesResponse tradesResponse;
        try {
            ScalpTradeRecord tradeRecordToClose = externalPlatform.getTradeRecordToClose(strategy);
            if (tradeRecordToClose == null) {
                return 0;// If is not found I consder position was closed by SL / TP
            }
            externalPlatform.closePosition(strategy, endIndex, tradeRecordToClose);
        } catch (APIErrorResponse | APICommandConstructionException | APIReplyParseException
                 | APICommunicationException e1) {
            logger.error("Closing position {} failed: {}", "xxx", e1);
            return 0;
        }
        return 0;
    }


}