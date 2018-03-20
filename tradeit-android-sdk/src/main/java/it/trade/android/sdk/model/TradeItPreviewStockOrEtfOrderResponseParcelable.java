package it.trade.android.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import it.trade.model.reponse.OrderDetails;
import it.trade.model.reponse.TradeItPreviewStockOrEtfOrderResponse;
import it.trade.model.reponse.Warning;

public class TradeItPreviewStockOrEtfOrderResponseParcelable implements Parcelable {

    protected String orderId;
    protected List<String> ackWarningsList;
    protected List<String> warningsList;
    protected TradeItOrderDetailsParcelable orderDetails;

    TradeItPreviewStockOrEtfOrderResponseParcelable(TradeItPreviewStockOrEtfOrderResponse response) {
        this.orderId = response.orderId;
        this.ackWarningsList = response.ackWarningsList;
        this.warningsList = response.warningsList;
        this.orderDetails = new TradeItOrderDetailsParcelable(response.orderDetails);
    }

    @Override
    public String toString() {
        return "TradeItPreviewStockOrEtfOrderResponseParcelable{" +
                "orderId='" + orderId + '\'' +
                ", orderDetails=" + orderDetails +
                '}';
    }

    public String getOrderId() {
        return orderId;
    }

    /**
     *
     * @deprecated Use orderDetails.warnings
     */
    @Deprecated
    public List<String> getAckWarningsList() {
        return ackWarningsList;
    }

    /**
     *
     * @deprecated Use orderDetails.warnings
     */
    @Deprecated
    public List<String> getWarningsList() {
        return warningsList;
    }

    public TradeItOrderDetailsParcelable getOrderDetails() {
        return orderDetails;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.ackWarningsList);
        dest.writeParcelable(this.orderDetails, flags);
        dest.writeString(this.orderId);
        dest.writeStringList(this.warningsList);
    }

    public TradeItPreviewStockOrEtfOrderResponseParcelable() {
    }

    protected TradeItPreviewStockOrEtfOrderResponseParcelable(Parcel in) {
        this.ackWarningsList = in.createStringArrayList();
        this.orderDetails = in.readParcelable(OrderDetails.class.getClassLoader());
        this.orderId = in.readString();
        this.warningsList = in.createStringArrayList();
    }

    public static final Parcelable.Creator<TradeItPreviewStockOrEtfOrderResponseParcelable> CREATOR = new Parcelable.Creator<TradeItPreviewStockOrEtfOrderResponseParcelable>() {
        @Override
        public TradeItPreviewStockOrEtfOrderResponseParcelable createFromParcel(Parcel source) {
            return new TradeItPreviewStockOrEtfOrderResponseParcelable(source);
        }

        @Override
        public TradeItPreviewStockOrEtfOrderResponseParcelable[] newArray(int size) {
            return new TradeItPreviewStockOrEtfOrderResponseParcelable[size];
        }
    };
}
