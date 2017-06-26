package it.trade.android.sdk;

import android.content.Context;

import it.trade.android.sdk.exceptions.TradeItKeystoreServiceCreateKeyException;
import it.trade.android.sdk.exceptions.TradeItRetrieveLinkedLoginException;
import it.trade.android.sdk.exceptions.TradeItSDKConfigurationException;
import it.trade.android.sdk.internal.TradeItKeystoreService;
import it.trade.android.sdk.manager.TradeItLinkedBrokerManager;
import it.trade.android.sdk.model.RequestInterceptorParcelable;
import it.trade.android.sdk.model.TradeItApiClientParcelable;
import it.trade.android.sdk.model.TradeItLinkedBrokerCache;
import it.trade.model.request.TradeItEnvironment;


public class TradeItSdkInstance {
    private String apiKey;
    private TradeItEnvironment environment;
    private TradeItLinkedBrokerManager linkedBrokerManager;
    private Context context;
    private TradeItLinkedBrokerCache linkedBrokerCache;
    private TradeItKeystoreService keyStoreService;
    private static final String TRADE_IT_LINKED_BROKERS_ALIAS = "TRADE_IT_LINKED_BROKERS_ALIAS";

    public TradeItSdkInstance(TradeItConfigurationBuilder configurationBuilder) {
        String baseUrl = configurationBuilder.getBaseUrl();
        if (baseUrl != null && !baseUrl.isEmpty()) {
            environment.setBaseUrl(baseUrl);
        }
        initializeTradeItSdkInstance(configurationBuilder.getContext(), configurationBuilder.getApiKey(), configurationBuilder.getEnvironment(), configurationBuilder.getRequestInterceptorParcelable());
    }

    private void initializeTradeItSdkInstance(Context context, String apiKey, TradeItEnvironment environment, RequestInterceptorParcelable requestInterceptorParcelable) {
        this.context = context;
        this.apiKey = apiKey;
        this.environment = environment;
        this.linkedBrokerCache = new TradeItLinkedBrokerCache(context);

        try {
            this.keyStoreService = new TradeItKeystoreService(TRADE_IT_LINKED_BROKERS_ALIAS, context);
        } catch (TradeItKeystoreServiceCreateKeyException e) {
            throw new TradeItSDKConfigurationException("Error initializing TradeItBrokerLinker: ", e);
        }

        try {
            linkedBrokerManager = new TradeItLinkedBrokerManager(new TradeItApiClientParcelable(apiKey, environment, requestInterceptorParcelable), linkedBrokerCache, keyStoreService);
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
