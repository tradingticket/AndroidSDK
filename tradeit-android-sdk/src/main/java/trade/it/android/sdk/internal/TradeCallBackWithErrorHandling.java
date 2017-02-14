package trade.it.android.sdk.internal;


import it.trade.tradeitapi.model.TradeItResponse;
import it.trade.tradeitapi.model.TradeItResponseStatus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import trade.it.android.sdk.model.TradeItCallback;
import trade.it.android.sdk.model.TradeItErrorResult;

public abstract class TradeCallBackWithErrorHandling <TradeItResponseType, TradeItCallBackType> implements Callback<TradeItResponseType> {

    private TradeItCallback<TradeItCallBackType> callback;

    protected TradeCallBackWithErrorHandling(TradeItCallback<TradeItCallBackType> callback) {
        this.callback = callback;
    }

    @Override
    public void onResponse(Call<TradeItResponseType> call, Response<TradeItResponseType> response) {
        if (response.isSuccessful()) {
            TradeItResponseType responseType = response.body();
            TradeItResponse tradeItResponse = responseType instanceof TradeItResponse ? (TradeItResponse) responseType : null;
            if (tradeItResponse == null) {
                callback.onError(new TradeItErrorResult());
            } else if (tradeItResponse.status == TradeItResponseStatus.REVIEW_ORDER) {
                onSuccessResponse(response);
            } else {
                callback.onError(new TradeItErrorResult(tradeItResponse.code, tradeItResponse.shortMessage, tradeItResponse.longMessages));
            }
        } else {
            callback.onError(new TradeItErrorResult(response.code()));
        }
    }

    @Override
    public void onFailure(Call<TradeItResponseType> call, Throwable t) {
        callback.onError(new TradeItErrorResult("Network exception occurred", t.getMessage()));
    }

    public abstract void onSuccessResponse(Response<TradeItResponseType> response);
}
