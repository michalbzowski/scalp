//package pl.bzowski.tradingbot;
//
//import io.quarkus.test.junit.QuarkusTest;
//import org.json.simple.JSONObject;
//import org.junit.Before;
//import org.junit.Ignore;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.ta4j.core.BarSeries;
//import pl.bzowski.archive.ArchiveCandlesService;
//import pl.bzowski.platform.ExternalPlatform;
//import pl.bzowski.platform.xstation.PlatformAuthorizationService;
//import pl.bzowski.series.SeriesHandler;
//import pl.bzowski.tradingbot.positions.ClosePosition;
//import pl.bzowski.tradingbot.positions.OpenPosition;
//import pl.bzowski.tradingbot.strategies.SimpleSarEma200Strategy;
//import pl.bzowski.tradingbot.strategies.Strategy;
//import pl.bzowski.tradingbot.strategies.StrategyWithLifeCycle;
//import pro.xstore.api.message.codes.PERIOD_CODE;
//import pro.xstore.api.message.error.APICommandConstructionException;
//import pro.xstore.api.message.error.APICommunicationException;
//import pro.xstore.api.message.error.APIReplyParseException;
//import pro.xstore.api.message.records.RateInfoRecord;
//import pro.xstore.api.message.records.SCandleRecord;
//import pro.xstore.api.message.response.APIErrorResponse;
//import pro.xstore.api.message.response.ChartResponse;
//
//import javax.inject.Inject;
//
//import java.io.IOException;
//import java.time.Duration;
//import java.util.List;
//
//import static org.junit.Assert.*;
//
//@QuarkusTest
//@Ignore
//public class TradingBotTest {
//
//    @Inject
//    ArchiveCandlesService archiveCandlesService;
//
//    @Inject
//    PlatformAuthorizationService platformAuthorizationService;
//
//    private static final String SYMBOL = "BITCOIN";
//
//    private static final PERIOD_CODE PC = PERIOD_CODE.PERIOD_M1;
//
//    private static TradingBot tradingBot;
//
//    @Test
//    @Ignore
//    public void onTick() throws APIErrorResponse, APICommunicationException, APIReplyParseException, APICommandConstructionException, IOException {
//        SeriesHandler seriesHandler = new SeriesHandler();
//
//        BarSeries series = seriesHandler.createSeries(SYMBOL);
//        platformAuthorizationService.authorize();
//        var archiveCandles = archiveCandlesService.getArchiveCandles(SYMBOL, PC, Duration.ofMinutes(60 * 6).toMillis());
//        var rateInfos = archiveCandles.getRateInfos();
//        seriesHandler.fillSeries(rateInfos.subList(0, 200), 2, series, PC);
//
//        ExternalPlatform externalPlatform = new TestExternalPlatform(series);
//        OpenPosition openPosition = new OpenPosition(externalPlatform);
//        ClosePosition closePosition = new ClosePosition(externalPlatform);
//
//        var simpleSarEma200Strategy = new SimpleTestStrategy(SYMBOL, openPosition, closePosition, series);
//        tradingBot = new TradingBotBuilder().symbol(SYMBOL).series(series).periodCode(PC).strategy(simpleSarEma200Strategy).build();
//
//        var rateInfoRecords = rateInfos.subList(200, rateInfos.size() - 1);
//        for (RateInfoRecord rir : rateInfoRecords) {
//
//            SCandleRecord sCandleRecord = new SCandleRecord();
//            sCandleRecord.setFieldsFromJSONObject(rir.toCandleJson(SYMBOL));
//            tradingBot.onTick(sCandleRecord);
//        }
//
//    }
//}