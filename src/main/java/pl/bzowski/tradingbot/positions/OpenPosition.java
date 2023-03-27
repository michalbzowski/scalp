package pl.bzowski.tradingbot.positions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final TradeTransactionCommand tradeTransactionCommand;
    private final SymbolCommand symbolCommand;
    private final TradeTransactionStatusCommand tradeTransactionStatusCommand;
    private final TradesCommand tradesCommand;

    public OpenPosition(TradeTransactionCommand tradeTransactionCommand,
                        SymbolCommand symbolCommand,
                        TradeTransactionStatusCommand tradeTransactionStatusCommand,
                        TradesCommand tradesCommand) {
        this.tradeTransactionCommand = tradeTransactionCommand;
        this.symbolCommand = symbolCommand;
        this.tradeTransactionStatusCommand = tradeTransactionStatusCommand;
        this.tradesCommand = tradesCommand;
    }

    public synchronized long openPosition(StrategyWithLifeCycle strategy, double stopLoss) {
        if (strategy.isPositionAlreadyOpened()) {
            logger.info("Position is already opened - skiping");
            return strategy.getPositionId();
        }
        try {
            strategy.positionCreatingPending();
            SymbolRecord symbolRecord = getSymbolRecordFromBroker(symbolCommand, strategy);
            TradeTransInfoRecord tradeRequest = prepareTradeRequest(strategy, symbolRecord, stopLoss);
            TradeTransactionResponse tradeTransactionResponse = tradeTransactionCommand.execute(tradeRequest);
            long transactionOrderId = tradeTransactionResponse.getOrder();
            logger.info("Transaction request for {}. Status {}. OrderId: {}", symbolRecord,
                    tradeTransactionResponse.getStatus(), transactionOrderId);
            if (tradeTransactionResponse.getStatus()) {
                TradeTransactionStatusResponse statusResponse = tradeTransactionStatusCommand.execute(transactionOrderId);
                REQUEST_STATUS tradeStatus = statusResponse.getRequestStatus();
                logger.info("Order {} status is: {}. (3 - accepted)", transactionOrderId, tradeStatus.toString());
                TradesResponse tradesResponse = tradesCommand.execute(true);
                for (TradeRecord tradeRecord : tradesResponse.getTradeRecords()) {
                    if (tradeRecord.getOrder2() == statusResponse.getOrder()) {
                        logger.info("Trade position opened at {}. Position number: {}", tradeRecord.getOpen_time(),
                                tradeRecord.getOrder2());
                        strategy.positionCreated(tradeRecord.getOrder2());
                        return tradeRecord.getOrder2();
                    }
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

    private TradeTransInfoRecord prepareTradeRequest(StrategyWithLifeCycle strategy, SymbolRecord symbol, double stopLoss) {
        boolean isLong = strategy.isLong();
        double price = isLong ? symbol.getAsk() : symbol.getBid();
        double sl = 0.0;//stopLoss;// Na razie dam otwierac i zamykac botowi :)
        double tp = 0;
        double volume = symbol.getLotMin();
        long createOrderId = 0;
        String customComment = "Transaction opened by bot";
        long expiration = 0;
        TradeTransInfoRecord ttOpenInfoRecord = new TradeTransInfoRecord(
                isLong ? TRADE_OPERATION_CODE.BUY : TRADE_OPERATION_CODE.SELL,
                TRADE_TRANSACTION_TYPE.OPEN,
                price, sl, tp, strategy.getSymbol(), volume, createOrderId, customComment, expiration);
        logger.info("New transaciton?: " + ttOpenInfoRecord.toString());
        return ttOpenInfoRecord;
    }

    private SymbolRecord getSymbolRecordFromBroker(SymbolCommand symbolCommand, StrategyWithLifeCycle strategy)
            throws APICommandConstructionException, APIReplyParseException, APIErrorResponse,
            APICommunicationException {
        SymbolResponse symbolResponse = symbolCommand.execute(strategy.getSymbol());
        SymbolRecord symbol = symbolResponse.getSymbol();
        return symbol;
    }
}