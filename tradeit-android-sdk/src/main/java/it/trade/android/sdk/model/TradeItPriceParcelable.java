package it.trade.android.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import it.trade.model.reponse.Price;

public class TradeItPriceParcelable implements Parcelable {

    String type;
    Integer limitPrice; //TODO update to Double in the java API
    Integer stopPrice; //TODO update to Double in the java API
    Double last;
    Double bid;
    Double ask;
    String timestamp;

    TradeItPriceParcelable(Price price) {
        this.type = price.type;
        this.limitPrice = price.limitPrice;
        this.stopPrice = price.stopPrice;
        this.last = price.last;
        this.bid = price.bid;
        this.ask = price.ask;
        this.timestamp = price.timestamp;
    }

    TradeItPriceParcelable() {}

    public String getType() {
        return type;
    }

    public Integer getLimitPrice() {
        return limitPrice;
    }

    public Integer getStopPrice() {
        return stopPrice;
    }

    public Double getLast() {
        return last;
    }

    public Double getBid() {
        return bid;
    }

    public Double getAsk() {
        return ask;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "TradeItPriceParcelable{" +
                "type='" + type + '\'' +
                ", limitPrice=" + limitPrice +
                ", stopPrice=" + stopPrice +
                ", last=" + last +
                ", bid=" + bid +
                ", ask=" + ask +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.type);
        dest.writeValue(this.limitPrice);
        dest.writeValue(this.stopPrice);
        dest.writeValue(this.last);
        dest.writeValue(this.bid);
        dest.writeValue(this.ask);
        dest.writeString(this.timestamp);
    }

    protected TradeItPriceParcelable(Parcel in) {
        this.type = in.readString();
        this.limitPrice = (Integer) in.readValue(Integer.class.getClassLoader());
        this.stopPrice = (Integer) in.readValue(Integer.class.getClassLoader());
        this.last = (Double) in.readValue(Double.class.getClassLoader());
        this.bid = (Double) in.readValue(Double.class.getClassLoader());
        this.ask = (Double) in.readValue(Double.class.getClassLoader());
        this.timestamp = in.readString();
    }

    public static final Parcelable.Creator<TradeItPriceParcelable> CREATOR = new Parcelable.Creator<TradeItPriceParcelable>() {
        @Override
        public TradeItPriceParcelable createFromParcel(Parcel source) {
            return new TradeItPriceParcelable(source);
        }

        @Override
        public TradeItPriceParcelable[] newArray(int size) {
            return new TradeItPriceParcelable[size];
        }
    };
}
