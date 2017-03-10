package it.trade.android.sdk.exceptions;

public class TradeItSDKConfigurationException extends RuntimeException {

    public TradeItSDKConfigurationException(String detailMessage) {
        super(detailMessage);
    }

    public TradeItSDKConfigurationException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
