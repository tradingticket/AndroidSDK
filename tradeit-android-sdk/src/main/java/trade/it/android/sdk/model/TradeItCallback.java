package trade.it.android.sdk.model;


public interface TradeItCallback<T> {

    void onSuccess(T type);

    void onError(TradeItErrorResult error);
}
