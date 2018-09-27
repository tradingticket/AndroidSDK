package it.trade.android.sdk.enums

enum class TradeItOrderExpirationType private constructor(val expirationValue: String) {
    GOOD_FOR_DAY("day"),
    GOOD_UNTIL_CANCELED("gtc");


    companion object {


        fun getExpirationForValue(actionValue: String): TradeItOrderExpirationType? {
            for (expiration in TradeItOrderExpirationType.values()) {
                if (expiration.expirationValue == actionValue) {
                    return expiration
                }
            }
            return null
        }
    }
}
