package pl.bzowski.symbol;

import pro.xstore.api.message.codes.PERIOD_CODE;
import pro.xstore.api.message.records.SymbolRecord;

import java.util.Collection;

public class SymbolViewModel {
    public Collection<SymbolRecord> pairs;
    public PERIOD_CODE[] periodCodes;
}