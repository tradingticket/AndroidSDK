package it.trade.android.sdk;

import it.trade.android.sdk.exceptions.TradeItKeystoreServiceCreateKeyException;
import it.trade.android.sdk.exceptions.TradeItRetrieveLinkedLoginException;
import it.trade.android.sdk.exceptions.TradeItSDKConfigurationException;
import it.trade.android.sdk.internal.TradeItKeystoreService;
import it.trade.android.sdk.manager.TradeItLinkedBrokerManager;
import it.trade.android.sdk.model.TradeItApiClientParcelable;
import it.trade.android.sdk.model.TradeItLinkedBrokerCache;
import it.trade.model.request.TradeItEnvironment;


public class TradeItSdkInstance {
    private TradeItEnvironment environment;
    private TradeItLinkedBrokerManager linkedBrokerManager;
    private TradeItLinkedBrokerCache linkedBrokerCache;
    private TradeItKeystoreService keyStoreService;

    public TradeItSdkInstance(TradeItConfigurationBuilder configurationBuilder) {
        String baseUrl = configurationBuilder.getBaseUrl();
        if (baseUrl != null && !baseUrl.isEmpty()) {
            configurationBuilder.getEnvironment().setBaseUrl(baseUrl);
        }
        initializeTradeItSdkInstance(configurationBuilder);
    }

    private void initializeTradeItSdkInstance(TradeItConfigurationBuilder configurationBuilder) {
        this.environment = configurationBuilder.getEnvironment();
        this.linkedBrokerCache = new TradeItLinkedBrokerCache(configurationBuilder.getContext());

        try {
            this.keyStoreService = new TradeItKeystoreService(configurationBuilder.getContext());
        } catch (TradeItKeystoreServiceCreateKeyException e) {
            throw new TradeItSDKConfigurationException("Error initializing TradeItKeystoreService: ", e);
        }

        try {
            linkedBrokerManager = new TradeItLinkedBrokerManager(new TradeItApiClientParcelable(configurationBuilder.getApiKey(), environment, configurationBuilder.getRequestInterceptorParcelable()), linkedBrokerCache, keyStoreService, configurationBuilder.isPrefetchBrokerListEnabled());
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

    public TradeItLinkedBrokerCache getLinkedBrokerCache() {
        return linkedBrokerCache;
    }
}
