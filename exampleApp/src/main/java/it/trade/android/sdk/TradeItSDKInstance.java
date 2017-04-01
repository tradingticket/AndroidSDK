package it.trade.android.sdk;

import android.content.Context;

import it.trade.android.sdk.manager.TradeItLinkedBrokerManager;
import it.trade.android.sdk.model.TradeItLinkedBrokerCache;
import it.trade.tradeitapi.model.TradeItEnvironment;

public class TradeItSDKInstance {
    public TradeItSDKInstance(Context context, String apiKey, TradeItEnvironment environment) {
        TradeItSDK.clearConfig();
        TradeItSDK.configure(context, apiKey, environment);
    }

    public TradeItLinkedBrokerManager getLinkedBrokerManager() {
        return TradeItSDK.getLinkedBrokerManager();
    }

    public TradeItEnvironment getEnvironment() {
        return TradeItSDK.getEnvironment();
    }

    public String getApiKey() {
        return TradeItSDK.getApiKey();
    }

    public TradeItLinkedBrokerCache getLinkedBrokerCache() {
        return TradeItSDK.getLinkedBrokerCache();
    }

    public Context getContext() {
        return TradeItSDK.getContext();
    }
}
