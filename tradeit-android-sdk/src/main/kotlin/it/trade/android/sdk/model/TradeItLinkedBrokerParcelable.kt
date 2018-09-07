package it.trade.android.sdk.model

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.google.gson.annotations.SerializedName
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.observers.DisposableObserver
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import it.trade.android.sdk.TradeItSDK
import it.trade.api.TradeItApiClient
import it.trade.model.TradeItErrorResult
import it.trade.model.callback.AuthenticationCallback
import it.trade.model.callback.TradeItCallback
import it.trade.model.callback.TradeItCallbackWithSecurityQuestion
import it.trade.model.reponse.TradeItAuthenticateResponse
import it.trade.model.reponse.TradeItBrokerAccount
import it.trade.model.reponse.TradeItErrorCode
import it.trade.model.request.TradeItLinkedLogin
import retrofit2.Response
import java.io.IOException
import java.net.SocketException
import java.util.*


class TradeItLinkedBrokerParcelable : Parcelable {
    @Transient
    var apiClient: TradeItApiClientParcelable? = null
        private set

    @Transient
    var linkedLogin: TradeItLinkedLoginParcelable? = null
        internal set

    @SerializedName("accounts")
    var accounts: MutableList<TradeItLinkedBrokerAccountParcelable> = ArrayList()

    @SerializedName("accountsLastUpdated")
    var accountsLastUpdated: Date? = null
        internal set

    @SerializedName("error")
    var error: TradeItErrorResultParcelable? = null
        internal set

    @Transient
    private var linkedBrokerCache: TradeItLinkedBrokerCache? = null

    val isUnauthenticated: Boolean
        get() = this.error != null && this.error!!.requiresAuthentication()

    val brokerName: String
        get() = this.linkedLogin!!.broker

    val isAccountLinkDelayedError: Boolean
        get() = this.error != null && this.error!!.isAccountLinkDelayedError

    constructor(apiClient: TradeItApiClientParcelable, linkedLogin: TradeItLinkedLoginParcelable, linkedBrokerCache: TradeItLinkedBrokerCache) {
        this.apiClient = apiClient
        this.linkedLogin = linkedLogin
        this.linkedBrokerCache = linkedBrokerCache
        setUnauthenticated()
    }

    fun getLinkedBrokerAccount(accountNumber: String): TradeItLinkedBrokerAccountParcelable? {
        var linkedBrokerAccount: TradeItLinkedBrokerAccountParcelable? = null
        for (account in this.accounts) {
            if (account.accountNumber == accountNumber) {
                linkedBrokerAccount = account
                break
            }
        }
        return linkedBrokerAccount
    }

    fun cache() {
        linkedBrokerCache!!.cache(this)
    }

    fun injectAccounts(linkedBrokerAccounts: List<TradeItLinkedBrokerAccountData>) {
        for (linkedBrokerAccountData in linkedBrokerAccounts) {
            val brokerAccount = TradeItBrokerAccount()
            brokerAccount.name = linkedBrokerAccountData.accountName
            brokerAccount.accountNumber = linkedBrokerAccountData.accountNumber
            brokerAccount.accountBaseCurrency = linkedBrokerAccountData.accountBaseCurrency
            this.accounts.add(TradeItLinkedBrokerAccountParcelable(this, brokerAccount))
        }
    }

    fun refreshAccountBalances(callback: TradeItCallBackCompletion) {
        RxJavaPlugins.setErrorHandler(Consumer { ex ->
            var e: Throwable? = ex
            if (e is UndeliverableException) {
                e = e.cause
            }
            if (e is IOException || e is SocketException) {
                // fine, irrelevant network problem or API that throws on cancellation
                return@Consumer
            }
            if (e is InterruptedException) {
                // fine, some blocking code was interrupted by a dispose call
                return@Consumer
            }
            if (e is NullPointerException || e is IllegalArgumentException) {
                // that's likely a bug in the application
                Thread.currentThread().uncaughtExceptionHandler
                        .uncaughtException(Thread.currentThread(), e)
                return@Consumer
            }
            if (e is IllegalStateException) {
                // that's a bug in RxJava or in a custom operator
                Thread.currentThread().uncaughtExceptionHandler
                        .uncaughtException(Thread.currentThread(), e)
                return@Consumer
            }
            Log.w(TAG, "Undeliverable exception received, not sure what to do", e)
        })
        Observable.fromIterable(this.accounts)
                .observeOn(AndroidSchedulers.mainThread(), true)
                .subscribeOn(Schedulers.io())
                .flatMapSingle(Function<TradeItLinkedBrokerAccountParcelable, Single<TradeItLinkedBrokerAccountParcelable>> { linkedBrokerAccountParcelable ->
                    Single.create { emmiter ->
                        linkedBrokerAccountParcelable.refreshBalance(object : TradeItCallback<TradeItLinkedBrokerAccountParcelable> {
                            override fun onSuccess(linkedBrokerAccountParcelable: TradeItLinkedBrokerAccountParcelable) {
                                emmiter.onSuccess(linkedBrokerAccountParcelable)
                            }

                            override fun onError(error: TradeItErrorResult) {
                                Log.e(TAG, error.toString())
                                emmiter.onError(RuntimeException(error.toString()))
                            }
                        })
                    }
                }, true)
                .subscribe(object : DisposableObserver<TradeItLinkedBrokerAccountParcelable>() {
                    override fun onNext(@NonNull linkedBrokerAccountParcelable: TradeItLinkedBrokerAccountParcelable) {
                        Log.d(TAG, "onNext: $linkedBrokerAccountParcelable")
                    }

                    override fun onError(@NonNull e: Throwable) {
                        callback.onFinished()
                    }

                    override fun onComplete() {
                        callback.onFinished()
                    }
                })
    }

    fun authenticate(callback: TradeItCallbackWithSecurityQuestion<List<TradeItLinkedBrokerAccountParcelable>>) {
        val linkedBroker = this
        this.getApiClient()!!.authenticate(this.linkedLogin!!, object : AuthenticationCallback<TradeItAuthenticateResponse, List<TradeItLinkedBrokerAccountParcelable>>(callback, apiClient) {
            public override fun onSuccessResponse(response: Response<TradeItAuthenticateResponse>) {
                linkedBroker.error = null
                val authResponse = response.body()
                val accountsResult = authResponse.accounts
                val linkedBrokerAccounts = mapBrokerAccountsToLinkedBrokerAccounts(accountsResult)
                linkedBrokerCache!!.cache(linkedBroker)
                callback.onSuccess(linkedBrokerAccounts)
            }

            public override fun onErrorResponse(errorResult: TradeItErrorResult) {
                val errorResultParcelable = TradeItErrorResultParcelable(errorResult)
                linkedBroker.error = errorResultParcelable
                callback.onError(errorResultParcelable)
            }
        })
    }

    fun authenticateIfNeeded(callback: TradeItCallbackWithSecurityQuestion<List<TradeItLinkedBrokerAccountParcelable>>) {
        if (this.error != null && this.error!!.requiresAuthentication()) {
            this.authenticate(callback)
        } else if (this.error != null && (this.error!!.requiresRelink() || this.error!!.isConcurrentAuthenticationError || this.error!!.isTooManyLoginAttemptsError)) {
            val errorResultParcelable = TradeItErrorResultParcelable(this.error!!)
            callback.onError(errorResultParcelable)
        } else {
            callback.onSuccess(this.accounts)
        }
    }

    private fun setUnauthenticated() {
        this.error = TradeItErrorResultParcelable(TradeItErrorCode.SESSION_EXPIRED, "Authentication required", Arrays.asList("Linked broker was not authenticated after initializing."))
    }

    fun setAccountLinkDelayedError() {
        this.error = TradeItErrorResultParcelable(TradeItErrorCode.BROKER_ACCOUNT_NOT_AVAILABLE, "Activation In Progress", Arrays.asList("Your " + this.brokerName + " account is being activated. Check back soon (up to two business days)"))
    }

    override fun toString(): String {
        var errorText = "NONE"
        if (this.error != null) {
            errorText = this.error!!.toString()
        }

        return "TradeItLinkedBrokerParcelable{" +
                "TradeItLinkedLogin=" + this.linkedLogin!!.toString() +
                ", accounts=" + this.accounts.toString() +
                ", accountsLastUpdated=" + accountsLastUpdated +
                ", error=" + errorText +
                '}'.toString()
    }

    internal fun getApiClient(): TradeItApiClient? {
        return this.apiClient
    }

    private fun mapBrokerAccountsToLinkedBrokerAccounts(accounts: List<TradeItBrokerAccount>): List<TradeItLinkedBrokerAccountParcelable> {
        val linkedBrokerAccounts = ArrayList<TradeItLinkedBrokerAccountParcelable>()
        for (account in accounts) {
            val existingAccount = getLinkedBrokerAccount(account.accountNumber)
            if (existingAccount != null) {
                existingAccount.accountNumber = account.accountNumber
                existingAccount.accountName = account.name
                existingAccount.accountBaseCurrency = account.accountBaseCurrency
                existingAccount.orderCapabilities = TradeItOrderCapabilityParcelable.mapOrderCapabilitiesToTradeItOrderCapabilityParcelables(account.orderCapabilities)
                existingAccount.userCanDisableMargin = account.userCanDisableMargin
                linkedBrokerAccounts.add(existingAccount)
            } else {
                linkedBrokerAccounts.add(TradeItLinkedBrokerAccountParcelable(this, account))
            }
        }
        this.accounts = linkedBrokerAccounts
        this.accountsLastUpdated = Date()
        return linkedBrokerAccounts
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val that = other as TradeItLinkedBrokerParcelable?

        return linkedLogin!!.userId == that!!.linkedLogin!!.userId
    }

    fun equalsAccounts(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val that = o as TradeItLinkedBrokerParcelable?

        return linkedLogin!!.userId == that!!.linkedLogin!!.userId && this.accounts == that.accounts
    }

    override fun hashCode(): Int {
        return linkedLogin!!.userId.hashCode()
    }


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeParcelable(this.apiClient, flags)
        dest.writeParcelable(this.linkedLogin, flags)
        dest.writeTypedList(this.accounts)
        dest.writeLong(if (this.accountsLastUpdated != null) this.accountsLastUpdated!!.time else -1)
        dest.writeParcelable(this.error, flags)
    }

    protected constructor(`in`: Parcel) {
        this.apiClient = `in`.readParcelable(TradeItApiClientParcelable::class.java.getClassLoader())
        this.linkedLogin = `in`.readParcelable(TradeItLinkedLogin::class.java.getClassLoader())
        this.accounts = `in`.createTypedArrayList(TradeItLinkedBrokerAccountParcelable.CREATOR)
        val tmpAccountsLastUpdated = `in`.readLong()
        this.accountsLastUpdated = if (tmpAccountsLastUpdated.equals(-1)) null else Date(tmpAccountsLastUpdated)
        this.error = `in`.readParcelable(TradeItErrorResultParcelable::class.java.getClassLoader())
        this.linkedBrokerCache = TradeItSDK.linkedBrokerCache

        for (accountParcelable in this.accounts) {
            accountParcelable.linkedBroker = this
        }

        val indexLinkedBroker = TradeItSDK.linkedBrokerManager.linkedBrokers.indexOf(this)

        if (indexLinkedBroker != -1) { // updating linkedBroker reference on the linkedBrokerManager as we created a new object
            TradeItSDK.linkedBrokerManager.linkedBrokers.removeAt(indexLinkedBroker)
            TradeItSDK.linkedBrokerManager.linkedBrokers.add(indexLinkedBroker, this)
        }
    }

    companion object {

        private val TAG = TradeItLinkedBrokerParcelable::class.java.getName()

        @JvmField
        val CREATOR: Parcelable.Creator<TradeItLinkedBrokerParcelable> = object : Parcelable.Creator<TradeItLinkedBrokerParcelable> {
            override fun createFromParcel(source: Parcel): TradeItLinkedBrokerParcelable {
                return TradeItLinkedBrokerParcelable(source)
            }

            override fun newArray(size: Int): Array<TradeItLinkedBrokerParcelable?> {
                return arrayOfNulls(size)
            }
        }
    }


}


