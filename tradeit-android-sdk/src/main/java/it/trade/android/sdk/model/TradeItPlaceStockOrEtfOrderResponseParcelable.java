package it.trade.android.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import it.trade.model.reponse.TradeItPlaceStockOrEtfOrderResponse;

public class TradeItPlaceStockOrEtfOrderResponseParcelable implements Parcelable {

    String broker;
    String confirmationMessage;
    String orderNumber;
    String timestamp;
    TradeItOrderInfoParcelable orderInfo;

    TradeItPlaceStockOrEtfOrderResponseParcelable(TradeItPlaceStockOrEtfOrderResponse response) {
        this.broker = response.broker;
        this.confirmationMessage = response.confirmationMessage;
        this.orderNumber = response.orderNumber;
        this.timestamp = response.timestamp;
        this.orderInfo = new TradeItOrderInfoParcelable(response.orderInfo);
    }

    TradeItPlaceStockOrEtfOrderResponseParcelable() {}

    public String getBroker() {
        return broker;
    }

    public String getConfirmationMessage() {
        return confirmationMessage;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public TradeItOrderInfoParcelable getOrderInfo() {
        return orderInfo;
    }

    @Override
    public String toString() {
        return "TradeItPlaceStockOrEtfOrderResponseParcelable{" +
                "broker='" + broker + '\'' +
                ", confirmationMessage='" + confirmationMessage + '\'' +
                ", orderNumber='" + orderNumber + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", orderInfo=" + orderInfo +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.broker);
        dest.writeString(this.confirmationMessage);
        dest.writeString(this.orderNumber);
        dest.writeString(this.timestamp);
        dest.writeParcelable(this.orderInfo, flags);
    }

    protected TradeItPlaceStockOrEtfOrderResponseParcelable(Parcel in) {
        this.broker = in.readString();
        this.confirmationMessage = in.readString();
        this.orderNumber = in.readString();
        this.timestamp = in.readString();
        this.orderInfo = in.readParcelable(TradeItOrderInfoParcelable.class.getClassLoader());
    }

    public static final Parcelable.Creator<TradeItPlaceStockOrEtfOrderResponseParcelable> CREATOR = new Parcelable.Creator<TradeItPlaceStockOrEtfOrderResponseParcelable>() {
        @Override
        public TradeItPlaceStockOrEtfOrderResponseParcelable createFromParcel(Parcel source) {
            return new TradeItPlaceStockOrEtfOrderResponseParcelable(source);
        }

        @Override
        public TradeItPlaceStockOrEtfOrderResponseParcelable[] newArray(int size) {
            return new TradeItPlaceStockOrEtfOrderResponseParcelable[size];
        }
    };
}
