package it.trade.android.sdk.enums


enum class TradeItOrderPriceType private constructor(val priceTypeValue: String) {
    MARKET("market"),
    LIMIT("limit"),
    STOP_MARKET("stopLimit"),
    STOP_LIMIT("stopMarket");


    companion object {

        fun getPriceTypeForValue(actionValue: String): TradeItOrderPriceType? {
            for (priceType in TradeItOrderPriceType.values()) {
                if (priceType.priceTypeValue == actionValue) {
                    return priceType
                }
            }
            return null
        }
    }
}
