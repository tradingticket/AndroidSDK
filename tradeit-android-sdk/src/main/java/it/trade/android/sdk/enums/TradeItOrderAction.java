package it.trade.android.sdk.enums;

public enum TradeItOrderAction {
    BUY("buy"),
    SELL("sell"),
    BUY_TO_COVER("buyToCover"),
    SELL_SHORT("sellShort");

    private String actionValue;

    TradeItOrderAction(String actionValue) {
        this.actionValue = actionValue;
    }

    public String getActionValue() {
        return actionValue;
    }

    public static TradeItOrderAction getActionForValue(String actionValue) {
        for (TradeItOrderAction action: TradeItOrderAction.values()) {
            if (action.actionValue.equals(actionValue)) {
                return action;
            }
        }
        return null;
    }
}
