package pro.xstore.api.streaming;

import pro.xstore.api.message.records.*;

public interface StreamingListenerInterface {
    void receiveTradeRecord(STradeRecord tradeRecord);
    void receiveTickRecord(STickRecord tickRecord);
    void receiveBalanceRecord(SBalanceRecord balanceRecord);
    void receiveNewsRecord(SNewsRecord newsRecord);
    void receiveTradeStatusRecord(STradeStatusRecord tradeStatusRecord);
    void receiveProfitRecord(SProfitRecord profitRecord);
    void receiveKeepAliveRecord(SKeepAliveRecord keepAliveRecord);
    void receiveCandleRecord(SCandleRecord candleRecord);
}