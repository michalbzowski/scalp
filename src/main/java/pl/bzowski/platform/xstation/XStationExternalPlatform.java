package pl.bzowski.platform.xstation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bzowski.platform.ExternalPlatform;
import pl.bzowski.platform.ScalpTradeRecord;
import pl.bzowski.tradingbot.commands.SymbolCommand;
import pl.bzowski.tradingbot.commands.TradeTransactionCommand;
import pl.bzowski.tradingbot.commands.TradeTransactionStatusCommand;
import pl.bzowski.tradingbot.commands.TradesCommand;
import pl.bzowski.tradingbot.positions.OpenPosition;
import pl.bzowski.tradingbot.positions.TradeTransactionAbstractResponse;
import pl.bzowski.tradingbot.strategies.StrategyWithLifeCycle;
import pro.xstore.api.message.codes.TRADE_OPERATION_CODE;
import pro.xstore.api.message.codes.TRADE_TRANSACTION_TYPE;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.records.SymbolRecord;
import pro.xstore.api.message.records.TradeRecord;
import pro.xstore.api.message.records.TradeTransInfoRecord;
import pro.xstore.api.message.response.*;

public class XStationExternalPlatform implements ExternalPlatform {
    private final Logger logger = LoggerFactory.getLogger(XStationExternalPlatform.class);
    private final TradeTransactionCommand tradeTransactionCommand;
    private final SymbolCommand symbolCommand;
    private final TradeTransactionStatusCommand tradeTransactionStatusCommand;
    private final TradesCommand tradesCommand;

    public XStationExternalPlatform(TradeTransactionCommand tradeTransactionCommand,
                                    SymbolCommand symbolCommand,
                                    TradeTransactionStatusCommand tradeTransactionStatusCommand,
                                    TradesCommand tradesCommand) {
        this.tradeTransactionCommand = tradeTransactionCommand;
        this.symbolCommand = symbolCommand;
        this.tradeTransactionStatusCommand = tradeTransactionStatusCommand;
        this.tradesCommand = tradesCommand;
    }

    @Override
    public ScalpTradeRecord getOrderId(StrategyWithLifeCycle strategy, int endIndex, TradeTransactionAbstractResponse tradeTransactionResponse) throws APICommandConstructionException, APIReplyParseException, APICommunicationException, APIErrorResponse {
        long transactionOrderId = tradeTransactionResponse.getOrder();
        TradeTransactionStatusResponse statusResponse = tradeTransactionStatusCommand.execute(transactionOrderId);
        TradesResponse tradesResponse = tradesCommand.execute(true);
        for (TradeRecord tradeRecord : tradesResponse.getTradeRecords()) {
            if (tradeRecord.getOrder2() == statusResponse.getOrder()) {
                strategy.positionCreated(tradeRecord.getOrder2(), endIndex, tradeRecord.getOpen_price(), tradeRecord.getVolume());
                return new ScalpTradeRecord(tradeRecord.getOrder2(), tradeRecord.getOpen_price(), tradeRecord.getVolume(), tradeRecord.getClose_price(), tradeRecord.getSymbol(), tradeRecord.getOrder());
            }
        }
        return null;
    }

    @Override
    public TradeTransactionAbstractResponse getTradeTransactionResponse(StrategyWithLifeCycle strategy, double stopLoss) throws APICommandConstructionException, APIReplyParseException, APIErrorResponse, APICommunicationException {
        SymbolRecord symbolRecord = getSymbolRecordFromBroker(symbolCommand, strategy);
        TradeTransInfoRecord tradeRequest = prepareTradeRequest(strategy, symbolRecord, stopLoss);
        TradeTransactionResponse tradeTransactionResponse = tradeTransactionCommand.execute(tradeRequest);
        return new TradeTransactionAbstractResponse(tradeTransactionResponse.getStatus(), tradeTransactionResponse.getOrder());
    }

    @Override
    public ScalpTradeRecord getTradeRecordToClose(StrategyWithLifeCycle strategy) throws APICommandConstructionException, APIReplyParseException, APICommunicationException, APIErrorResponse {
        TradesResponse tradesResponse;
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
        return new ScalpTradeRecord(tradeRecordToClose.getOrder2(), tradeRecordToClose.getOpen_price(), tradeRecordToClose.getVolume(), tradeRecordToClose.getClose_price(), tradeRecordToClose.getSymbol(), tradeRecordToClose.getOrder());
    }

    @Override
    public void closePosition(StrategyWithLifeCycle strategy, int endIndex, ScalpTradeRecord tradeRecordToClose) throws APICommandConstructionException, APIReplyParseException, APIErrorResponse, APICommunicationException {
        TradeTransInfoRecord ttCloseInfoRecord = getTradeTransInfoRecord(strategy, tradeRecordToClose);
        TradeTransactionResponse closeTradeTransactionResponse = tradeTransactionCommand.execute(ttCloseInfoRecord);
        TradeTransactionStatusResponse ttsCloseResponse = tradeTransactionStatusCommand.execute(closeTradeTransactionResponse.getOrder());
        strategy.closePosition(endIndex, tradeRecordToClose.getClose_price(), tradeRecordToClose.getVolume());
        logger.info("Closed: {}", ttsCloseResponse);
    }

    private static TradeTransInfoRecord getTradeTransInfoRecord(StrategyWithLifeCycle strategy, ScalpTradeRecord tradeRecordToClose) {
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
        return ttCloseInfoRecord;
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
        logger.info("New trade request: " + ttOpenInfoRecord);
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
