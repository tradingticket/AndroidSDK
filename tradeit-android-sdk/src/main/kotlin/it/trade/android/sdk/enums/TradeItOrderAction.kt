package it.trade.android.sdk.enums

enum class TradeItOrderAction private constructor(val actionValue: String) {
    BUY("buy"),
    SELL("sell"),
    BUY_TO_COVER("buyToCover"),
    SELL_SHORT("sellShort");


    companion object {

        fun getActionForValue(actionValue: String): TradeItOrderAction? {
            for (action in TradeItOrderAction.values()) {
                if (action.actionValue == actionValue) {
                    return action
                }
            }
            return null
        }
    }
}
