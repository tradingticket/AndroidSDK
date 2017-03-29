package it.trade.android.sdk;

import android.content.Context;
import android.support.annotation.WorkerThread;

import it.trade.android.sdk.exceptions.TradeItSDKConfigurationException;
import it.trade.android.sdk.manager.TradeItLinkedBrokerManager;
import it.trade.android.sdk.model.TradeItLinkedBrokerCache;
import it.trade.tradeitapi.exception.TradeItKeystoreServiceCreateKeyException;
import it.trade.tradeitapi.exception.TradeItRetrieveLinkedLoginException;
import it.trade.tradeitapi.model.TradeItEnvironment;

/**
 * Created by sanbeg on 3/29/17.
 */

public class TradeItSdk2 {
    private final String apiKey;
    private final TradeItEnvironment environment;
    private TradeItLinkedBrokerManager linkedBrokerManager;
    private final TradeItLinkedBrokerCache linkedBrokerCache;

    public TradeItSdk2(Context context, String apiKey, TradeItEnvironment environment) {
        this.apiKey = apiKey;
        this.environment = environment;
        linkedBrokerCache = new TradeItLinkedBrokerCache(context);
    }

    @WorkerThread
    public void configure(Context context) {
        try {
            // On a fresh install, this could take multiple seconds to generate a key pair
            linkedBrokerManager = new TradeItLinkedBrokerManager(context, this);
        } catch (TradeItKeystoreServiceCreateKeyException e) {
            throw new TradeItSDKConfigurationException("Error initializing TradeItLinkedBrokerManager: ", e);
        } catch (TradeItRetrieveLinkedLoginException e) {
            throw new TradeItSDKConfigurationException("Error initializing TradeItLinkedBrokerManager: ", e);
        }
    }

    public TradeItLinkedBrokerManager getLinkedBrokerManager() throws TradeItSDKConfigurationException {
        if (linkedBrokerManager == null) {
            throw new TradeItSDKConfigurationException("ERROR: TradeItSDK2.linkedBrokerManager referenced before calling TradeItSDK2.configure()!");
        }
        return linkedBrokerManager;
    }

    public TradeItEnvironment getEnvironment() {
        return environment;
    }

    public String getApiKey() {
        return apiKey;
    }

    public TradeItLinkedBrokerCache getLinkedBrokerCache() {
        return linkedBrokerCache;
    }

}
