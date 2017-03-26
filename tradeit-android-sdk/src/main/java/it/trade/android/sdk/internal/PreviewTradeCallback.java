package it.trade.android.sdk.internal;


import it.trade.android.sdk.model.TradeItErrorResult;
import it.trade.tradeitapi.model.TradeItResponse;
import it.trade.tradeitapi.model.TradeItResponseStatus;
import retrofit2.Call;
import retrofit2.Response;
import it.trade.android.sdk.model.TradeItCallback;

public abstract class PreviewTradeCallback<TradeItResponseType, TradeItCallBackType> extends DefaultCallback<TradeItResponseType, TradeItCallBackType> {

    protected PreviewTradeCallback(TradeItCallback<TradeItCallBackType> callback) {
        super(callback);
    }

    @Override
    public void onResponse(Call<TradeItResponseType> call, Response<TradeItResponseType> response) {
        if (response.isSuccessful()) {
            TradeItResponseType responseType = response.body();
            TradeItResponse tradeItResponse = responseType instanceof TradeItResponse ? (TradeItResponse) responseType : null;
            if (tradeItResponse == null) {
                onErrorResponse(new TradeItErrorResult());
            } else if (tradeItResponse.status == TradeItResponseStatus.REVIEW_ORDER) {
                onSuccessResponse(response);
            } else {
                onErrorResponse(new TradeItErrorResult(tradeItResponse.code, tradeItResponse.shortMessage, tradeItResponse.longMessages));
            }
        } else {
            onErrorResponse(new TradeItErrorResult(response.code()));
        }
    }
}
