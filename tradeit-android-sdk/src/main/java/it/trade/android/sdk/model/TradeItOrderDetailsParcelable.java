package it.trade.android.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import it.trade.model.reponse.OrderDetails;

public class TradeItOrderDetailsParcelable extends OrderDetails implements Parcelable {

    TradeItOrderDetailsParcelable(OrderDetails orderDetails) {
        this.askPrice = orderDetails.askPrice;
        this.availableCash = orderDetails.availableCash;
        this.bidPrice = orderDetails.bidPrice;
        this.buyingPower = orderDetails.buyingPower;
        this.estimatedOrderCommission = orderDetails.estimatedOrderCommission;
        this.estimatedOrderValue = orderDetails.estimatedOrderValue;
        this.estimatedOrderCommission = orderDetails.estimatedOrderCommission;
        this.lastPrice = orderDetails.lastPrice;
        this.orderAction = orderDetails.orderAction;
        this.orderSymbol = orderDetails.orderSymbol;
        this.orderExpiration = orderDetails.orderExpiration;
        this.orderPrice = orderDetails.orderPrice;
        this.orderQuantity = orderDetails.orderQuantity;
        this.orderMessage = orderDetails.orderMessage;
        this.orderValueLabel = orderDetails.orderValueLabel;
        this.timestamp = orderDetails.timestamp;
        this.longHoldings = orderDetails.longHoldings;
        this.shortHoldings = orderDetails.shortHoldings;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.orderSymbol);
        dest.writeString(this.orderAction);
        dest.writeValue(this.orderQuantity);
        dest.writeString(this.orderExpiration);
        dest.writeString(this.orderPrice);
        dest.writeString(this.orderValueLabel);
        dest.writeString(this.orderMessage);
        dest.writeString(this.lastPrice);
        dest.writeString(this.bidPrice);
        dest.writeString(this.askPrice);
        dest.writeString(this.timestamp);
        dest.writeValue(this.buyingPower);
        dest.writeValue(this.availableCash);
        dest.writeValue(this.estimatedOrderCommission);
        dest.writeValue(this.longHoldings);
        dest.writeValue(this.shortHoldings);
        dest.writeValue(this.estimatedOrderValue);
        dest.writeValue(this.estimatedTotalValue);
    }

    public TradeItOrderDetailsParcelable() {
    }

    protected TradeItOrderDetailsParcelable(Parcel in) {
        this.orderSymbol = in.readString();
        this.orderAction = in.readString();
        this.orderQuantity = (Double) in.readValue(Double.class.getClassLoader());
        this.orderExpiration = in.readString();
        this.orderPrice = in.readString();
        this.orderValueLabel = in.readString();
        this.orderMessage = in.readString();
        this.lastPrice = in.readString();
        this.bidPrice = in.readString();
        this.askPrice = in.readString();
        this.timestamp = in.readString();
        this.buyingPower = (Double) in.readValue(Double.class.getClassLoader());
        this.availableCash = (Double) in.readValue(Double.class.getClassLoader());
        this.estimatedOrderCommission = (Double) in.readValue(Double.class.getClassLoader());
        this.longHoldings = (Double) in.readValue(Double.class.getClassLoader());
        this.shortHoldings = (Double) in.readValue(Double.class.getClassLoader());
        this.estimatedOrderValue = (Double) in.readValue(Double.class.getClassLoader());
        this.estimatedTotalValue = (Double) in.readValue(Double.class.getClassLoader());
    }

    public static final Parcelable.Creator<TradeItOrderDetailsParcelable> CREATOR = new Parcelable.Creator<TradeItOrderDetailsParcelable>() {
        @Override
        public TradeItOrderDetailsParcelable createFromParcel(Parcel source) {
            return new TradeItOrderDetailsParcelable(source);
        }

        @Override
        public TradeItOrderDetailsParcelable[] newArray(int size) {
            return new TradeItOrderDetailsParcelable[size];
        }
    };
}
