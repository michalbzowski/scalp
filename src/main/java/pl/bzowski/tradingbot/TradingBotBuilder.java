package pl.bzowski.tradingbot;

import org.ta4j.core.BarSeries;
import pl.bzowski.tradingbot.strategies.Strategy;
import pro.xstore.api.message.codes.PERIOD_CODE;
import pro.xstore.api.message.error.APICommandConstructionException;

public class TradingBotBuilder {


    private String symbol;
    private BarSeries series;
    private Strategy strategy;
    private PERIOD_CODE periodCode;

    public TradingBotBuilder symbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    public TradingBotBuilder series(BarSeries series) {
        this.series = series;
        return this;
    }

    public TradingBotBuilder strategy(Strategy strategy) {
        this.strategy = strategy;
        return this;
    }

    public TradingBotBuilder periodCode(PERIOD_CODE periodCode) {
        this.periodCode = periodCode;
        return this;
    }

    public TradingBot build() throws APICommandConstructionException {
        return new TradingBotImpl(symbol, series, periodCode, strategy.getLongStrategy(), strategy.getShortStrategy());
    }
}
