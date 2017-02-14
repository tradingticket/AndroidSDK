package trade.it.android.sdk.internal;

import it.trade.tradeitapi.model.TradeItAuthenticateResponse;
import it.trade.tradeitapi.model.TradeItResponse;
import it.trade.tradeitapi.model.TradeItResponseStatus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import trade.it.android.sdk.model.TradeItCallbackWithSecurityQuestion;
import trade.it.android.sdk.model.TradeItErrorResult;
import trade.it.android.sdk.model.TradeItSecurityQuestion;

public abstract class CallBackWithDefaultSecurityQuestionAndErrorHandling<TradeItResponseType, TradeItCallBackType> implements Callback<TradeItResponseType> {

    private TradeItCallbackWithSecurityQuestion<TradeItCallBackType> callback;

    protected CallBackWithDefaultSecurityQuestionAndErrorHandling(TradeItCallbackWithSecurityQuestion<TradeItCallBackType> callback) {
        this.callback = callback;
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
                // The broker is requesting a security question be answered by the user to authenticate.
                // Display the question authResponse.securityQuestion to the user.
                // Use tradeItApiClient.answerSecurityQuestion to submit the user's answer to the broker.
                TradeItAuthenticateResponse authenticateResponse = responseType instanceof TradeItAuthenticateResponse ? (TradeItAuthenticateResponse) responseType : null;
                if (tradeItResponse == null) {
                    callback.onError(new TradeItErrorResult());
                } else {
                    callback.onSecurityQuestion(new TradeItSecurityQuestion(authenticateResponse.securityQuestion, authenticateResponse.securityQuestionOptions));
                }
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
