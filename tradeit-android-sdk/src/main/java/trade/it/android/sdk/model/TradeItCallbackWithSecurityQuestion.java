package trade.it.android.sdk.model;

public interface TradeItCallbackWithSecurityQuestion<T> extends TradeItCallback<T>  {
    void onSecurityQuestion(TradeItSecurityQuestion securityQuestion);
}
