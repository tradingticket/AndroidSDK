package it.trade.android.sdk.model.orderstatus;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import it.trade.model.reponse.OrderLeg;
import it.trade.model.reponse.OrderStatusDetails;


public class TradeItOrderStatusParcelable implements Parcelable {

    private String groupOrderType;
    private String orderExpiration;
    private String orderType;
    private String groupOrderId;
    private List<TradeItOrderLegParcelable> orderLegs = new ArrayList<>();
    private String orderNumber;
    private String orderStatus;
    private List<TradeItOrderStatusParcelable> groupOrders = new ArrayList<>();

    public TradeItOrderStatusParcelable(OrderStatusDetails orderStatusDetails) {
        this.groupOrderType = orderStatusDetails.groupOrderType;
        this.orderExpiration = orderStatusDetails.orderExpiration;
        this.orderType = orderStatusDetails.orderType;
        this.groupOrderId = orderStatusDetails.groupOrderId;
        this.orderLegs = mapOrderLegToTradeItOrderLegParcelable(orderStatusDetails.orderLegs);
        this.orderNumber = orderStatusDetails.orderNumber;
        this.orderStatus = orderStatusDetails.orderStatus;
        this.groupOrders = mapOrderStatusDetailsToTradeItOrderStatusParcelable(orderStatusDetails.groupOrders);
    }

    public static List<TradeItOrderStatusParcelable> mapOrderStatusDetailsToTradeItOrderStatusParcelable(List<OrderStatusDetails> orderStatusDetailsList) {
        List<TradeItOrderStatusParcelable> orderStatusParcelableList = new ArrayList<>();
        for (OrderStatusDetails orderStatusDetails: orderStatusDetailsList) {
            orderStatusParcelableList.add(new TradeItOrderStatusParcelable(orderStatusDetails));
        }
        return orderStatusParcelableList;
    }

    public String getGroupOrderType() {
        return groupOrderType;
    }

    public String getOrderExpiration() {
        return orderExpiration;
    }

    public String getOrderType() {
        return orderType;
    }

    public String getGroupOrderId() {
        return groupOrderId;
    }

    public List<TradeItOrderLegParcelable> getOrderLegs() {
        return orderLegs;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public List<TradeItOrderStatusParcelable> getGroupOrders() {
        return groupOrders;
    }

    @Override
    public String toString() {
        return "TradeItOrderStatusParcelable{" +
                "groupOrderType='" + groupOrderType + '\'' +
                ", orderExpiration='" + orderExpiration + '\'' +
                ", orderType='" + orderType + '\'' +
                ", groupOrderId='" + groupOrderId + '\'' +
                ", orderLegs=" + orderLegs +
                ", orderNumber='" + orderNumber + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                ", groupOrders=" + groupOrders +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TradeItOrderStatusParcelable that = (TradeItOrderStatusParcelable) o;

        if (groupOrderType != null ? !groupOrderType.equals(that.groupOrderType) : that.groupOrderType != null)
            return false;
        if (orderExpiration != null ? !orderExpiration.equals(that.orderExpiration) : that.orderExpiration != null)
            return false;
        if (orderType != null ? !orderType.equals(that.orderType) : that.orderType != null)
            return false;
        if (groupOrderId != null ? !groupOrderId.equals(that.groupOrderId) : that.groupOrderId != null)
            return false;
        if (!orderLegs.equals(that.orderLegs)) return false;
        if (orderNumber != null ? !orderNumber.equals(that.orderNumber) : that.orderNumber != null)
            return false;
        if (orderStatus != null ? !orderStatus.equals(that.orderStatus) : that.orderStatus != null)
            return false;
        return groupOrders.equals(that.groupOrders);

    }

    @Override
    public int hashCode() {
        int result = groupOrderType != null ? groupOrderType.hashCode() : 0;
        result = 31 * result + (orderExpiration != null ? orderExpiration.hashCode() : 0);
        result = 31 * result + (orderType != null ? orderType.hashCode() : 0);
        result = 31 * result + (groupOrderId != null ? groupOrderId.hashCode() : 0);
        result = 31 * result + orderLegs.hashCode();
        result = 31 * result + (orderNumber != null ? orderNumber.hashCode() : 0);
        result = 31 * result + (orderStatus != null ? orderStatus.hashCode() : 0);
        result = 31 * result + groupOrders.hashCode();
        return result;
    }

    private List<TradeItOrderLegParcelable> mapOrderLegToTradeItOrderLegParcelable(List<OrderLeg> orderLegs) {
        List<TradeItOrderLegParcelable> orderLegParcelables = new ArrayList<>();
        for (OrderLeg orderLeg: orderLegs) {
            orderLegParcelables.add(new TradeItOrderLegParcelable(orderLeg));
        }
        return orderLegParcelables;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.groupOrderType);
        dest.writeString(this.orderExpiration);
        dest.writeString(this.orderType);
        dest.writeString(this.groupOrderId);
        dest.writeTypedList(this.orderLegs);
        dest.writeString(this.orderNumber);
        dest.writeString(this.orderStatus);
        dest.writeList(this.groupOrders);
    }

    protected TradeItOrderStatusParcelable(Parcel in) {
        this.groupOrderType = in.readString();
        this.orderExpiration = in.readString();
        this.orderType = in.readString();
        this.groupOrderId = in.readString();
        this.orderLegs = in.createTypedArrayList(TradeItOrderLegParcelable.CREATOR);
        this.orderNumber = in.readString();
        this.orderStatus = in.readString();
        this.groupOrders = new ArrayList<TradeItOrderStatusParcelable>();
        in.readList(this.groupOrders, TradeItOrderStatusParcelable.class.getClassLoader());
    }

    public static final Parcelable.Creator<TradeItOrderStatusParcelable> CREATOR = new Parcelable.Creator<TradeItOrderStatusParcelable>() {
        @Override
        public TradeItOrderStatusParcelable createFromParcel(Parcel source) {
            return new TradeItOrderStatusParcelable(source);
        }

        @Override
        public TradeItOrderStatusParcelable[] newArray(int size) {
            return new TradeItOrderStatusParcelable[size];
        }
    };
}
