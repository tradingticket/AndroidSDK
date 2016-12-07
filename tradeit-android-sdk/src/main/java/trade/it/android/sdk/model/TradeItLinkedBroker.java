package trade.it.android.sdk.model;

import java.util.ArrayList;
import java.util.List;

import it.trade.tradeitapi.API.TradeItApiClient;
import it.trade.tradeitapi.model.TradeItAuthenticateResponse;
import it.trade.tradeitapi.model.TradeItAuthenticateResponse.Account;
import it.trade.tradeitapi.model.TradeItLinkedAccount;
import retrofit2.Response;
import trade.it.android.sdk.internal.CallBackWithDefaultSecurityQuestionAndErrorHandling;

public class TradeItLinkedBroker {
    private TradeItApiClient apiClient;
    private List<TradeItLinkedBrokerAccount> accounts = new ArrayList<>();

    public TradeItLinkedBroker(TradeItApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public void authenticate(final TradeItCallbackWithSecurityQuestion<List<TradeItLinkedBrokerAccount>> callback) {
        this.apiClient.authenticate(new CallBackWithDefaultSecurityQuestionAndErrorHandling<TradeItAuthenticateResponse, List<TradeItLinkedBrokerAccount>>(callback) {
            @Override
            public void onSuccessResponse(Response<TradeItAuthenticateResponse> response) {
                TradeItAuthenticateResponse authResponse = response.body();
                List<Account> accountsResult = authResponse.accounts;
                List<TradeItLinkedBrokerAccount> linkedBrokerAccounts = mapAccountsToLinkedBrokerAccount(accountsResult);
                accounts = linkedBrokerAccounts;
                callback.onSuccess(linkedBrokerAccounts);
            }
        });
    }

    @Override
    public String toString() {
        return "TradeItLinkedBroker{" +
                "TradeItLinkedAccount=" + getLinkedAccount().toString() +
                ", accounts=" + getAccounts().toString() +
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

    public List<TradeItLinkedBrokerAccount> getAccounts() {
        return this.accounts;
    }

    private List<TradeItLinkedBrokerAccount> mapAccountsToLinkedBrokerAccount(List<Account> accounts) {
        List<TradeItLinkedBrokerAccount> linkedBrokerAccounts = new ArrayList<>();
        for (Account account: accounts) {
            linkedBrokerAccounts.add(new TradeItLinkedBrokerAccount(this, account));
        }
        return linkedBrokerAccounts;
    }

}


