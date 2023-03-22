package api.streaming;

public class BalanceStop extends StreamingCommandRecord {

	@Override
	protected String getCommand() {
		return "stopBalance";
	}
}