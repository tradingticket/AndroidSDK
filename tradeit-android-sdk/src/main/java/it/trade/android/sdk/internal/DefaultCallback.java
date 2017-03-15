package it.trade.android.sdk.internal;

import it.trade.android.sdk.model.TradeItCallback;
import it.trade.android.sdk.model.TradeItErrorResult;
import it.trade.tradeitapi.model.TradeItResponse;
import it.trade.tradeitapi.model.TradeItResponseStatus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * This class handles default callback behaviors (to use inside the sdk)
 * @param <TradeItResponseType> the trade it response type
 * @param <TradeItCallBackType> the type we want to return to the user in the TradeItCallBack
 */
public abstract class DefaultCallback<TradeItResponseType, TradeItCallBackType> implements Callback<TradeItResponseType> {

    protected TradeItCallback<TradeItCallBackType> callback;

    protected DefaultCallback(TradeItCallback<TradeItCallBackType> callback) {
        this.callback = callback;
    }

    @Override
    public void onResponse(Call<TradeItResponseType> call, Response<TradeItResponseType> response) {
        if (response.isSuccessful()) {
            TradeItResponseType responseType = response.body();
            TradeItResponse tradeItResponse = responseType instanceof TradeItResponse ? (TradeItResponse) responseType : null;
            if (tradeItResponse == null) {
                onErrorResponse(new TradeItErrorResult());
            } else if (tradeItResponse.status == TradeItResponseStatus.SUCCESS) {
                onSuccessResponse(response);
            } else {
                onErrorResponse(new TradeItErrorResult(tradeItResponse.code, tradeItResponse.shortMessage, tradeItResponse.longMessages));
            }
        } else {
            onErrorResponse(new TradeItErrorResult(response.code()));
        }
    }

    @Override
    public void onFailure(Call<TradeItResponseType> call, Throwable t) {
        onErrorResponse(new TradeItErrorResult("Network exception occurred", t.getMessage()));
    }

    public abstract void onSuccessResponse(Response<TradeItResponseType> response);
    public abstract void onErrorResponse(TradeItErrorResult errorResult);

}

