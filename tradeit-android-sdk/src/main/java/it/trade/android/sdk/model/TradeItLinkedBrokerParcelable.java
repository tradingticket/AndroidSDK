package it.trade.android.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import it.trade.android.sdk.TradeItSDK;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.exceptions.UndeliverableException;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import it.trade.api.TradeItApiClient;
import it.trade.model.TradeItErrorResult;
import it.trade.model.callback.AuthenticationCallback;
import it.trade.model.callback.TradeItCallback;
import it.trade.model.callback.TradeItCallbackWithSecurityQuestion;
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

    private static final String TAG = TradeItLinkedBrokerParcelable.class.getName();

    public TradeItLinkedBrokerParcelable(TradeItApiClientParcelable apiClient, TradeItLinkedLoginParcelable linkedLogin, TradeItLinkedBrokerCache linkedBrokerCache) {
        this.apiClient = apiClient;
        this.linkedLogin = linkedLogin;
        this.linkedBrokerCache = linkedBrokerCache;
        setUnauthenticated();
    }

    public TradeItLinkedBrokerAccountParcelable getLinkedBrokerAccount(String accountNumber) {
        TradeItLinkedBrokerAccountParcelable linkedBrokerAccount = null;
        for (TradeItLinkedBrokerAccountParcelable account : this.accounts) {
            if (account.getAccountNumber().equals(accountNumber)) {
                linkedBrokerAccount = account;
                break;
            }
        }
        return linkedBrokerAccount;
    }

    protected void cache() {
        linkedBrokerCache.cache(this);
    }

    public void injectAccounts(List<TradeItInjectBrokerAccount> injectAccounts) {
        for (TradeItInjectBrokerAccount injectBrokerAccount: injectAccounts) {
            TradeItBrokerAccount brokerAccount = new TradeItBrokerAccount();
            brokerAccount.name = injectBrokerAccount.accountName;
            brokerAccount.accountNumber = injectBrokerAccount.accountNumber;
            brokerAccount.accountBaseCurrency = injectBrokerAccount.accountBaseCurrency;
            this.accounts.add(new TradeItLinkedBrokerAccountParcelable(this, brokerAccount));
        }
    }
    protected TradeItErrorResultParcelable getError() {
        return this.error;
    }
    public void refreshAccountBalances(final TradeItCallBackCompletion callback) {
        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable e) throws Exception {
                if (e instanceof UndeliverableException) {
                    e = e.getCause();
                }
                if ((e instanceof IOException) || (e instanceof SocketException)) {
                    // fine, irrelevant network problem or API that throws on cancellation
                    return;
                }
                if (e instanceof InterruptedException) {
                    // fine, some blocking code was interrupted by a dispose call
                    return;
                }
                if ((e instanceof NullPointerException) || (e instanceof IllegalArgumentException)) {
                    // that's likely a bug in the application
                    Thread.currentThread().getUncaughtExceptionHandler()
                            .uncaughtException(Thread.currentThread(), e);
                    return;
                }
                if (e instanceof IllegalStateException) {
                    // that's a bug in RxJava or in a custom operator
                    Thread.currentThread().getUncaughtExceptionHandler()
                            .uncaughtException(Thread.currentThread(), e);
                    return;
                }
                Log.w(TAG, "Undeliverable exception received, not sure what to do", e);
            }
        });
        Observable.fromIterable(this.accounts)
                .observeOn(AndroidSchedulers.mainThread(), true)
                .subscribeOn(Schedulers.io())
                .flatMapSingle(new Function<TradeItLinkedBrokerAccountParcelable, Single<TradeItLinkedBrokerAccountParcelable>>() {
                    @Override
                    public Single<TradeItLinkedBrokerAccountParcelable> apply(@NonNull final TradeItLinkedBrokerAccountParcelable linkedBrokerAccountParcelable) throws Exception {
                        return Single.create(new SingleOnSubscribe<TradeItLinkedBrokerAccountParcelable>() {
                            @Override
                            public void subscribe(@NonNull final SingleEmitter<TradeItLinkedBrokerAccountParcelable> emmiter) throws Exception {
                                linkedBrokerAccountParcelable.refreshBalance(new TradeItCallback<TradeItLinkedBrokerAccountParcelable>() {
                                    @Override
                                    public void onSuccess(TradeItLinkedBrokerAccountParcelable linkedBrokerAccountParcelable) {
                                        emmiter.onSuccess(linkedBrokerAccountParcelable);
                                    }

                                    @Override
                                    public void onError(TradeItErrorResult error) {
                                        Log.e(TAG, error.toString());
                                        emmiter.onError(new RuntimeException(error.toString()));
                                    }
                                });
                            }
                        });
                    }
                }, true)
                .subscribe(new DisposableObserver() {
                    @Override
                    public void onNext(@NonNull Object linkedBrokerAccountParcelable) {
                        Log.d(TAG, "onNext: " + linkedBrokerAccountParcelable);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        callback.onFinished();
                    }

                    @Override
                    public void onComplete() {
                        callback.onFinished();
                    }
                });
    }

    public void authenticate(final TradeItCallbackWithSecurityQuestion<List<TradeItLinkedBrokerAccountParcelable>> callback) {
        final TradeItLinkedBrokerParcelable linkedBroker = this;
        this.getApiClient().authenticate(this.getLinkedLogin(), new AuthenticationCallback<TradeItAuthenticateResponse, List<TradeItLinkedBrokerAccountParcelable>>(callback, apiClient) {
            @Override
            public void onSuccessResponse(Response<TradeItAuthenticateResponse> response) {
                    linkedBroker.error = null;
                    TradeItAuthenticateResponse authResponse = response.body();
                    List<TradeItBrokerAccount> accountsResult = authResponse.accounts;
                    List<TradeItLinkedBrokerAccountParcelable> linkedBrokerAccounts = mapBrokerAccountsToLinkedBrokerAccounts(accountsResult);
                    accounts = linkedBrokerAccounts;
                    accountsLastUpdated = new Date();
                    linkedBrokerCache.cache(linkedBroker);
                    callback.onSuccess(linkedBrokerAccounts);
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

    public void setAccountLinkDelayedError() {
        this.setError(new TradeItErrorResultParcelable(TradeItErrorCode.BROKER_ACCOUNT_NOT_AVAILABLE, "Activation In Progress", Arrays.asList("Your " + this.getBrokerName() + " account is being activated. Check back soon (up to two business days)")));
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

    public boolean isAccountLinkDelayedError() {
        return (this.error != null && this.error.isAccountLinkDelayedError());
    }

    private List<TradeItLinkedBrokerAccountParcelable> mapBrokerAccountsToLinkedBrokerAccounts(List<TradeItBrokerAccount> accounts) {
        List<TradeItLinkedBrokerAccountParcelable> linkedBrokerAccounts = new ArrayList<>();
        for (TradeItBrokerAccount account : accounts) {
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
        this.apiClient = in.readParcelable(TradeItApiClientParcelable.class.getClassLoader());
        this.linkedLogin = in.readParcelable(TradeItLinkedLogin.class.getClassLoader());
        this.accounts = in.createTypedArrayList(TradeItLinkedBrokerAccountParcelable.CREATOR);
        long tmpAccountsLastUpdated = in.readLong();
        this.accountsLastUpdated = tmpAccountsLastUpdated == -1 ? null : new Date(tmpAccountsLastUpdated);
        this.error = in.readParcelable(TradeItErrorResultParcelable.class.getClassLoader());
        this.linkedBrokerCache = TradeItSDK.getLinkedBrokerCache();
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


