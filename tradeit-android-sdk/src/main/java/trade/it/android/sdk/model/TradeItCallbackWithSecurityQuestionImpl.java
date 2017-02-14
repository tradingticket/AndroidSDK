package trade.it.android.sdk.model;

import android.util.Log;

import it.trade.tradeitapi.API.TradeItApiClient;
import it.trade.tradeitapi.model.TradeItAnswerSecurityQuestionRequest;
import trade.it.android.sdk.internal.AuthenticationCallbackWithErrorHandling;

public abstract class TradeItCallbackWithSecurityQuestionImpl<T> implements TradeItCallbackWithSecurityQuestion<T>  {
    private TradeItApiClient apiClient;
    private AuthenticationCallbackWithErrorHandling authenticationHandler;

    @Override
    public void submitSecurityAnswer(String answer) {
        TradeItAnswerSecurityQuestionRequest answerSecurityQuestionRequest = new TradeItAnswerSecurityQuestionRequest(answer);
        if (this.apiClient != null && this.authenticationHandler != null) {
            this.apiClient.answerSecurityQuestion(answerSecurityQuestionRequest, authenticationHandler);
        } else {
            Log.e(this.getClass().getName(), "apiClient or authenticationCallbackWithErrorHandling are not set");
            onError(new TradeItErrorResult("Error submitting answer", "An error occured while submitting security question"));
        }

    }


    public void setApiClient(TradeItApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public void setAuthenticationHandler(AuthenticationCallbackWithErrorHandling authenticationHandler) {
        this.authenticationHandler = authenticationHandler;
    }
}
