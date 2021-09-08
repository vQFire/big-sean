package Observers;

import java.util.ArrayList;
import java.util.Map;

public interface TradeObservable {
    public Map<String, ArrayList<String>> getTradeStatus();
}
