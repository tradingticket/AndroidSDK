package it.trade.android.sdk.model.orderstatus;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import it.trade.model.reponse.Fill;

public class TradeItFillParcelable implements Parcelable {

    @SerializedName("timestampFormat")
    private String timestampFormat;

    @SerializedName("price")
    private Double price;

    @SerializedName("timestamp")
    private String timestamp;

    @SerializedName("quantity")
    private Integer quantity;

    TradeItFillParcelable(Fill fill) {
        this.timestampFormat = fill.timestampFormat;
        this.price = fill.price;
        this.timestamp = fill.timestamp;
        this.quantity = fill.quantity;
    }

    public String getTimestampFormat() {
        return timestampFormat;
    }

    public Double getPrice() {
        return price;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public Integer getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "TradeItFillParcelable{" +
                "timestampFormat='" + timestampFormat + '\'' +
                ", price=" + price +
                ", timestamp='" + timestamp + '\'' +
                ", quantity=" + quantity +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TradeItFillParcelable that = (TradeItFillParcelable) o;

        if (timestampFormat != null ? !timestampFormat.equals(that.timestampFormat) : that.timestampFormat != null)
            return false;
        if (price != null ? !price.equals(that.price) : that.price != null) return false;
        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null)
            return false;
        return quantity != null ? quantity.equals(that.quantity) : that.quantity == null;

    }

    @Override
    public int hashCode() {
        int result = timestampFormat != null ? timestampFormat.hashCode() : 0;
        result = 31 * result + (price != null ? price.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        result = 31 * result + (quantity != null ? quantity.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.timestampFormat);
        dest.writeValue(this.price);
        dest.writeString(this.timestamp);
        dest.writeValue(this.quantity);
    }

    protected TradeItFillParcelable(Parcel in) {
        this.timestampFormat = in.readString();
        this.price = (Double) in.readValue(Double.class.getClassLoader());
        this.timestamp = in.readString();
        this.quantity = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<TradeItFillParcelable> CREATOR = new Parcelable.Creator<TradeItFillParcelable>() {
        @Override
        public TradeItFillParcelable createFromParcel(Parcel source) {
            return new TradeItFillParcelable(source);
        }

        @Override
        public TradeItFillParcelable[] newArray(int size) {
            return new TradeItFillParcelable[size];
        }
    };
}
