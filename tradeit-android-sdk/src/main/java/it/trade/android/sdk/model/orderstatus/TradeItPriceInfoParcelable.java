package it.trade.android.sdk.model.orderstatus;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import it.trade.model.reponse.PriceInfo;

public class TradeItPriceInfoParcelable implements Parcelable {

    @SerializedName("conditionType")
    private String conditionType;

    @SerializedName("initialStopPrice")
    private Double initialStopPrice;

    @SerializedName("conditionSymbol")
    private String conditionSymbol;

    @SerializedName("trailPrice")
    private Double trailPrice;

    @SerializedName("conditionFollowPrice")
    private Double conditionFollowPrice;

    @SerializedName("limitPrice")
    private Double limitPrice;

    @SerializedName("triggerPrice")
    private Double triggerPrice;

    @SerializedName("conditionPrice")
    private Double conditionPrice;

    @SerializedName("bracketLimitPrice")
    private Double bracketLimitPrice;

    @SerializedName("type")
    private String type;

    @SerializedName("stopPrice")
    private Double stopPrice;

    TradeItPriceInfoParcelable(PriceInfo priceInfo) {
        this.conditionType = priceInfo.conditionType;
        this.initialStopPrice = priceInfo.initialStopPrice;
        this.conditionSymbol = priceInfo.conditionSymbol;
        this.trailPrice = priceInfo.trailPrice;
        this.conditionFollowPrice = priceInfo.conditionFollowPrice;
        this.limitPrice = priceInfo.limitPrice;
        this.triggerPrice = priceInfo.triggerPrice;
        this.conditionPrice = priceInfo.conditionPrice;
        this.bracketLimitPrice = priceInfo.bracketLimitPrice;
        this.type = priceInfo.type;
        this.stopPrice = priceInfo.stopPrice;
    }

    @Override
    public String toString() {
        return "TradeItPriceInfoParcelable{" +
                "conditionType='" + conditionType + '\'' +
                ", initialStopPrice=" + initialStopPrice +
                ", conditionSymbol='" + conditionSymbol + '\'' +
                ", trailPrice=" + trailPrice +
                ", conditionFollowPrice=" + conditionFollowPrice +
                ", limitPrice=" + limitPrice +
                ", triggerPrice=" + triggerPrice +
                ", conditionPrice=" + conditionPrice +
                ", bracketLimitPrice=" + bracketLimitPrice +
                ", type='" + type + '\'' +
                ", stopPrice=" + stopPrice +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TradeItPriceInfoParcelable that = (TradeItPriceInfoParcelable) o;

        if (conditionType != null ? !conditionType.equals(that.conditionType) : that.conditionType != null)
            return false;
        if (initialStopPrice != null ? !initialStopPrice.equals(that.initialStopPrice) : that.initialStopPrice != null)
            return false;
        if (conditionSymbol != null ? !conditionSymbol.equals(that.conditionSymbol) : that.conditionSymbol != null)
            return false;
        if (trailPrice != null ? !trailPrice.equals(that.trailPrice) : that.trailPrice != null)
            return false;
        if (conditionFollowPrice != null ? !conditionFollowPrice.equals(that.conditionFollowPrice) : that.conditionFollowPrice != null)
            return false;
        if (limitPrice != null ? !limitPrice.equals(that.limitPrice) : that.limitPrice != null)
            return false;
        if (triggerPrice != null ? !triggerPrice.equals(that.triggerPrice) : that.triggerPrice != null)
            return false;
        if (conditionPrice != null ? !conditionPrice.equals(that.conditionPrice) : that.conditionPrice != null)
            return false;
        if (bracketLimitPrice != null ? !bracketLimitPrice.equals(that.bracketLimitPrice) : that.bracketLimitPrice != null)
            return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        return stopPrice != null ? stopPrice.equals(that.stopPrice) : that.stopPrice == null;

    }

    @Override
    public int hashCode() {
        int result = conditionType != null ? conditionType.hashCode() : 0;
        result = 31 * result + (initialStopPrice != null ? initialStopPrice.hashCode() : 0);
        result = 31 * result + (conditionSymbol != null ? conditionSymbol.hashCode() : 0);
        result = 31 * result + (trailPrice != null ? trailPrice.hashCode() : 0);
        result = 31 * result + (conditionFollowPrice != null ? conditionFollowPrice.hashCode() : 0);
        result = 31 * result + (limitPrice != null ? limitPrice.hashCode() : 0);
        result = 31 * result + (triggerPrice != null ? triggerPrice.hashCode() : 0);
        result = 31 * result + (conditionPrice != null ? conditionPrice.hashCode() : 0);
        result = 31 * result + (bracketLimitPrice != null ? bracketLimitPrice.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (stopPrice != null ? stopPrice.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.conditionType);
        dest.writeValue(this.initialStopPrice);
        dest.writeString(this.conditionSymbol);
        dest.writeValue(this.trailPrice);
        dest.writeValue(this.conditionFollowPrice);
        dest.writeValue(this.limitPrice);
        dest.writeValue(this.triggerPrice);
        dest.writeValue(this.conditionPrice);
        dest.writeValue(this.bracketLimitPrice);
        dest.writeString(this.type);
        dest.writeValue(this.stopPrice);
    }

    protected TradeItPriceInfoParcelable(Parcel in) {
        this.conditionType = in.readString();
        this.initialStopPrice = (Double) in.readValue(Double.class.getClassLoader());
        this.conditionSymbol = in.readString();
        this.trailPrice = (Double) in.readValue(Double.class.getClassLoader());
        this.conditionFollowPrice = (Double) in.readValue(Double.class.getClassLoader());
        this.limitPrice = (Double) in.readValue(Double.class.getClassLoader());
        this.triggerPrice = (Double) in.readValue(Double.class.getClassLoader());
        this.conditionPrice = (Double) in.readValue(Double.class.getClassLoader());
        this.bracketLimitPrice = (Double) in.readValue(Double.class.getClassLoader());
        this.type = in.readString();
        this.stopPrice = (Double) in.readValue(Double.class.getClassLoader());
    }

    public static final Parcelable.Creator<TradeItPriceInfoParcelable> CREATOR = new Parcelable.Creator<TradeItPriceInfoParcelable>() {
        @Override
        public TradeItPriceInfoParcelable createFromParcel(Parcel source) {
            return new TradeItPriceInfoParcelable(source);
        }

        @Override
        public TradeItPriceInfoParcelable[] newArray(int size) {
            return new TradeItPriceInfoParcelable[size];
        }
    };
}
