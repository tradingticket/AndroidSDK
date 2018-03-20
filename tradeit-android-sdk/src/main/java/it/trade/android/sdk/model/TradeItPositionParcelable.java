package it.trade.android.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import it.trade.model.reponse.TradeItPosition;

public class TradeItPositionParcelable extends TradeItPosition implements Parcelable {

    public TradeItPositionParcelable(TradeItPosition position) {
        this.costbasis = position.costbasis;
        this.holdingType = position.holdingType;
        this.lastPrice = position.lastPrice;
        this.quantity = position.quantity;
        this.symbol = position.symbol;
        this.symbolClass = position.symbolClass;
        this.todayGainLossDollar = position.todayGainLossDollar;
        this.todayGainLossPercentage = position.totalGainLossPercentage;
        this.totalGainLossDollar = position.totalGainLossDollar;
        this.totalGainLossPercentage = position.totalGainLossPercentage;
        this.todayGainLossAbsolute = position.todayGainLossAbsolute;
        this.totalGainLossAbsolute = position.totalGainLossAbsolute;
        this.exchange = position.exchange;
        this.currency = position.currency;
    }

    protected TradeItPositionParcelable() {
        super();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.costbasis);
        dest.writeString(this.holdingType);
        dest.writeValue(this.lastPrice);
        dest.writeValue(this.quantity);
        dest.writeString(this.symbol);
        dest.writeString(this.symbolClass);
        dest.writeValue(this.todayGainLossDollar);
        dest.writeValue(this.todayGainLossPercentage);
        dest.writeValue(this.totalGainLossDollar);
        dest.writeValue(this.totalGainLossPercentage);
        dest.writeValue(this.todayGainLossAbsolute);
        dest.writeValue(this.totalGainLossAbsolute);
        dest.writeString(this.exchange);
        dest.writeString(this.currency);
    }

    protected TradeItPositionParcelable(Parcel in) {
        this.costbasis = (Double) in.readValue(Double.class.getClassLoader());
        this.holdingType = in.readString();
        this.lastPrice = (Double) in.readValue(Double.class.getClassLoader());
        this.quantity = (Double) in.readValue(Double.class.getClassLoader());
        this.symbol = in.readString();
        this.symbolClass = in.readString();
        this.todayGainLossDollar = (Double) in.readValue(Double.class.getClassLoader());
        this.todayGainLossPercentage = (Double) in.readValue(Double.class.getClassLoader());
        this.totalGainLossDollar = (Double) in.readValue(Double.class.getClassLoader());
        this.totalGainLossPercentage = (Double) in.readValue(Double.class.getClassLoader());
        this.todayGainLossAbsolute = (Double) in.readValue(Double.class.getClassLoader());
        this.totalGainLossAbsolute = (Double) in.readValue(Double.class.getClassLoader());
        this.exchange = in.readString();
        this.currency = in.readString();
    }

    public static final Parcelable.Creator<TradeItPositionParcelable> CREATOR = new Parcelable.Creator<TradeItPositionParcelable>() {
        @Override
        public TradeItPositionParcelable createFromParcel(Parcel source) {
            return new TradeItPositionParcelable(source);
        }

        @Override
        public TradeItPositionParcelable[] newArray(int size) {
            return new TradeItPositionParcelable[size];
        }
    };
}
