package it.trade.android.sdk

import it.trade.android.sdk.exceptions.TradeItKeystoreServiceCreateKeyException
import it.trade.android.sdk.exceptions.TradeItRetrieveLinkedLoginException
import it.trade.android.sdk.exceptions.TradeItSDKConfigurationException
import it.trade.android.sdk.internal.TradeItKeystoreService
import it.trade.android.sdk.manager.TradeItLinkedBrokerManager
import it.trade.android.sdk.model.TradeItApiClientParcelable
import it.trade.android.sdk.model.TradeItLinkedBrokerCache
import it.trade.model.request.TradeItEnvironment


class TradeItSdkInstance(configurationBuilder: TradeItConfigurationBuilder) {
    var environment: TradeItEnvironment? = null
        private set
    var linkedBrokerManager: TradeItLinkedBrokerManager? = null
        private set
    var linkedBrokerCache: TradeItLinkedBrokerCache? = null
        private set
    private var keyStoreService: TradeItKeystoreService? = null

    init {
        val baseUrl = configurationBuilder.baseUrl
        if (baseUrl != null && !baseUrl.isEmpty()) {
            configurationBuilder.environment.baseUrl = baseUrl
        }
        initializeTradeItSdkInstance(configurationBuilder)
    }

    private fun initializeTradeItSdkInstance(configurationBuilder: TradeItConfigurationBuilder) {
        this.environment = configurationBuilder.environment
        this.linkedBrokerCache = TradeItLinkedBrokerCache(configurationBuilder.context)

        try {
            this.keyStoreService = TradeItKeystoreService(configurationBuilder.context)
        } catch (e: TradeItKeystoreServiceCreateKeyException) {
            throw TradeItSDKConfigurationException("Error initializing TradeItKeystoreService: ", e)
        }

        try {
            linkedBrokerManager = TradeItLinkedBrokerManager(TradeItApiClientParcelable(configurationBuilder.apiKey, environment, configurationBuilder.requestInterceptorParcelable), linkedBrokerCache, keyStoreService, configurationBuilder.isPrefetchBrokerListEnabled)
        } catch (e: TradeItRetrieveLinkedLoginException) {
            throw TradeItSDKConfigurationException("Error initializing TradeItLinkedBrokerManager: ", e)
        }

    }
}
