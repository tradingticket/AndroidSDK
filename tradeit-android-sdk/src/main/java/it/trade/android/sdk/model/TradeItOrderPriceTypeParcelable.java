package it.trade.android.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import it.trade.android.sdk.enums.TradeItOrderPriceType;

public class TradeItOrderPriceTypeParcelable implements Parcelable {
    private TradeItOrderPriceType priceType;
    private String displayLabel;

    TradeItOrderPriceTypeParcelable(TradeItOrderPriceType priceType, String displayLabel) {
        this.priceType = priceType;
        this.displayLabel = displayLabel;
    }

    public TradeItOrderPriceType getPriceType() {
        return priceType;
    }

    public void setPriceType(TradeItOrderPriceType priceType) {
        this.priceType = priceType;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    public void setDisplayLabel(String displayLabel) {
        this.displayLabel = displayLabel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TradeItOrderPriceTypeParcelable that = (TradeItOrderPriceTypeParcelable) o;

        if (priceType != that.priceType) return false;
        return displayLabel != null ? displayLabel.equals(that.displayLabel) : that.displayLabel == null;
    }

    @Override
    public int hashCode() {
        int result = priceType != null ? priceType.hashCode() : 0;
        result = 31 * result + (displayLabel != null ? displayLabel.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.priceType == null ? -1 : this.priceType.ordinal());
        dest.writeString(this.displayLabel);
    }

    protected TradeItOrderPriceTypeParcelable(Parcel in) {
        int tmpPriceType = in.readInt();
        this.priceType = tmpPriceType == -1 ? null : TradeItOrderPriceType.values()[tmpPriceType];
        this.displayLabel = in.readString();
    }

    public static final Parcelable.Creator<TradeItOrderPriceTypeParcelable> CREATOR = new Parcelable.Creator<TradeItOrderPriceTypeParcelable>() {
        @Override
        public TradeItOrderPriceTypeParcelable createFromParcel(Parcel source) {
            return new TradeItOrderPriceTypeParcelable(source);
        }

        @Override
        public TradeItOrderPriceTypeParcelable[] newArray(int size) {
            return new TradeItOrderPriceTypeParcelable[size];
        }
    };
}
