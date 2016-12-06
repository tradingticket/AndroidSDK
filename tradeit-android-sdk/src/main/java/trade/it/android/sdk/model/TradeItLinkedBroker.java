package trade.it.android.sdk.model;

import java.util.List;

import it.trade.tradeitapi.API.TradeItApiClient;
import it.trade.tradeitapi.model.TradeItAuthenticateResponse;
import it.trade.tradeitapi.model.TradeItAuthenticateResponse.Account;
import it.trade.tradeitapi.model.TradeItLinkedAccount;
import retrofit2.Response;
import trade.it.android.sdk.internal.CallBackWithDefaultSecurityQuestionAndErrorHandling;

public class TradeItLinkedBroker {
    private TradeItApiClient apiClient;

    public TradeItLinkedBroker(TradeItApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public void authenticate(final TradeItCallbackWithSecurityQuestion<List<Account>> callback) {
        this.apiClient.authenticate(new CallBackWithDefaultSecurityQuestionAndErrorHandling<TradeItAuthenticateResponse, List<Account>>(callback) {
            @Override
            public void onSuccessResponse(Response<TradeItAuthenticateResponse> response) {
                TradeItAuthenticateResponse authResponse = response.body();
                List<Account> accounts = authResponse.accounts;
                callback.onSuccess(accounts);
            }
        });
    }

    @Override
    public String toString() {
        return "TradeItLinkedBroker{" +
                "TradeItLinkedAccount=" + getLinkedAccount().toString() +
                '}';
    }
//    public void answerSecurityQuestion(String securityAnswer, final TradeItCallbackWithSecurityQuestion<List<Account>> callback) {
//        TradeItAnswerSecurityQuestionRequest answerSecurityQuestionRequest = new TradeItAnswerSecurityQuestionRequest(securityAnswer);
//        this.apiClient.answerSecurityQuestion(answerSecurityQuestionRequest, new CallBackWithDefaultSecurityQuestionAndErrorHandling<TradeItAuthenticateResponse, List<Account>>(callback) {
//            @Override
//            public void onSuccessResponse(Response<TradeItAuthenticateResponse> response) {
//                TradeItAuthenticateResponse authResponse = response.body();
//                List<Account> accounts = authResponse.accounts;
//                callback.onSuccess(accounts);
//            }
//        });
//    }

    public TradeItLinkedAccount getLinkedAccount() {
        return this.apiClient.getTradeItLinkedAccount();
    }

}


