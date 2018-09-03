package it.trade.android.sdk


import android.util.Log

import it.trade.android.sdk.exceptions.TradeItSDKConfigurationException
import it.trade.android.sdk.manager.TradeItLinkedBrokerManager
import it.trade.android.sdk.model.TradeItLinkedBrokerCache
import it.trade.model.request.TradeItEnvironment


object TradeItSDK {
    private var instance: TradeItSdkInstance? = null

    @JvmStatic
    val linkedBrokerManager: TradeItLinkedBrokerManager?
        @Throws(TradeItSDKConfigurationException::class)
        get() {
            if (instance == null) {
                throw TradeItSDKConfigurationException("ERROR: TradeItSDK.linkedBrokerManager referenced before calling TradeItSDK.configure()!")
            }
            return instance!!.linkedBrokerManager
        }

    val environment: TradeItEnvironment?
        get() {
            if (instance == null) {
                throw TradeItSDKConfigurationException("ERROR: TradeItSDK.linkedBrokerManager referenced before calling TradeItSDK.configure()!")
            }
            return instance!!.environment
        }

    val linkedBrokerCache: TradeItLinkedBrokerCache?
        get() {
            if (instance == null) {
                throw TradeItSDKConfigurationException("ERROR: TradeItSDK.linkedBrokerCache referenced before calling TradeItSDK.configure()!")
            }
            return instance!!.linkedBrokerCache
        }


    @JvmStatic
    fun configure(configurationBuilder: TradeItConfigurationBuilder) {
        if (instance == null) {
            instance = TradeItSdkInstance(configurationBuilder)
        } else {
            Log.w("TradeItSDK", "Warning: TradeItSDK.configure() called multiple times. Ignoring.")
        }
    }

    fun clearConfig() {
        instance = null
    }
}
