package it.trade.android.sdk.enums


enum class TradeItOrderPriceType private constructor(val priceTypeValue: String) {
    MARKET("market"),
    LIMIT("limit"),
    STOP_MARKET("stopMarket"),
    STOP_LIMIT("stopLimit");


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
