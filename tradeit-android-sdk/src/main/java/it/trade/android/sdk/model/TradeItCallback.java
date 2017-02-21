package it.trade.android.sdk.model;


public interface TradeItCallback<T> {

    void onSuccess(T type);

    void onError(TradeItErrorResult error);
}
