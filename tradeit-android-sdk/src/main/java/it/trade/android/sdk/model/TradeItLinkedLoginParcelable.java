package it.trade.android.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import it.trade.model.reponse.TradeItLinkLoginResponse;
import it.trade.model.reponse.TradeItOAuthAccessTokenResponse;
import it.trade.model.request.TradeItLinkLoginRequest;
import it.trade.model.request.TradeItLinkedLogin;
import it.trade.model.request.TradeItOAuthAccessTokenRequest;


public class TradeItLinkedLoginParcelable extends TradeItLinkedLogin implements Parcelable {
    public TradeItLinkedLoginParcelable(String broker, String userId, String userToken) {
        super(broker, userId, userToken);
    }

    public TradeItLinkedLoginParcelable(TradeItLinkLoginRequest linkLoginRequest, TradeItLinkLoginResponse linkLoginResponse) {
        super(linkLoginRequest, linkLoginResponse);
    }

    public TradeItLinkedLoginParcelable(TradeItOAuthAccessTokenRequest oAuthAccessTokenRequest, TradeItOAuthAccessTokenResponse oAuthAccessTokenResponse) {
        super(oAuthAccessTokenRequest, oAuthAccessTokenResponse);
    }

    public TradeItLinkedLoginParcelable(TradeItLinkedLogin linkedLogin) {
        this.broker = linkedLogin.broker;
        this.userId = linkedLogin.userId;
        this.userToken = linkedLogin.userToken;
        this.label = linkedLogin.label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TradeItLinkedLoginParcelable that = (TradeItLinkedLoginParcelable) o;

        return userId.equals(that.userId);

    }

    @Override
    public int hashCode() {
        return userId.hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.label);
        dest.writeString(this.broker);
        dest.writeString(this.userToken);
        dest.writeString(this.userId);
    }

    protected TradeItLinkedLoginParcelable(Parcel in) {
        this.label = in.readString();
        this.broker = in.readString();
        this.userToken = in.readString();
        this.userId = in.readString();
    }

    public static final Parcelable.Creator<TradeItLinkedLoginParcelable> CREATOR = new Parcelable.Creator<TradeItLinkedLoginParcelable>() {
        @Override
        public TradeItLinkedLoginParcelable createFromParcel(Parcel source) {
            return new TradeItLinkedLoginParcelable(source);
        }

        @Override
        public TradeItLinkedLoginParcelable[] newArray(int size) {
            return new TradeItLinkedLoginParcelable[size];
        }
    };
}
