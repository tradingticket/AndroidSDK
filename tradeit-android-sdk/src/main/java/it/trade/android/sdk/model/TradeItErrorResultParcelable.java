package it.trade.android.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import it.trade.model.TradeItErrorResult;
import it.trade.model.reponse.TradeItErrorCode;

public class TradeItErrorResultParcelable extends TradeItErrorResult implements Parcelable {

    public TradeItErrorResultParcelable(TradeItErrorResult errorResult) {
        this.shortMessage = errorResult.getShortMessage();
        this.longMessages = errorResult.getLongMessages();
        this.systemMessage = errorResult.getSystemMessage();
        this.errorCode = errorResult.getErrorCode();
        this.httpCode = errorResult.getHttpCode();
    }

    public TradeItErrorResultParcelable(Throwable throwable) {
        this.systemMessage = throwable.getMessage();
    }

    public TradeItErrorResultParcelable(TradeItErrorCode errorCode, String shortMessage, List<String> longMessages) {
        super(errorCode, shortMessage, longMessages);
    }

    public TradeItErrorResultParcelable(String title, String message) {
        super(title, message);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.errorCode == null ? -1 : this.errorCode.ordinal());
        dest.writeString(this.shortMessage);
        dest.writeStringList(this.longMessages);
        dest.writeString(this.systemMessage);
        dest.writeInt(this.httpCode);
    }

    public TradeItErrorResultParcelable() {
    }

    protected TradeItErrorResultParcelable(Parcel in) {
        int tmpErrorCode = in.readInt();
        this.errorCode = tmpErrorCode == -1 ? null : TradeItErrorCode.values()[tmpErrorCode];
        this.shortMessage = in.readString();
        this.longMessages = in.createStringArrayList();
        this.systemMessage = in.readString();
        this.httpCode = in.readInt();
    }

    public static final Parcelable.Creator<TradeItErrorResultParcelable> CREATOR = new Parcelable.Creator<TradeItErrorResultParcelable>() {
        @Override
        public TradeItErrorResultParcelable createFromParcel(Parcel source) {
            return new TradeItErrorResultParcelable(source);
        }

        @Override
        public TradeItErrorResultParcelable[] newArray(int size) {
            return new TradeItErrorResultParcelable[size];
        }
    };
}
