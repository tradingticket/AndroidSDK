package it.trade.android.sdk.enums;

public enum TradeItOrderExpiration {
    GOOD_FOR_DAY("day"),
    GOOD_UNTIL_CANCELED("gtc");

    private String expirationValue;

    TradeItOrderExpiration(String expirationValue) {
        this.expirationValue = expirationValue;
    }

    public String getExpirationValue() {
        return expirationValue;
    }


    public static TradeItOrderExpiration getExpirationForValue(String actionValue) {
        for (TradeItOrderExpiration expiration: TradeItOrderExpiration.values()) {
            if (expiration.expirationValue.equals(actionValue)) {
                return expiration;
            }
        }
        return null;
    }
}
