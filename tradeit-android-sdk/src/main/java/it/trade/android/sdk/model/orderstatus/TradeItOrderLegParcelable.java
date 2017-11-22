package it.trade.android.sdk.model.orderstatus;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import it.trade.model.reponse.Fill;
import it.trade.model.reponse.OrderLeg;

public class TradeItOrderLegParcelable implements Parcelable {

    @SerializedName("priceInfo")
    private TradeItPriceInfoParcelable priceInfo;

    @SerializedName("fills")
    private List<TradeItFillParcelable> fills = new ArrayList<>();

    @SerializedName("symbol")
    private String symbol;

    @SerializedName("orderedQuantity")
    private Integer orderedQuantity;

    @SerializedName("filledQuantity")
    private Integer filledQuantity;

    @SerializedName("action")
    private String action;

    TradeItOrderLegParcelable(OrderLeg orderLeg) {
        this.priceInfo = new TradeItPriceInfoParcelable(orderLeg.priceInfo);
        this.fills = mapFillToTradeItFillParcelable(orderLeg.fills);
        this.symbol = orderLeg.symbol;
        this.orderedQuantity = orderLeg.orderedQuantity;
        this.filledQuantity = orderLeg.filledQuantity;
        this.action = orderLeg.action;
    }

    public TradeItPriceInfoParcelable getPriceInfo() {
        return priceInfo;
    }

    public List<TradeItFillParcelable> getFills() {
        return fills;
    }

    public String getSymbol() {
        return symbol;
    }

    public Integer getOrderedQuantity() {
        return orderedQuantity;
    }

    public Integer getFilledQuantity() {
        return filledQuantity;
    }

    public String getAction() {
        return action;
    }

    @Override
    public String toString() {
        return "TradeItOrderLegParcelable{" +
                "priceInfo=" + priceInfo +
                ", fills=" + fills +
                ", symbol='" + symbol + '\'' +
                ", orderedQuantity=" + orderedQuantity +
                ", filledQuantity=" + filledQuantity +
                ", action='" + action + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TradeItOrderLegParcelable that = (TradeItOrderLegParcelable) o;

        if (priceInfo != null ? !priceInfo.equals(that.priceInfo) : that.priceInfo != null)
            return false;
        if (!fills.equals(that.fills)) return false;
        if (symbol != null ? !symbol.equals(that.symbol) : that.symbol != null) return false;
        if (orderedQuantity != null ? !orderedQuantity.equals(that.orderedQuantity) : that.orderedQuantity != null)
            return false;
        if (filledQuantity != null ? !filledQuantity.equals(that.filledQuantity) : that.filledQuantity != null)
            return false;
        return action != null ? action.equals(that.action) : that.action == null;

    }

    @Override
    public int hashCode() {
        int result = priceInfo != null ? priceInfo.hashCode() : 0;
        result = 31 * result + fills.hashCode();
        result = 31 * result + (symbol != null ? symbol.hashCode() : 0);
        result = 31 * result + (orderedQuantity != null ? orderedQuantity.hashCode() : 0);
        result = 31 * result + (filledQuantity != null ? filledQuantity.hashCode() : 0);
        result = 31 * result + (action != null ? action.hashCode() : 0);
        return result;
    }

    private List<TradeItFillParcelable> mapFillToTradeItFillParcelable(List<Fill> fills) {
        List<TradeItFillParcelable> fillParcelableList = new ArrayList<>();
        for (Fill fill: fills) {
            fillParcelableList.add(new TradeItFillParcelable(fill));
        }
        return fillParcelableList;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.priceInfo, flags);
        dest.writeTypedList(this.fills);
        dest.writeString(this.symbol);
        dest.writeValue(this.orderedQuantity);
        dest.writeValue(this.filledQuantity);
        dest.writeString(this.action);
    }

    protected TradeItOrderLegParcelable(Parcel in) {
        this.priceInfo = in.readParcelable(TradeItPriceInfoParcelable.class.getClassLoader());
        this.fills = in.createTypedArrayList(TradeItFillParcelable.CREATOR);
        this.symbol = in.readString();
        this.orderedQuantity = (Integer) in.readValue(Integer.class.getClassLoader());
        this.filledQuantity = (Integer) in.readValue(Integer.class.getClassLoader());
        this.action = in.readString();
    }

    public static final Parcelable.Creator<TradeItOrderLegParcelable> CREATOR = new Parcelable.Creator<TradeItOrderLegParcelable>() {
        @Override
        public TradeItOrderLegParcelable createFromParcel(Parcel source) {
            return new TradeItOrderLegParcelable(source);
        }

        @Override
        public TradeItOrderLegParcelable[] newArray(int size) {
            return new TradeItOrderLegParcelable[size];
        }
    };
}
