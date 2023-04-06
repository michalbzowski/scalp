package pl.bzowski.tradingbot;

import org.json.simple.JSONObject;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import pl.bzowski.platform.ExternalPlatform;
import pl.bzowski.platform.ScalpTradeRecord;
import pl.bzowski.tradingbot.positions.TradeTransactionAbstractResponse;
import pl.bzowski.tradingbot.strategies.StrategyWithLifeCycle;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.records.TradeRecord;
import pro.xstore.api.message.response.APIErrorResponse;

public class TestExternalPlatform implements ExternalPlatform {

    private TradeTransactionAbstractResponse tradeTransactionAbstractResponse = new TradeTransactionAbstractResponse(true, 1);
    private BarSeries series;

    private ScalpTradeRecord scalpTradeRecord;

    public TestExternalPlatform(BarSeries series) {
        this.series = series;
    }

    @Override
    public ScalpTradeRecord getOrderId(StrategyWithLifeCycle strategy, int endIndex, TradeTransactionAbstractResponse tradeTransactionAbstractResponse) throws APICommandConstructionException, APIReplyParseException, APICommunicationException, APIErrorResponse {
        var order = tradeTransactionAbstractResponse.getOrder();
        this.scalpTradeRecord = new ScalpTradeRecord(order, series.getBar(endIndex).getOpenPrice().doubleValue(), 0.1d, series.getBar(endIndex).getClosePrice().doubleValue(), series.getName(), order);
        return scalpTradeRecord;
    }

    @Override
    public TradeTransactionAbstractResponse getTradeTransactionResponse(StrategyWithLifeCycle strategy, double stopLoss, double takeProfit) throws APICommandConstructionException, APIReplyParseException, APIErrorResponse, APICommunicationException {
        return tradeTransactionAbstractResponse;
    }

    @Override
    public ScalpTradeRecord getTradeRecordToClose(StrategyWithLifeCycle strategy) throws APICommandConstructionException, APIReplyParseException, APICommunicationException, APIErrorResponse {
        return scalpTradeRecord;
    }

    @Override
    public void closePosition(StrategyWithLifeCycle strategy, int endIndex, ScalpTradeRecord tradeRecordToClose) throws APICommandConstructionException, APIReplyParseException, APIErrorResponse, APICommunicationException {
        this.tradeTransactionAbstractResponse = new TradeTransactionAbstractResponse(false, 0);
    }
}
