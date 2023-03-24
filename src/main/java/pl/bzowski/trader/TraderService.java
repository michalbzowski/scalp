package pl.bzowski.trader;

import io.vertx.core.Vertx;
import pl.bzowski.tradingbot.BotService;
import pl.bzowski.tradingbot.TradingBot;
import pl.bzowski.tradingbot.strategies.SimpleSarEma200Strategy;
import pl.bzowski.tradingbot.strategies.StrategyBuilder;
import pro.xstore.api.message.error.APICommunicationException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class TraderService {

    @Inject
    BotService botService;
    @Inject
    Vertx vertx;

    Map<String, TradingBot> activeBots = new HashMap<>();

    @PostConstruct
    public void startObserveMarket() {
        var executor = vertx.createSharedWorkerExecutor("my-worker", 1);
        executor.<String>executeBlocking(promise -> {
            TradeBotStreamListener tradeBotStreamListener = new TradeBotStreamListener(strategies, minuteSeriesHandler);
            try {
                connector.connectStream(tradeBotStreamListener);
                strategies.forEach((key, value) -> {
                    try {
                        connector.subscribeCandle(key);
                    } catch (APICommunicationException e) {
                        logger.error(e.getLocalizedMessage());
                    }

                });
            } catch (IOException | APICommunicationException e) {
                logger.error(e.getLocalizedMessage());
            }
        });
    }

    public void startTrade(String symbol, String strategyName) {
        StrategyBuilder strategyBuilder = getStrategyBuilder(symbol, strategyName);
        var botInstance = botService.createBotInstance(symbol, strategyBuilder);
        activeBots.put(symbol, botInstance);
    }

    private StrategyBuilder getStrategyBuilder(String symbol, String strategyName) {
        return new SimpleSarEma200Strategy(symbol);
    }
}
