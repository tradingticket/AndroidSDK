package it.trade.android.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import it.trade.model.reponse.OrderInfo;
import it.trade.model.reponse.Price;

public class TradeItOrderInfoParcelable implements Parcelable {
    String action;
    Double quantity;
    String symbol;
    TradeItPriceParcelable price;
    String expiration;

    TradeItOrderInfoParcelable(OrderInfo orderInfo) {
        this.action = orderInfo.action;
        this.quantity = orderInfo.quantity;
        this.symbol = orderInfo.symbol;
        this.price = new TradeItPriceParcelable(orderInfo.price);
        this.expiration = orderInfo.expiration;
    }

    TradeItOrderInfoParcelable() {}

    public String getAction() {
        return action;
    }

    public Double getQuantity() {
        return quantity;
    }

    public String getSymbol() {
        return symbol;
    }

    public TradeItPriceParcelable getPrice() {
        return price;
    }

    public String getExpiration() {
        return expiration;
    }

    @Override
    public String toString() {
        return "TradeItOrderInfoParcelable{" +
                "action='" + action + '\'' +
                ", quantity=" + quantity +
                ", symbol='" + symbol + '\'' +
                ", price=" + price +
                ", expiration='" + expiration + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.action);
        dest.writeValue(this.quantity);
        dest.writeString(this.symbol);
        dest.writeParcelable(this.price, flags);
        dest.writeString(this.expiration);
    }

    protected TradeItOrderInfoParcelable(Parcel in) {
        this.action = in.readString();
        this.quantity = (Double) in.readValue(Double.class.getClassLoader());
        this.symbol = in.readString();
        this.price = in.readParcelable(Price.class.getClassLoader());
        this.expiration = in.readString();
    }

    public static final Parcelable.Creator<TradeItOrderInfoParcelable> CREATOR = new Parcelable.Creator<TradeItOrderInfoParcelable>() {
        @Override
        public TradeItOrderInfoParcelable createFromParcel(Parcel source) {
            return new TradeItOrderInfoParcelable(source);
        }

        @Override
        public TradeItOrderInfoParcelable[] newArray(int size) {
            return new TradeItOrderInfoParcelable[size];
        }
    };
}
