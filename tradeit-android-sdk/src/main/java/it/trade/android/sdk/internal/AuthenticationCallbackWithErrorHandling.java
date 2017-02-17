package it.trade.android.sdk.internal;

import it.trade.android.sdk.model.TradeItCallbackWithSecurityQuestion;
import it.trade.android.sdk.model.TradeItErrorResult;
import it.trade.android.sdk.model.TradeItSecurityQuestion;
import it.trade.tradeitapi.API.TradeItApiClient;
import it.trade.tradeitapi.model.TradeItAuthenticateResponse;
import it.trade.tradeitapi.model.TradeItResponse;
import it.trade.tradeitapi.model.TradeItResponseStatus;
import retrofit2.Call;
import retrofit2.Response;

public abstract class AuthenticationCallbackWithErrorHandling<TradeItResponseType, TradeItCallBackType> extends DefaultCallbackWithErrorHandling<TradeItResponseType, TradeItCallBackType> {

    protected AuthenticationCallbackWithErrorHandling(TradeItCallbackWithSecurityQuestion callback, TradeItApiClient apiClient) {
        super(callback);
        callback.setApiClient(apiClient);
        callback.setAuthenticationHandler(this);
    }

    @Override
    public void onResponse(Call<TradeItResponseType> call, Response<TradeItResponseType> response) {
        if (response.isSuccessful()) {
            TradeItResponseType responseType = response.body();
            TradeItResponse tradeItResponse = responseType instanceof TradeItResponse ? (TradeItResponse) responseType : null;
            if (tradeItResponse == null) {
                callback.onError(new TradeItErrorResult());
            } else if (tradeItResponse.status == TradeItResponseStatus.SUCCESS) {
                onSuccessResponse(response);
            } else if (tradeItResponse.status == TradeItResponseStatus.INFORMATION_NEEDED) {
                TradeItAuthenticateResponse authenticateResponse = responseType instanceof TradeItAuthenticateResponse ? (TradeItAuthenticateResponse) responseType : null;
                if (tradeItResponse == null) {
                    callback.onError(new TradeItErrorResult());
                } else {
                    ((TradeItCallbackWithSecurityQuestion) callback).onSecurityQuestion(new TradeItSecurityQuestion(authenticateResponse.securityQuestion, authenticateResponse.securityQuestionOptions));
                }
            } else {
                callback.onError(new TradeItErrorResult(tradeItResponse.code, tradeItResponse.shortMessage, tradeItResponse.longMessages));
            }
        } else {
            callback.onError(new TradeItErrorResult(response.code()));
        }
    }
}
