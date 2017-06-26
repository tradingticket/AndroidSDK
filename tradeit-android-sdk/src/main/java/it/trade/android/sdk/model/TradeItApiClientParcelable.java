package it.trade.android.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import it.trade.api.TradeItApi;
import it.trade.api.TradeItApiClient;
import it.trade.model.request.TradeItEnvironment;
import it.trade.model.request.TradeItRequestWithKey;

public class TradeItApiClientParcelable extends TradeItApiClient implements Parcelable {

    private RequestCookieProviderParcelable requestCookieProviderParcelable;
    private RequestInterceptorParcelable requestInterceptorParcelable;

    public TradeItApiClientParcelable(String apiKey, TradeItEnvironment environment) {
        this(apiKey, environment, null, null);
    }
    public TradeItApiClientParcelable(String apiKey, TradeItEnvironment environment, RequestCookieProviderParcelable requestCookieProviderParcelable, RequestInterceptorParcelable requestInterceptorParcelable) {
        super(apiKey, environment, requestCookieProviderParcelable, requestInterceptorParcelable);
        this.requestCookieProviderParcelable = requestCookieProviderParcelable;
        this.requestInterceptorParcelable = requestInterceptorParcelable;
    }

    protected TradeItApiClientParcelable(TradeItApi tradeItApi) {
        super(tradeItApi);
    }

    public RequestCookieProviderParcelable getRequestCookieProviderParcelable() {
        return requestCookieProviderParcelable;
    }

    public RequestInterceptorParcelable getRequestInterceptorParcelable() {
        return requestInterceptorParcelable;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.requestCookieProviderParcelable, flags);
        dest.writeParcelable(this.requestInterceptorParcelable, flags);
        dest.writeString(this.serverUuid);
        dest.writeString(this.sessionToken);
        dest.writeInt(this.environment == null ? -1 : this.environment.ordinal());
        dest.writeString(this.apiKey);
    }

    protected TradeItApiClientParcelable(Parcel in) {
        this.requestCookieProviderParcelable = in.readParcelable(RequestCookieProviderParcelable.class.getClassLoader());
        this.requestInterceptorParcelable = in.readParcelable(RequestInterceptorParcelable.class.getClassLoader());
        this.serverUuid = in.readString();
        this.sessionToken = in.readString();
        int tmpEnvironment = in.readInt();
        this.environment = tmpEnvironment == -1 ? null : TradeItEnvironment.values()[tmpEnvironment];
        this.apiKey = in.readString();
        TradeItRequestWithKey.API_KEY = apiKey;
        this.tradeItApi = this.createTradeItApi(environment, requestCookieProviderParcelable, requestInterceptorParcelable);
    }

    public static final Creator<TradeItApiClientParcelable> CREATOR = new Creator<TradeItApiClientParcelable>() {
        @Override
        public TradeItApiClientParcelable createFromParcel(Parcel source) {
            return new TradeItApiClientParcelable(source);
        }

        @Override
        public TradeItApiClientParcelable[] newArray(int size) {
            return new TradeItApiClientParcelable[size];
        }
    };
}
