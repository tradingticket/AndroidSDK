package it.trade.android.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import it.trade.api.TradeItApiClient;
import it.trade.model.TradeItErrorResult;
import it.trade.model.TradeItSecurityQuestion;
import it.trade.model.callback.AuthenticationCallback;
import it.trade.model.callback.TradeItCallbackWithSecurityQuestion;
import it.trade.model.callback.TradeItCallbackWithSecurityQuestionImpl;
import it.trade.model.reponse.TradeItAuthenticateResponse;
import it.trade.model.reponse.TradeItBrokerAccount;
import it.trade.model.reponse.TradeItErrorCode;
import it.trade.model.request.TradeItLinkedLogin;
import retrofit2.Response;

public class TradeItLinkedBrokerParcelable implements Parcelable {
    private transient TradeItApiClientParcelable apiClient;
    private transient TradeItLinkedLoginParcelable linkedLogin;
    private List<TradeItLinkedBrokerAccountParcelable> accounts = new ArrayList<>();
    private Date accountsLastUpdated;
    private TradeItErrorResultParcelable error;
    private transient TradeItLinkedBrokerCache linkedBrokerCache;

    public TradeItLinkedBrokerParcelable(TradeItApiClientParcelable apiClient, TradeItLinkedLoginParcelable linkedLogin, TradeItLinkedBrokerCache linkedBrokerCache) {
        this.apiClient = apiClient;
        this.linkedLogin = linkedLogin;
        this.linkedBrokerCache = linkedBrokerCache;
        setUnauthenticated();
    }

    protected void cache() {
        linkedBrokerCache.cache(this);
    }

//    public void refreshBalancesForAllAccounts() {
//
//    }

    public void authenticate(final TradeItCallbackWithSecurityQuestion<List<TradeItLinkedBrokerAccountParcelable>> callback) {
        final TradeItLinkedBrokerParcelable linkedBroker = this;
        this.apiClient.authenticate(this.linkedLogin, new AuthenticationCallback<TradeItAuthenticateResponse, TradeItSecurityQuestion>(callback, apiClient) {
            @Override
            public void onSuccessResponse(Response<TradeItAuthenticateResponse> response) {
                linkedBroker.error = null;
                TradeItAuthenticateResponse authResponse = response.body();
                List<TradeItBrokerAccount> accountsResult = authResponse.accounts;
                List<TradeItLinkedBrokerAccountParcelable> linkedBrokerAccounts = mapBrokerAccountsToLinkedBrokerAccounts(accountsResult);
                accounts = linkedBrokerAccounts;
                accountsLastUpdated = new Date();
                linkedBrokerCache.cache(linkedBroker);
                ((TradeItCallbackWithSecurityQuestionImpl) callback).onSuccess(linkedBrokerAccounts);
            }

            @Override
            public void onErrorResponse(TradeItErrorResult errorResult) {
                TradeItErrorResultParcelable errorResultParcelable = new TradeItErrorResultParcelable(errorResult);
                linkedBroker.error = errorResultParcelable;
                callback.onError(errorResultParcelable);
            }
        });
    }

    public void authenticateIfNeeded(final TradeItCallbackWithSecurityQuestion<List<TradeItLinkedBrokerAccountParcelable>> callback) {
        if (this.error != null && this.error.requiresAuthentication()) {
            this.authenticate(callback);
        } else if (this.error != null && (this.error.requiresRelink() || this.error.isConcurrentAuthenticationError() || this.error.isTooManyLoginAttemptsError())) {
            TradeItErrorResultParcelable errorResultParcelable = new TradeItErrorResultParcelable(this.error);
            callback.onError(errorResultParcelable);
        } else {
            callback.onSuccess(this.accounts);
        }
    }

    private void setUnauthenticated() {
        this.setError(new TradeItErrorResultParcelable(TradeItErrorCode.SESSION_EXPIRED, "Authentication required", Arrays.asList("Linked broker was not authenticated after initializing.")));
    }

    void setError(TradeItErrorResultParcelable error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "TradeItLinkedBrokerParcelable{" +
                "TradeItLinkedLogin=" + this.linkedLogin.toString() +
                ", accounts=" + getAccounts().toString() +
                ", accountsLastUpdated=" + getAccountsLastUpdated() +
                '}';
    }

    public String getBrokerName() {
        return this.linkedLogin.broker;
    }

    TradeItApiClient getApiClient() {
        return this.apiClient;
    }

    public List<TradeItLinkedBrokerAccountParcelable> getAccounts() {
        return this.accounts;
    }

    public Date getAccountsLastUpdated() {
        return accountsLastUpdated;
    }

    void setAccounts(List<TradeItLinkedBrokerAccountParcelable> accounts) {
        this.accounts = accounts;
    }

    void setAccountsLastUpdated(Date accountsLastUpdated) {
        this.accountsLastUpdated = accountsLastUpdated;
    }

    private List<TradeItLinkedBrokerAccountParcelable> mapBrokerAccountsToLinkedBrokerAccounts(List<TradeItBrokerAccount> accounts) {
        List<TradeItLinkedBrokerAccountParcelable> linkedBrokerAccounts = new ArrayList<>();
        for (TradeItBrokerAccount account: accounts) {
            linkedBrokerAccounts.add(new TradeItLinkedBrokerAccountParcelable(this, account));
        }
        return linkedBrokerAccounts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TradeItLinkedBrokerParcelable that = (TradeItLinkedBrokerParcelable) o;

        return linkedLogin.userId.equals(that.linkedLogin.userId);

    }

    @Override
    public int hashCode() {
        return linkedLogin.userId.hashCode();
    }

    public TradeItLinkedLoginParcelable getLinkedLogin() {
        return linkedLogin;
    }

    public void setLinkedLogin(TradeItLinkedLoginParcelable linkedLogin) {
        this.linkedLogin = linkedLogin;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.apiClient, flags);
        dest.writeParcelable(this.linkedLogin, flags);
        dest.writeTypedList(this.accounts);
        dest.writeLong(this.accountsLastUpdated != null ? this.accountsLastUpdated.getTime() : -1);
        dest.writeParcelable(this.error, flags);
    }

    protected TradeItLinkedBrokerParcelable(Parcel in) {
        this.apiClient = in.readParcelable(TradeItApiClient.class.getClassLoader());
        this.linkedLogin = in.readParcelable(TradeItLinkedLogin.class.getClassLoader());
        this.accounts = in.createTypedArrayList(TradeItLinkedBrokerAccountParcelable.CREATOR);
        long tmpAccountsLastUpdated = in.readLong();
        this.accountsLastUpdated = tmpAccountsLastUpdated == -1 ? null : new Date(tmpAccountsLastUpdated);
        this.error = in.readParcelable(TradeItErrorResultParcelable.class.getClassLoader());
    }

    public static final Parcelable.Creator<TradeItLinkedBrokerParcelable> CREATOR = new Parcelable.Creator<TradeItLinkedBrokerParcelable>() {
        @Override
        public TradeItLinkedBrokerParcelable createFromParcel(Parcel source) {
            return new TradeItLinkedBrokerParcelable(source);
        }

        @Override
        public TradeItLinkedBrokerParcelable[] newArray(int size) {
            return new TradeItLinkedBrokerParcelable[size];
        }
    };
}


