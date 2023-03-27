package pl.bzowski.tradingbot;

import org.ta4j.core.BarSeries;
import pl.bzowski.chart.ChartService;
import pl.bzowski.series.SeriesHandler;
import pl.bzowski.tradingbot.strategies.Strategy;
import pro.xstore.api.message.codes.PERIOD_CODE;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.response.APIErrorResponse;
import pro.xstore.api.message.response.ChartResponse;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class BotService {

    public TradingBot createBotInstance(String symbol, Strategy strategy, PERIOD_CODE periodCode, BarSeries barSeries) {
        try {
            TradingBotBuilder tradingBotBuilder = new TradingBotBuilder();

            return tradingBotBuilder.symbol(symbol).series(barSeries).periodCode(periodCode).strategy(strategy).build();
        } catch (APICommandConstructionException apiErrorResponse) {
            apiErrorResponse.printStackTrace();
        }
        return null;
    }


}
