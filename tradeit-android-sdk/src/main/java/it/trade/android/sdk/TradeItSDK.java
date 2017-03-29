package it.trade.android.sdk;


import android.content.Context;
import android.util.Log;

import it.trade.android.sdk.exceptions.TradeItSDKConfigurationException;
import it.trade.android.sdk.manager.TradeItLinkedBrokerManager;
import it.trade.android.sdk.model.TradeItLinkedBrokerCache;
import it.trade.tradeitapi.exception.TradeItKeystoreServiceCreateKeyException;
import it.trade.tradeitapi.exception.TradeItRetrieveLinkedLoginException;
import it.trade.tradeitapi.model.TradeItEnvironment;

public class TradeItSDK {
    private static String apiKey;
    private static TradeItEnvironment environment;
    private static boolean isConfigured;
    private static TradeItLinkedBrokerManager linkedBrokerManager;
    private static Context context;
    private static TradeItLinkedBrokerCache linkedBrokerCache;

    public static void configure(Context context, String apiKey, TradeItEnvironment environment) {
        if (!isConfigured) {
            isConfigured = true;
            TradeItSDK.context = context;
            TradeItSDK.apiKey = apiKey;
            TradeItSDK.environment = environment;
            linkedBrokerCache = new TradeItLinkedBrokerCache(context);
            try {
                linkedBrokerManager = new TradeItLinkedBrokerManager(context);
            } catch (TradeItKeystoreServiceCreateKeyException e) {
                throw new TradeItSDKConfigurationException("Error initializing TradeItLinkedBrokerManager: ", e);
            } catch (TradeItRetrieveLinkedLoginException e) {
                throw new TradeItSDKConfigurationException("Error initializing TradeItLinkedBrokerManager: ", e);
            }
        } else {
            Log.w("TradeItSDK", "Warning: TradeItSDK.configure() called multiple times. Ignoring.");
        }
    }

    public static void clearConfig() {
        isConfigured = false;
    }

    public static TradeItLinkedBrokerManager getLinkedBrokerManager() throws TradeItSDKConfigurationException {
        if (linkedBrokerManager == null) {
            throw new TradeItSDKConfigurationException("ERROR: TradeItSDK.linkedBrokerManager referenced before calling TradeItSDK.configure()!");
        }
        return linkedBrokerManager;
    }

    public static TradeItEnvironment getEnvironment() {
        if (environment == null) {
            throw new TradeItSDKConfigurationException("ERROR: TradeItSDK.linkedBrokerManager referenced before calling TradeItSDK.configure()!");
        }
        return environment;
    }

    public static String getApiKey() {
        if (apiKey == null) {
            throw new TradeItSDKConfigurationException("ERROR: TradeItSDK.apiKey referenced before calling TradeItSDK.configure()!");
        }
        return apiKey;
    }

    public static TradeItLinkedBrokerCache getLinkedBrokerCache() {
        if (linkedBrokerCache == null) {
            throw new TradeItSDKConfigurationException("ERROR: TradeItSDK.linkedBrokerCache referenced before calling TradeItSDK.configure()!");
        }
        return linkedBrokerCache;
    }

    public static Context getContext() {
        if (context == null) {
            throw new TradeItSDKConfigurationException("ERROR: TradeItSDK.context referenced before calling TradeItSDK.configure()!");
        }
        return context;
    }
}
