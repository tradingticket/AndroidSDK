package it.trade.android.sdk.enums;


public enum TradeItOrderAction {
    BUY("buy"),
    SELL("sell"),
    BUY_TO_COVER("buyTocover"),
    SELL_SHORT("sellShort");

    private String actionValue;

    TradeItOrderAction(String actionValue) {
        this.actionValue = actionValue;
    }

    public String getActionValue() {
        return actionValue;
    }
}
