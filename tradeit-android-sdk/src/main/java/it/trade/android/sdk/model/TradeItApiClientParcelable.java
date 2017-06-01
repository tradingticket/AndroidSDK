package it.trade.android.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import it.trade.api.TradeItApi;
import it.trade.api.TradeItApiClient;
import it.trade.model.request.TradeItEnvironment;
import it.trade.model.request.TradeItRequestWithKey;

public class TradeItApiClientParcelable extends TradeItApiClient implements Parcelable {

    private RequestCookieProviderParcelable requestCookieProviderParcelable;

    public TradeItApiClientParcelable(String apiKey, TradeItEnvironment environment) {
        this(apiKey, environment, null);
    }
    public TradeItApiClientParcelable(String apiKey, TradeItEnvironment environment, RequestCookieProviderParcelable requestCookieProviderParcelable) {
        super(apiKey, environment, requestCookieProviderParcelable);
        this.requestCookieProviderParcelable = requestCookieProviderParcelable;
    }

    protected TradeItApiClientParcelable(TradeItApi tradeItApi) {
        super(tradeItApi);
    }

    public RequestCookieProviderParcelable getRequestCookieProviderParcelable() {
        return requestCookieProviderParcelable;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.requestCookieProviderParcelable, flags);
        dest.writeString(this.serverUuid);
        dest.writeString(this.sessionToken);
        dest.writeInt(this.environment == null ? -1 : this.environment.ordinal());
        dest.writeString(this.apiKey);
    }

    protected TradeItApiClientParcelable(Parcel in) {
        this.requestCookieProviderParcelable = in.readParcelable(RequestCookieProviderParcelable.class.getClassLoader());
        this.serverUuid = in.readString();
        this.sessionToken = in.readString();
        int tmpEnvironment = in.readInt();
        this.environment = tmpEnvironment == -1 ? null : TradeItEnvironment.values()[tmpEnvironment];
        this.apiKey = in.readString();
        TradeItRequestWithKey.API_KEY = apiKey;
        this.tradeItApi = this.createTradeItApi(environment, requestCookieProviderParcelable);
    }

    public static final Parcelable.Creator<TradeItApiClientParcelable> CREATOR = new Parcelable.Creator<TradeItApiClientParcelable>() {
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
