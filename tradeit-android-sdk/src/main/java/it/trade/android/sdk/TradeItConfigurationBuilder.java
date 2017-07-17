package it.trade.android.sdk;

import android.content.Context;

import it.trade.android.sdk.model.RequestInterceptorParcelable;
import it.trade.model.request.TradeItEnvironment;

public class TradeItConfigurationBuilder {

    private Context context;
    private String apiKey;
    private TradeItEnvironment environment;
    private String baseUrl;
    private RequestInterceptorParcelable requestInterceptorParcelable;

    private TradeItConfigurationBuilder() {
    }

    public TradeItConfigurationBuilder(Context context, String apiKey, TradeItEnvironment environment) {
        this.context = context;
        this.apiKey = apiKey;
        this.environment = environment;
    }

    public TradeItConfigurationBuilder withBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public TradeItConfigurationBuilder withRequestInterceptor(RequestInterceptorParcelable requestInterceptorParcelable) {
        this.requestInterceptorParcelable = requestInterceptorParcelable;
        return this;
    }

    public Context getContext() {
        return context;
    }

    public String getApiKey() {
        return apiKey;
    }

    public TradeItEnvironment getEnvironment() {
        return environment;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public RequestInterceptorParcelable getRequestInterceptorParcelable() {
        return requestInterceptorParcelable;
    }
}
