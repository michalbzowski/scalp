package pl.bzowski.tradingbot.strategies;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.BarSeries;
import pro.xstore.api.message.error.APICommandConstructionException;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class StrategiesFactory {

    private static final Logger logger = LoggerFactory.getLogger(StrategiesFactory.class);

    /*
     * Strategy Nameing Convention
     * 1. Prefix
     * 2. Long or Short
     * 3. Indicators
     * 4. Strategy
     * eg SimpleLongSarEma200Strategy
     * or AgressiveShortSarEma200Strategy
     */
    private static final Map<Class<? extends StrategyBuilder>, LongShortStrategyPair> instances = new HashMap<>();

    public static LongShortStrategyPair create(Class<? extends StrategyBuilder> strategyBuilderClass, BarSeries barSeries) {
        if (instances.containsKey(strategyBuilderClass)) {
            return instances.get(strategyBuilderClass);
        } else {
            StrategyBuilder newInstance;
            try {
                newInstance = strategyBuilderClass.getConstructor().newInstance();
                StrategyWithLifeCycle longStrategy = newInstance.getLongStrategy(barSeries);
                StrategyWithLifeCycle shortStrategy = newInstance.getShortStrategy(barSeries);
                LongShortStrategyPair value = new LongShortStrategyPair(longStrategy, shortStrategy);
                instances.put(strategyBuilderClass, value);
                return value;
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException |
                     InvocationTargetException
                     | NoSuchMethodException | SecurityException e) {
                logger.error("Crateing strategy instance error", e.getLocalizedMessage());
                return null;
            } catch (APICommandConstructionException e) {
                throw new RuntimeException(e);
            }
        }
    }
}