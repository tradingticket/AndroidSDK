package it.trade.android.sdk.enums;

public enum TradeItOrderExpirationType {
    GOOD_FOR_DAY("day"),
    GOOD_UNTIL_CANCELED("gtc");

    private String expirationValue;

    TradeItOrderExpirationType(String expirationValue) {
        this.expirationValue = expirationValue;
    }

    public String getExpirationValue() {
        return expirationValue;
    }


    public static TradeItOrderExpirationType getExpirationForValue(String actionValue) {
        for (TradeItOrderExpirationType expiration: TradeItOrderExpirationType.values()) {
            if (expiration.expirationValue.equals(actionValue)) {
                return expiration;
            }
        }
        return null;
    }
}
