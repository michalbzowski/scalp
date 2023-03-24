package pl.bzowski.trader;

import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bzowski.ConnectorProvider;
import pl.bzowski.tradingbot.BotService;
import pl.bzowski.tradingbot.TradingBot;
import pl.bzowski.tradingbot.strategies.SimpleSarEma200Strategy;
import pl.bzowski.tradingbot.strategies.StrategyBuilder;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.records.SCandleRecord;
import pro.xstore.api.streaming.StreamingListener;
import pro.xstore.api.sync.StreamingConnector;
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
    ConnectorProvider connectorProvider;

    Map<String, TradingBot> activeBots = new HashMap<>();

    @PostConstruct
    public void startObserveMarket() {
        var executor = vertx.createSharedWorkerExecutor("my-worker", 1);
        var syncAPIConnector = connectorProvider.get();
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

    public void startTrade(String symbol, String strategyName) {
        StrategyBuilder strategyBuilder = getStrategyBuilder(symbol, strategyName);
        var botInstance = botService.createBotInstance(symbol, strategyBuilder);
        activeBots.put(symbol, botInstance);
        var syncAPIConnector = connectorProvider.get();
        try {
            syncAPIConnector.subscribeCandle(symbol);
        } catch (APICommunicationException e) {
            throw new RuntimeException(e);
        }
    }

    private StrategyBuilder getStrategyBuilder(String symbol, String strategyName) {
        return new SimpleSarEma200Strategy(symbol);
    }
}
