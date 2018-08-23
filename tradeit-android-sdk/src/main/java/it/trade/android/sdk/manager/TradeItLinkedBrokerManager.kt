package it.trade.android.sdk.manager

import android.util.Log
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.internal.operators.single.SingleCache
import io.reactivex.observers.DisposableObserver
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import it.trade.android.sdk.exceptions.TradeItDeleteLinkedLoginException
import it.trade.android.sdk.exceptions.TradeItRetrieveLinkedLoginException
import it.trade.android.sdk.exceptions.TradeItSaveLinkedLoginException
import it.trade.android.sdk.exceptions.TradeItUpdateLinkedLoginException
import it.trade.android.sdk.internal.LinkedBrokersParcelableList
import it.trade.android.sdk.internal.TradeItKeystoreService
import it.trade.android.sdk.model.*
import it.trade.model.TradeItErrorResult
import it.trade.model.TradeItSecurityQuestion
import it.trade.model.callback.TradeItCallback
import it.trade.model.callback.TradeItCallbackWithSecurityQuestionImpl
import it.trade.model.reponse.Instrument
import it.trade.model.reponse.TradeItAvailableBrokersResponse.Broker
import it.trade.model.reponse.TradeItResponse
import it.trade.model.request.TradeItLinkedLogin
import java.io.IOException
import java.net.SocketException
import java.util.*

class TradeItLinkedBrokerManager @Throws(TradeItRetrieveLinkedLoginException::class)
constructor(
        private val apiClient: TradeItApiClientParcelable,
        private val linkedBrokerCache: TradeItLinkedBrokerCache,
        private val keystoreService: TradeItKeystoreService,
        prefetchBrokerList: Boolean
) {
    private val linkedBrokers = mutableListOf<TradeItLinkedBrokerParcelable>()
    private var availableBrokersSingleCache: SingleCache<List<Broker>>? = null

    init {
        this.loadLinkedBrokersFromSharedPreferences()

        if (prefetchBrokerList) {
            // Start fetching available brokers asap so that it is cached
            this.getAvailableBrokersSingleCache()
        }
    }

    @Synchronized
    @Throws(TradeItSaveLinkedLoginException::class, TradeItDeleteLinkedLoginException::class)
    fun syncLocalLinkedBrokers(
            linkedBrokerDataList: List<TradeItLinkedBrokerData>
    ) {
        val linkedBrokers = this.linkedBrokers

        for (linkedBrokerData in linkedBrokerDataList) {
            val linkedBrokerDataParcelable = createNewLinkedBroker(linkedBrokerData)

            if (!linkedBrokers.contains(linkedBrokerDataParcelable)) { // Add missing linkedBrokers
                if (linkedBrokerData.isLinkActivationPending) {
                    linkedBrokerDataParcelable.setAccountLinkDelayedError()
                }

                linkedBrokerCache.cache(linkedBrokerDataParcelable)
                linkedBrokers.add(linkedBrokerDataParcelable)
                linkedBrokerDataParcelable.linkedLogin?.let { linkedLogin ->
                    keystoreService.saveLinkedLogin(linkedLogin, linkedLogin.label)
                }

            } else if (!LinkedBrokersParcelableList(linkedBrokers).containsSameAccounts(linkedBrokerDataParcelable)) { // Update linkedBrokers accounts if they changed
                val index = linkedBrokers.indexOf(linkedBrokerDataParcelable)

                if (index != -1) {
                    val linkedBrokerParcelableToUpdate = linkedBrokers[index]

                    if (linkedBrokerData.isLinkActivationPending) {
                        linkedBrokerParcelableToUpdate.setAccountLinkDelayedError()
                    }

                    linkedBrokerParcelableToUpdate.accounts = linkedBrokerDataParcelable.accounts
                    linkedBrokerCache.cache(linkedBrokerParcelableToUpdate)
                } else {
                    Log.e(TAG, "syncLocalLinkedBrokers error: couldn't find the linkedBroker to update")
                }
            }
        }

        // Remove non existing linkedBrokers
        for (linkedBroker in ArrayList(linkedBrokers)) {
            if (!linkedBrokerDataList.contains(TradeItLinkedBrokerData(linkedBroker.linkedLogin!!))) {
                linkedBrokerCache.removeFromCache(linkedBroker)
                linkedBrokers.remove(linkedBroker)
                linkedBroker.linkedLogin?.let { linkedLogin ->
                    keystoreService.deleteLinkedLogin(linkedLogin)
                }
            }
        }
    }

    @Throws(TradeItRetrieveLinkedLoginException::class)
    private fun loadLinkedBrokersFromSharedPreferences() {
        val linkedLoginList = keystoreService.linkedLogins

        for (linkedLogin in linkedLoginList) {
            val linkedBroker = createNewLinkedBroker(linkedLogin)
            linkedBrokerCache.syncFromCache(linkedBroker)
            linkedBrokers.add(linkedBroker)
        }
    }

    private fun createNewLinkedBroker(linkedBrokerData: TradeItLinkedBrokerData): TradeItLinkedBrokerParcelable {
        val linkedLoginParcelable = TradeItLinkedLoginParcelable(
                linkedBrokerData.broker,
                linkedBrokerData.userId ?: "",
                linkedBrokerData.userToken ?: ""
        )

        val linkedBrokerParcelable = createNewLinkedBroker(linkedLoginParcelable)
        linkedBrokerParcelable.injectAccounts(linkedBrokerData.linkedBrokerAccounts)

        return linkedBrokerParcelable
    }

    private fun createNewLinkedBroker(linkedLoginParcelable: TradeItLinkedLoginParcelable): TradeItLinkedBrokerParcelable {
        val apiClientParcelable = TradeItApiClientParcelable(
                this.apiClient.apiKey,
                this.apiClient.environment,
                this.apiClient.requestInterceptorParcelable
        )
        //provides a default token, so if the user doesn't authenticate before an other call, it will pass an expired token in order to get the session expired error
        apiClientParcelable.sessionToken = "invalid-default-token"

        return TradeItLinkedBrokerParcelable(apiClientParcelable, linkedLoginParcelable, linkedBrokerCache)
    }

    fun authenticateAll(callback: TradeItCallbackWithSecurityQuestionAndCompletion) {
        RxJavaPlugins.setErrorHandler(
                Consumer { e ->
                    var e = e
                    if (e is UndeliverableException) {
                        e = e.cause!!
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
                }
        )

        Observable.fromIterable(this.getLinkedBrokers())
                .observeOn(AndroidSchedulers.mainThread(), true)
                .subscribeOn(Schedulers.io())
                .flatMapSingle(
                        Function<TradeItLinkedBrokerParcelable, Single<TradeItLinkedBrokerParcelable>> { linkedBrokerParcelable ->
                            Single.create { emmiter ->
                                linkedBrokerParcelable.authenticateIfNeeded(
                                        object : TradeItCallbackWithSecurityQuestionImpl<List<TradeItLinkedBrokerAccountParcelable>>() {
                                            override fun onSecurityQuestion(securityQuestion: TradeItSecurityQuestion) {
                                                Log.d(TAG, "Single onSecurityQuestion")
                                                callback.onSecurityQuestion(securityQuestion, this)
                                            }

                                            override fun onSuccess(type: List<TradeItLinkedBrokerAccountParcelable>) {
                                                Log.d(TAG, "Single onSuccess")
                                                emmiter.onSuccess(linkedBrokerParcelable)
                                            }

                                            override fun onError(error: TradeItErrorResult) {
                                                Log.d(TAG, "Single onError" + error.toString())
                                                emmiter.onError(RuntimeException(error.toString()))
                                            }

                                            override fun cancelSecurityQuestion() {
                                                Log.d(TAG, "Single cancelSecurityQuestion")
                                                emmiter.onSuccess(linkedBrokerParcelable)
                                            }
                                        }
                                )
                            }
                        },
                        true
                ).subscribe(
                        object : DisposableObserver<TradeItLinkedBrokerParcelable>() {
                            override fun onNext(@NonNull linkedBrokerParcelable: TradeItLinkedBrokerParcelable) {
                                Log.d(TAG, "authenticateAll - onNext: $linkedBrokerParcelable")
                            }

                            override fun onError(@NonNull e: Throwable) {
                                Log.d(TAG, "authenticateAll - onError " + e.message)
                                callback.onFinished()
                            }

                            override fun onComplete() {
                                Log.d(TAG, "authenticateAll - Oncomplete")
                                callback.onFinished()
                            }
                        }
                )
    }

    fun refreshAccountBalances(callback: TradeItCallBackCompletion) {
        Observable.fromIterable(this.getLinkedBrokers())
                .observeOn(AndroidSchedulers.mainThread(), true)
                .subscribeOn(Schedulers.io())
                .flatMapSingle(
                        Function<TradeItLinkedBrokerParcelable, Single<TradeItLinkedBrokerParcelable>> { linkedBrokerParcelable ->
                            Single.create { emmiter ->
                                linkedBrokerParcelable.refreshAccountBalances(
                                    object: TradeItCallBackCompletion {
                                        override fun onFinished() {
                                            emmiter.onSuccess(linkedBrokerParcelable)
                                        }
                                    }
                                )
                            }
                        },
                        true
                ).subscribe(
                        object : DisposableObserver<TradeItLinkedBrokerParcelable>() {
                            override fun onNext(@NonNull linkedBrokerParcelable: TradeItLinkedBrokerParcelable) {
                                Log.d(TAG, "refreshAccountBalances onNext: $linkedBrokerParcelable")
                            }

                            override fun onError(@NonNull e: Throwable) {
                                Log.d(TAG, "refreshAccountBalances onError: $e")
                                callback.onFinished()
                            }

                            override fun onComplete() {
                                Log.d(TAG, "refreshAccountBalances oncomplete")
                                callback.onFinished()
                            }
                        }
                )
    }

    fun getAvailableBrokers(callback: TradeItCallback<List<Broker>>) {
        this.getAvailableBrokersSingleCache().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                { brokers -> callback.onSuccess(brokers) },
                { throwable ->
                    Log.e(TAG, "getAvailableBrokers error: " + throwable.message)
                    callback.onError(TradeItErrorResultParcelable(throwable))
                }
        )
    }

    @Synchronized
    private fun getAvailableBrokersSingleCache(): SingleCache<List<Broker>> {
        val cache = this.availableBrokersSingleCache
        if (cache != null) {
            return cache
        } else {
            val single = Single.create(
                    SingleOnSubscribe<List<Broker>> { emitter ->
                        RxJavaPlugins.setErrorHandler(
                                Consumer { e ->
                                    var e = e
                                    if (e is UndeliverableException) {
                                        e = e.cause!!
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
                                }
                        )

                        apiClient.getAvailableBrokers(
                                object : TradeItCallback<List<Broker>> {
                                    override fun onSuccess(brokersList: List<Broker>) {
                                        emitter.onSuccess(brokersList)
                                    }

                                    override fun onError(error: TradeItErrorResult) {
                                        Log.e(TAG, error.toString())
                                        availableBrokersSingleCache = null
                                        emitter.onError(RuntimeException(error.toString()))
                                    }
                                }
                        )
                    }
            )

            val singleCache = SingleCache(single)
            singleCache.subscribe()
            this.availableBrokersSingleCache = singleCache

            return singleCache
        }
    }

    fun getAllFeaturedBrokers(callback: TradeItCallback<List<Broker>>) {
        getAvailableBrokers(
                object : TradeItCallback<List<Broker>> {
                    override fun onSuccess(brokersList: List<Broker>) {
                        callback.onSuccess(getFeaturedBrokerList(brokersList))
                    }

                    override fun onError(error: TradeItErrorResult) {
                        callback.onError(error)
                    }
                }
        )
    }

    fun getFeaturedBrokersForInstrumentType(
            instrumentType: Instrument,
            callback: TradeItCallback<List<Broker>>
    ) {
        getAllFeaturedBrokers(
                object : TradeItCallback<List<Broker>> {
                    override fun onSuccess(brokersList: List<Broker>) {
                        callback.onSuccess(getBrokerListForInstrumentType(brokersList, instrumentType))
                    }

                    override fun onError(error: TradeItErrorResult) {
                        callback.onError(error)
                    }
                }
        )
    }

    fun getAllNonFeaturedBrokers(callback: TradeItCallback<List<Broker>>) {
        getAvailableBrokers(
                object : TradeItCallback<List<Broker>> {
                    override fun onSuccess(brokersList: List<Broker>) {
                        callback.onSuccess(getNonFeaturedBrokerList(brokersList))
                    }

                    override fun onError(error: TradeItErrorResult) {
                        callback.onError(error)
                    }
                }
        )
    }

    fun getNonFeaturedBrokersForInstrumentType(
            instrumentType: Instrument,
            callback: TradeItCallback<List<Broker>>
    ) {
        getAllNonFeaturedBrokers(
                object : TradeItCallback<List<Broker>> {
                    override fun onSuccess(brokerList: List<Broker>) {
                        callback.onSuccess(getBrokerListForInstrumentType(brokerList, instrumentType))
                    }

                    override fun onError(error: TradeItErrorResult) {
                        callback.onError(error)
                    }
                }
        )
    }

    private fun getBrokerListForInstrumentType(
            brokersList: List<Broker>,
            instrumentType: Instrument
    ): List<Broker> {
        val filteredBrokerList = ArrayList<Broker>()
        for (broker in brokersList) {
            for (brokerInstrument in broker.brokerInstruments) {
                if (brokerInstrument.instrument == instrumentType) {
                    filteredBrokerList.add(broker)
                }
            }
        }

        return filteredBrokerList
    }

    private fun getFeaturedBrokerList(brokersList: List<Broker>): List<Broker> {
        val featuredBrokersList = ArrayList<Broker>()

        for (broker in brokersList) {
            for (instrument in broker.brokerInstruments) {
                if (instrument.isFeatured) {
                    featuredBrokersList.add(broker)
                }
            }
        }

        return featuredBrokersList
    }

    private fun getNonFeaturedBrokerList(brokersList: List<Broker>): List<Broker> {
        val nonFeaturedBrokersList = ArrayList<Broker>()

        for (broker in brokersList) {
            for (instrument in broker.brokerInstruments) {
                if (!instrument.isFeatured) {
                    nonFeaturedBrokersList.add(broker)
                }
            }
        }

        return nonFeaturedBrokersList
    }


    fun getOAuthLoginPopupUrl(
            broker: String,
            deepLinkCallback: String,
            callback: TradeItCallback<String>
    ) {
        apiClient.getOAuthLoginPopupUrlForMobile(
                broker,
                deepLinkCallback,
                object : TradeItCallback<String> {
                    override fun onSuccess(oAuthURL: String) {
                        callback.onSuccess(oAuthURL)
                    }

                    override fun onError(error: TradeItErrorResult) {
                        val errorResultParcelable = TradeItErrorResultParcelable(error)
                        callback.onError(errorResultParcelable)
                    }
                }
        )
    }

    fun getOAuthLoginPopupForTokenUpdateUrl(
            linkedBroker: TradeItLinkedBrokerParcelable,
            deepLinkCallback: String,
            callback: TradeItCallback<String>
    ) {
        apiClient.getOAuthLoginPopupUrlForTokenUpdate(
                linkedBroker.brokerName,
                linkedBroker.linkedLogin!!.userId,
                linkedBroker.linkedLogin!!.userToken,
                deepLinkCallback,
                object : TradeItCallback<String> {
                    override fun onSuccess(oAuthUrl: String) {
                        callback.onSuccess(oAuthUrl)
                    }

                    override fun onError(error: TradeItErrorResult) {
                        val errorResultParcelable = TradeItErrorResultParcelable(error)
                        callback.onError(errorResultParcelable)
                    }
                }
        )
    }

    fun getOAuthLoginPopupForTokenUpdateUrlByUserId(
            userId: String,
            deepLinkCallback: String,
            callback: TradeItCallback<String>
    ) {
        val linkedBroker = getLinkedBrokerByUserId(userId)

        if (linkedBroker == null) {
            callback.onError(
                    TradeItErrorResultParcelable(
                            "getOAuthLoginPopupForTokenUpdateUrlByUserId error",
                            "No linked broker was found for userId $userId"
                    )
            )
        } else {
            getOAuthLoginPopupForTokenUpdateUrl(linkedBroker, deepLinkCallback, callback)
        }
    }

    fun linkBrokerWithOauthVerifier(accountLabel: String, oAuthVerifier: String, callback: TradeItCallback<TradeItLinkedBrokerParcelable>) {
        apiClient.linkBrokerWithOauthVerifier(
                oAuthVerifier,
                object : TradeItCallback<TradeItLinkedLogin> {
                    override fun onSuccess(linkedLogin: TradeItLinkedLogin) {
                        try {
                            val linkedLoginParcelable = TradeItLinkedLoginParcelable(linkedLogin)

                            val apiClientParcelable = TradeItApiClientParcelable(
                                    apiClient.apiKey,
                                    apiClient.environment,
                                    apiClient.requestInterceptorParcelable
                            )

                            var linkedBroker = TradeItLinkedBrokerParcelable(
                                    apiClientParcelable,
                                    linkedLoginParcelable,
                                    linkedBrokerCache
                            )

                            val indexOfLinkedBroker = linkedBrokers.indexOf(linkedBroker)

                            if (indexOfLinkedBroker != -1) { //linked broker for this user id already exists, this is a token update
                                val linkedBrokerToUpdate = linkedBrokers[indexOfLinkedBroker]
                                linkedBrokerToUpdate.linkedLogin = linkedLoginParcelable
                                linkedBroker = linkedBrokerToUpdate
                                keystoreService.updateLinkedLogin(linkedLoginParcelable)
                            } else {
                                keystoreService.saveLinkedLogin(linkedLoginParcelable, accountLabel)
                                linkedBrokers.add(linkedBroker)
                            }

                            callback.onSuccess(linkedBroker)
                        } catch (e: TradeItSaveLinkedLoginException) {
                            Log.e(this.javaClass.getName(), e.message, e)
                            callback.onError(TradeItErrorResultParcelable("Failed to link broker", e.message ?: ""))
                        } catch (e: TradeItUpdateLinkedLoginException) {
                            Log.e(this.javaClass.getName(), e.message, e)
                            callback.onError(TradeItErrorResultParcelable("Failed to update link broker", e.message ?: ""))
                        }

                    }

                    override fun onError(error: TradeItErrorResult) {
                        val errorResultParcelable = TradeItErrorResultParcelable(error)
                        callback.onError(errorResultParcelable)
                    }
                }
        )
    }

    fun unlinkBroker(
            linkedBroker: TradeItLinkedBrokerParcelable,
            callback: TradeItCallback<TradeItResponse>
    ) {
        try {
            var linkedLogin = linkedBroker.linkedLogin?.let { linkedLogin ->
                keystoreService.deleteLinkedLogin(linkedLogin)
                linkedLogin
            }
            linkedBrokers.remove(linkedBroker)
            linkedBrokerCache.removeFromCache(linkedBroker)
            apiClient.unlinkBrokerAccount(
                    linkedLogin,
                    object : TradeItCallback<TradeItResponse> {
                        override fun onSuccess(response: TradeItResponse) {
                            callback.onSuccess(response)
                        }

                        override fun onError(error: TradeItErrorResult) {
                            val errorResultParcelable = TradeItErrorResultParcelable(error)
                            callback.onError(errorResultParcelable)
                        }
                    }
            )
        } catch (e: TradeItDeleteLinkedLoginException) {
            Log.e(this.javaClass.getName(), e.message, e)
            callback.onError(
                    TradeItErrorResultParcelable(
                            "Unlink broker error",
                            "An error occured while unlinking the broker, please try again later"
                    )
            )
        }

    }

    fun getLinkedBrokerByUserId(userId: String): TradeItLinkedBrokerParcelable? {
        var linkedBrokerParcelable: TradeItLinkedBrokerParcelable? = null
        for (linkedBroker in this.getLinkedBrokers()) {
            if (linkedBroker.linkedLogin!!.userId == userId) {
                linkedBrokerParcelable = linkedBroker
                break
            }
        }

        return linkedBrokerParcelable
    }

    fun unlinkBrokerByUserId(userId: String, callback: TradeItCallback<TradeItResponse>) {
        val linkedBroker = getLinkedBrokerByUserId(userId)

        if (linkedBroker == null) {
            callback.onError(
                    TradeItErrorResultParcelable(
                            "Unlink broker error",
                            "No linked broker was found for userId $userId"
                    )
            )
        } else {
            unlinkBroker(linkedBroker, callback)
        }
    }


    @Deprecated("Use the new OAuth flow and the #linkBrokerWithOauthVerifier(String, String, String, TradeItCallback) method instead")
    fun linkBroker(
            accountLabel: String,
            broker: String,
            username: String,
            password: String,
            callback: TradeItCallback<TradeItLinkedBrokerParcelable>
    ) {
        val errorResultParcelable = TradeItErrorResultParcelable(
                "Couldn't complete your request",
                "Failed to link broker"
        )
        callback.onError(errorResultParcelable)
    }

    fun getLinkedBrokers(): MutableList<TradeItLinkedBrokerParcelable> {
        return linkedBrokers
    }

    companion object {
        private val TAG = TradeItLinkedBrokerManager::class.java!!.getName()
    }
}
