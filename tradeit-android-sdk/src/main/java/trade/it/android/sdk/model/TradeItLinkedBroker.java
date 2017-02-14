package trade.it.android.sdk.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.trade.tradeitapi.API.TradeItApiClient;
import it.trade.tradeitapi.model.TradeItAuthenticateResponse;
import it.trade.tradeitapi.model.TradeItAuthenticateResponse.Account;
import it.trade.tradeitapi.model.TradeItLinkedAccount;
import retrofit2.Response;
import trade.it.android.sdk.internal.AuthenticationCallbackWithErrorHandling;

public class TradeItLinkedBroker {
    private transient TradeItApiClient apiClient;
    private List<TradeItLinkedBrokerAccount> accounts = new ArrayList<>();
    private Date accountsLastUpdated;
    private transient TradeItLinkedBrokerCache linkedBrokerCache = new TradeItLinkedBrokerCache();
    private transient Context context;

    public TradeItLinkedBroker(Context context, TradeItApiClient apiClient) {
        this.apiClient = apiClient;
        this.context = context;
    }

    public void authenticate(final TradeItCallbackWithSecurityQuestion<List<TradeItLinkedBrokerAccount>> callback) {
        final TradeItLinkedBroker linkedBroker = this;

        this.apiClient.authenticate(new AuthenticationCallbackWithErrorHandling<TradeItAuthenticateResponse, List<TradeItLinkedBrokerAccount>>(callback, apiClient) {
            @Override
            public void onSuccessResponse(Response<TradeItAuthenticateResponse> response) {
                TradeItAuthenticateResponse authResponse = response.body();
                List<Account> accountsResult = authResponse.accounts;
                List<TradeItLinkedBrokerAccount> linkedBrokerAccounts = mapAccountsToLinkedBrokerAccount(accountsResult);
                accounts = linkedBrokerAccounts;
                accountsLastUpdated = new Date();
                linkedBrokerCache.cache(context, linkedBroker);
                callback.onSuccess(linkedBrokerAccounts);
            }
        });
    }

    @Override
    public String toString() {
        return "TradeItLinkedBroker{" +
                "TradeItLinkedAccount=" + getLinkedAccount().toString() +
                ", accounts=" + getAccounts().toString() +
                ", accountsLastUpdated=" + getAccountsLastUpdated() +
                '}';
    }

    public TradeItLinkedAccount getLinkedAccount() {
        return this.apiClient.getTradeItLinkedAccount();
    }

    TradeItApiClient getTradeItApiClient() {
        return this.apiClient;
    }

    public List<TradeItLinkedBrokerAccount> getAccounts() {
        return this.accounts;
    }

    public Date getAccountsLastUpdated() {
        return accountsLastUpdated;
    }

    void setAccounts(List<TradeItLinkedBrokerAccount> accounts) {
        this.accounts = accounts;
    }

    void setAccountsLastUpdated(Date accountsLastUpdated) {
        this.accountsLastUpdated = accountsLastUpdated;
    }

    private List<TradeItLinkedBrokerAccount> mapAccountsToLinkedBrokerAccount(List<Account> accounts) {
        List<TradeItLinkedBrokerAccount> linkedBrokerAccounts = new ArrayList<>();
        for (Account account: accounts) {
            linkedBrokerAccounts.add(new TradeItLinkedBrokerAccount(this, account));
        }
        return linkedBrokerAccounts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TradeItLinkedBroker that = (TradeItLinkedBroker) o;

        return apiClient.getTradeItLinkedAccount().userId.equals(that.apiClient.getTradeItLinkedAccount().userId);

    }

    @Override
    public int hashCode() {
        return apiClient.getTradeItLinkedAccount().userId.hashCode();
    }

    public void setLinkedAccount(TradeItLinkedAccount linkedAccount) {
        this.apiClient.setTradeItLinkedAccount(linkedAccount);
    }
}


