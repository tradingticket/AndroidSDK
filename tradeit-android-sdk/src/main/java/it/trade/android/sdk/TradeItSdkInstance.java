package it.trade.android.sdk;

import android.content.Context;

import it.trade.android.sdk.exceptions.TradeItSDKConfigurationException;
import it.trade.android.sdk.manager.TradeItLinkedBrokerManager;
import it.trade.android.sdk.model.TradeItLinkedBrokerCache;
import it.trade.tradeitapi.API.TradeItBrokerLinker;
import it.trade.tradeitapi.exception.TradeItKeystoreServiceCreateKeyException;
import it.trade.tradeitapi.exception.TradeItRetrieveLinkedLoginException;
import it.trade.tradeitapi.model.TradeItEnvironment;

public class TradeItSdkInstance {
    private String apiKey;
    private TradeItEnvironment environment;
    private TradeItLinkedBrokerManager linkedBrokerManager;
    private Context context;
    private TradeItLinkedBrokerCache linkedBrokerCache;
    private TradeItBrokerLinker brokerLinker;

    public TradeItSdkInstance(Context context, String apiKey, TradeItEnvironment environment) {
        this.context = context;
        this.apiKey = apiKey;
        this.environment = environment;
        this.linkedBrokerCache = new TradeItLinkedBrokerCache(context);

        try {
            this.brokerLinker = new TradeItBrokerLinker(apiKey, environment, context);
        } catch (TradeItKeystoreServiceCreateKeyException e) {
            throw new TradeItSDKConfigurationException("Error initializing TradeItBrokerLinker: ", e);
        }

        try {
            linkedBrokerManager = new TradeItLinkedBrokerManager(environment, linkedBrokerCache, brokerLinker);
        } catch (TradeItRetrieveLinkedLoginException e) {
            throw new TradeItSDKConfigurationException("Error initializing TradeItLinkedBrokerManager: ", e);
        }
    }

    public TradeItLinkedBrokerManager getLinkedBrokerManager() {
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

    public Context getContext() {
        return context;
    }
}
