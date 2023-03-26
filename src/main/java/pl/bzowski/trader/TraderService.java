package pl.bzowski.trader;

import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.BarSeries;
import pl.bzowski.ConnectorProvider;
import pl.bzowski.chart.ChartService;
import pl.bzowski.platform.xstation.PlatformAuthorizationService;
import pl.bzowski.series.SeriesHandler;
import pl.bzowski.tradingbot.BotService;
import pl.bzowski.tradingbot.TradingBot;
import pl.bzowski.tradingbot.strategies.SimpleSarEma200Strategy;
import pl.bzowski.tradingbot.strategies.Strategy;
import pro.xstore.api.message.codes.PERIOD_CODE;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.records.SCandleRecord;
import pro.xstore.api.message.response.APIErrorResponse;
import pro.xstore.api.message.response.ChartResponse;
import pro.xstore.api.streaming.StreamingListener;
import pro.xstore.api.sync.SyncAPIConnector;

import javax.annotation.PostConstruct;
import javax.enterprise.context.BeforeDestroyed;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class TraderService extends StreamingListener {

    private static final Logger logger = LoggerFactory.getLogger(TraderService.class);
    @Inject
    BotService botService;
    @Inject
    Vertx vertx;

    @Inject
    PlatformAuthorizationService platformAuthorizationService;

    @Inject
    ConnectorProvider connectorProvider;


    @Inject
    SeriesHandler seriesHandler;

    @Inject
    ChartService chartService;

    Map<String, TradingBot> activeBots = new HashMap<>();

    @PostConstruct
    public void startObserveMarket() throws APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException {
        var executor = vertx.createSharedWorkerExecutor("my-worker", 1);
        var syncAPIConnector = connectorProvider.get();
        if (!platformAuthorizationService.authorize()) {
            logger.error("WTF?");
        }
        executor.<String>executeBlocking(promise -> {
            try {
                syncAPIConnector.connectStream(this);
                activeBots.keySet().forEach(symbol -> {
                    try {
                        syncAPIConnector.subscribeCandle(symbol);
                    } catch (APICommunicationException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (IOException | APICommunicationException e) {
                logger.error(e.getLocalizedMessage());
            }
        });
    }

    @BeforeDestroyed(value = Singleton.class)
    public void preDestroy() {
        var syncAPIConnector = connectorProvider.get();
        activeBots.keySet().forEach(symbol -> {
            try {
                syncAPIConnector.unsubscribeCandle(symbol);
            } catch (APICommunicationException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void receiveCandleRecord(SCandleRecord candleRecord) {
        activeBots.values()
                .forEach(ab -> ab.onTick(candleRecord));
    }

    public void startTrade(String symbol, String strategyName, PERIOD_CODE periodCode) throws APIErrorResponse, APICommunicationException, APIReplyParseException, APICommandConstructionException {
        var syncAPIConnector = connectorProvider.get();

        BarSeries barSeries = seriesHandler.createSeries(symbol);
        Strategy strategy = getStrategy(symbol, strategyName, syncAPIConnector, barSeries);
        ChartResponse chartResponse = getArchiveCandles(symbol, periodCode, strategy.candlesOfMillisArchive());
        seriesHandler.fillSeries(chartResponse.getRateInfos(), chartResponse.getDigits(), barSeries, periodCode);

        var botInstance = botService.createBotInstance(symbol, strategy, periodCode, barSeries);
        activeBots.put(symbol, botInstance);

        try {
            syncAPIConnector.subscribeCandle(symbol);
        } catch (APICommunicationException e) {
            throw new RuntimeException(e);
        }
    }

    private Strategy getStrategy(String symbol, String strategyName, SyncAPIConnector syncAPIConnector, BarSeries barSeries) {
        return new SimpleSarEma200Strategy(symbol, syncAPIConnector, barSeries);
    }

    private ChartResponse getArchiveCandles(String symbol, PERIOD_CODE periodCode, long durationOfMillis)
            throws APIErrorResponse, APICommunicationException, APIReplyParseException,
            APICommandConstructionException {
        return chartService.getChartForPeriodFromNow(symbol, periodCode, durationOfMillis);

    }
}
