package it.trade.android.sdk.model;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import it.trade.api.StatelessTradeItApiClient;
import it.trade.api.TradeItApiClient;
import it.trade.model.request.TradeItEnvironment;

public class TradeItApiClientParcelable extends TradeItApiClient implements Parcelable {

    private RequestInterceptorParcelable requestInterceptorParcelable;

    public TradeItApiClientParcelable(String apiKey, TradeItEnvironment environment) {
        this(apiKey, environment, null);
    }
    public TradeItApiClientParcelable(String apiKey, TradeItEnvironment environment, RequestInterceptorParcelable requestInterceptorParcelable) {
        super(apiKey, environment, requestInterceptorParcelable, forceTLS12());
        this.requestInterceptorParcelable = requestInterceptorParcelable;
    }

    protected TradeItApiClientParcelable(String apiKey, StatelessTradeItApiClient statelessTradeItApiClient) {
        super(apiKey, statelessTradeItApiClient);
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
        dest.writeParcelable(this.requestInterceptorParcelable, flags);
        dest.writeString(this.serverUuid);
        dest.writeString(this.sessionToken);
        dest.writeInt(this.environment == null ? -1 : this.environment.ordinal());
        dest.writeString(this.apiKey);
    }

    protected TradeItApiClientParcelable(Parcel in) {
        this.requestInterceptorParcelable = in.readParcelable(RequestInterceptorParcelable.class.getClassLoader());
        this.serverUuid = in.readString();
        this.sessionToken = in.readString();
        int tmpEnvironment = in.readInt();
        this.environment = tmpEnvironment == -1 ? null : TradeItEnvironment.values()[tmpEnvironment];
        this.apiKey = in.readString();
        this.statelessTradeItApiClient = this.createStatelessTradeItApiClient(environment, requestInterceptorParcelable, forceTLS12());
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

    private static boolean forceTLS12() {
        return (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 22);
    }
}
