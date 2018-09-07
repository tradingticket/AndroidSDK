package it.trade.android.sdk

import android.content.Context

import it.trade.android.sdk.model.RequestInterceptorParcelable
import it.trade.model.request.TradeItEnvironment

class TradeItConfigurationBuilder (
        val context: Context,
        val apiKey: String,
        val environment: TradeItEnvironment
    ) {

    var baseUrl: String? = null
        private set
    var requestInterceptorParcelable: RequestInterceptorParcelable? = null
        private set
    var isPrefetchBrokerListEnabled = true
        private set

    fun withBaseUrl(baseUrl: String): TradeItConfigurationBuilder {
        this.baseUrl = baseUrl
        return this
    }

    fun withRequestInterceptor(requestInterceptorParcelable: RequestInterceptorParcelable): TradeItConfigurationBuilder {
        this.requestInterceptorParcelable = requestInterceptorParcelable
        return this
    }

    fun disablePrefetchBrokerList(): TradeItConfigurationBuilder {
        this.isPrefetchBrokerListEnabled = false
        return this
    }
}
