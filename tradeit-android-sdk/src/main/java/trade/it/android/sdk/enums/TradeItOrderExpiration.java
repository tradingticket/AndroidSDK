package trade.it.android.sdk.enums;

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
}
