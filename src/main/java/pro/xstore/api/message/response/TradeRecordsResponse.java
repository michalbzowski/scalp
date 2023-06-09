package pro.xstore.api.message.response;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.records.TradeRecord;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TradeRecordsResponse extends BaseResponse {
    
	private List<TradeRecord> tradeRecords = new LinkedList<TradeRecord>();

    @SuppressWarnings("rawtypes")
	public TradeRecordsResponse(String body) throws APIReplyParseException, APIErrorResponse {
        super(body);
        JSONArray arr = (JSONArray) this.getReturnData();
        for (Iterator it = arr.iterator(); it.hasNext();) {
            JSONObject e = (JSONObject) it.next();
            TradeRecord record = new TradeRecord();
            record.setFieldsFromJSONObject(e);
            tradeRecords.add(record);
        }
    }

    public List<TradeRecord> getTradeRecords() {
        return tradeRecords;
    }

    @Override
    public String toString() {
        return "TradeRecordsResponse{" + "tradeRecords=" + tradeRecords + '}';
    }
}
