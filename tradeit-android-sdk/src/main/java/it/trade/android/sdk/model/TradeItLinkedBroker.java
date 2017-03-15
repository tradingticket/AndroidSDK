package it.trade.android.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.trade.android.sdk.TradeItSDK;
import it.trade.android.sdk.internal.AuthenticationCallbackWithErrorHandling;
import it.trade.tradeitapi.API.TradeItApiClient;
import it.trade.tradeitapi.model.TradeItAuthenticateResponse;
import it.trade.tradeitapi.model.TradeItBrokerAccount;
import it.trade.tradeitapi.model.TradeItLinkedLogin;
import retrofit2.Response;

public class TradeItLinkedBroker implements Parcelable {
    private transient TradeItApiClient apiClient;
    private List<TradeItLinkedBrokerAccount> accounts = new ArrayList<>();
    private Date accountsLastUpdated;

    public TradeItLinkedBroker(TradeItApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public void authenticate(final TradeItCallbackWithSecurityQuestion<List<TradeItLinkedBrokerAccount>> callback) {
        final TradeItLinkedBroker linkedBroker = this;

        this.apiClient.authenticate(new AuthenticationCallbackWithErrorHandling<TradeItAuthenticateResponse, List<TradeItLinkedBrokerAccount>>(callback, apiClient) {
            @Override
            public void onSuccessResponse(Response<TradeItAuthenticateResponse> response) {
                TradeItAuthenticateResponse authResponse = response.body();
                List<TradeItBrokerAccount> accountsResult = authResponse.accounts;
                List<TradeItLinkedBrokerAccount> linkedBrokerAccounts = mapBrokerAccountsToLinkedBrokerAccounts(accountsResult);
                accounts = linkedBrokerAccounts;
                accountsLastUpdated = new Date();
                TradeItSDK.getLinkedBrokerCache().cache(linkedBroker);
                callback.onSuccess(linkedBrokerAccounts);
            }
        });
    }

    @Override
    public String toString() {
        return "TradeItLinkedBroker{" +
                "TradeItLinkedLogin=" + getLinkedLogin().toString() +
                ", accounts=" + getAccounts().toString() +
                ", accountsLastUpdated=" + getAccountsLastUpdated() +
                '}';
    }

    public TradeItLinkedLogin getLinkedLogin() {
        return this.apiClient.getTradeItLinkedLogin();
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

    private List<TradeItLinkedBrokerAccount> mapBrokerAccountsToLinkedBrokerAccounts(List<TradeItBrokerAccount> accounts) {
        List<TradeItLinkedBrokerAccount> linkedBrokerAccounts = new ArrayList<>();
        for (TradeItBrokerAccount account: accounts) {
            linkedBrokerAccounts.add(new TradeItLinkedBrokerAccount(this, account));
        }
        return linkedBrokerAccounts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TradeItLinkedBroker that = (TradeItLinkedBroker) o;

        return apiClient.getTradeItLinkedLogin().userId.equals(that.apiClient.getTradeItLinkedLogin().userId);

    }

    @Override
    public int hashCode() {
        return apiClient.getTradeItLinkedLogin().userId.hashCode();
    }

    public void setLinkedLogin(TradeItLinkedLogin linkedLogin) {
        this.apiClient.setTradeItLinkedLogin(linkedLogin);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.apiClient, flags);
        dest.writeTypedList(this.accounts);
        dest.writeLong(this.accountsLastUpdated != null ? this.accountsLastUpdated.getTime() : -1);
    }

    protected TradeItLinkedBroker(Parcel in) {
        this.apiClient = in.readParcelable(TradeItApiClient.class.getClassLoader());
        this.accounts = in.createTypedArrayList(TradeItLinkedBrokerAccount.CREATOR);
        long tmpAccountsLastUpdated = in.readLong();
        this.accountsLastUpdated = tmpAccountsLastUpdated == -1 ? null : new Date(tmpAccountsLastUpdated);
    }

    public static final Parcelable.Creator<TradeItLinkedBroker> CREATOR = new Parcelable.Creator<TradeItLinkedBroker>() {
        @Override
        public TradeItLinkedBroker createFromParcel(Parcel source) {
            return new TradeItLinkedBroker(source);
        }

        @Override
        public TradeItLinkedBroker[] newArray(int size) {
            return new TradeItLinkedBroker[size];
        }
    };
}


