package pl.bzowski.tradingbot.strategies;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.*;
import pl.bzowski.tradingbot.commands.SymbolCommand;
import pl.bzowski.tradingbot.commands.TradeTransactionCommand;
import pl.bzowski.tradingbot.commands.TradeTransactionStatusCommand;
import pl.bzowski.tradingbot.commands.TradesCommand;
import pl.bzowski.tradingbot.positions.ClosePosition;
import pl.bzowski.tradingbot.positions.OpenPosition;
import pl.bzowski.tradingbot.states.*;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.sync.SyncAPIConnector;

import java.util.Arrays;

public class StrategyWithLifeCycle extends BaseStrategy {

    private Strategy strategy;
    private final Indicator[] indicators;
    private final String symbol;
    private final TradingRecord tradingRecord = new BaseTradingRecord();
    private final OpenPosition openPosition;
    private final ClosePosition closePosition;
    public Logger logger = LoggerFactory.getLogger(StrategyWithLifeCycle.class);
    private PositionState positionState = new PositionClosed();

    public StrategyWithLifeCycle(String name, String symbol, Rule entryRule, Rule exitRule, SyncAPIConnector connector, Strategy strategy, Indicator... indicators) throws APICommandConstructionException {
        super(name, entryRule, exitRule);
        this.symbol = symbol;
        this.strategy = strategy;
        this.indicators = indicators;
        TradeTransactionCommand tradeTransactionCommand = new TradeTransactionCommand(connector);
        SymbolCommand symbolCommand = new SymbolCommand(connector);
        TradeTransactionStatusCommand tradeTransactionStatusCommand = new TradeTransactionStatusCommand(connector);
        TradesCommand tradesCommand = new TradesCommand(connector);
        this.openPosition = new OpenPosition(tradeTransactionCommand, symbolCommand, tradeTransactionStatusCommand, tradesCommand);
        this.closePosition = new ClosePosition(tradesCommand, tradeTransactionCommand, tradeTransactionStatusCommand);
    }

    @Override
    public boolean shouldEnter(int index) {
        logger.info("Should enter at index {}?", index);
        if (positionState.isOpened()) {
            logger.info("- No - position is already opened {}", positionState.isOpened());
            return false;
        }
        boolean shouldEnter = super.shouldEnter(index);
        logger.info("- Strategy {} should enter: {}. Indicators:", getName(), shouldEnter);
        Arrays.stream(indicators)
                .forEach(i -> logger.debug("Indicator {} - value: {}", i.getClass(), i.getValue(index)));
        return shouldEnter;
    }

    public Indicator getIndicator(Class<? extends Indicator> indicatorClass) {
        return Arrays.stream(indicators).filter(indicator -> indicator.getClass().equals(indicatorClass)).findFirst()
                .orElseThrow();
    }

    public void positionCreatingPending() {
        logger.info("- position pending");
        this.positionState = new PositionCreatingPending();
    }

    public void positionCreatingFailed() {
        logger.info("- position creating failed");
        this.positionState = new PositionCreatingFailed();
    }

    public void positionCreated(long positionId) {
        logger.info("- position created");
        this.positionState = new PositionCreated(positionId);
    }

    public boolean canBeClosed(long positionId) {
        boolean canBeClosed = positionState.canBeClosed(positionId);
        logger.info("- position {} can be closed {}?", positionId, canBeClosed);
        return canBeClosed;
    }

    public void closePosition() {
        logger.info("- position closed now");
        this.closePosition.closePosition(this);
        this.positionState = new PositionClosed();
    }

    public long getPositionId() {
        if (positionState.isOpened()) {
            return positionState.getPositionId();
        }
        return 0;
    }

    public boolean isPositionAlreadyOpened() {
        return positionState.isOpened();
    }

    public TradingRecord getTradingRecord() {
        return tradingRecord;
    }

    public String getSymbol() {
        return symbol;
    }

    public boolean isLong() {
        return getName().contains("LONG");
    }

    public boolean isShort() {
        return getName().contains("SHORT");
    }

    public void manage(int endIndex) {
        if (isPositionAlreadyOpened()) {
            if (shouldExit(endIndex)) {
                closePosition();
            }
        } else {
            if (shouldEnter(endIndex) && !isPositionAlreadyOpened()) {
                enterPosition(endIndex, strategy.stoplossValue(endIndex));
            }
        }
    }

    private void enterPosition(int endIndex, double stopLoss) {
        logger.info("- opening position");
        positionCreatingPending();
        PostitionOpeningStatus status = openPositionAtPlatform(stopLoss);
        if (status.isOpened()) {
            positionCreated(status.positionId());
        }
    }

    protected PostitionOpeningStatus openPositionAtPlatform(double stopLoss) {
        var id = this.openPosition.openPosition(this, stopLoss);
        logger.info("OpenPosition service returned id: " + id);
        if (id > 0) {
            return new PostitionOpeningStatus(id, true);
        } else {
            return new PostitionOpeningStatus(0, false);
        }
    }


}
