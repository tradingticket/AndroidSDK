package it.trade.android.sdk.model.orderstatus

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import it.trade.model.reponse.OrderLeg
import it.trade.model.reponse.OrderStatusDetails
import java.util.*


class TradeItOrderStatusParcelable : Parcelable {

    @SerializedName("groupOrderType")
    var groupOrderType: String? = null
        private set

    @SerializedName("orderExpiration")
    var orderExpiration: String? = null
        private set

    @SerializedName("orderType")
    var orderType: String? = null
        private set

    @SerializedName("groupOrderId")
    var groupOrderId: String? = null
        private set

    @SerializedName("orderLegs")
    var orderLegs: List<TradeItOrderLegParcelable>? = null
        private set

    @SerializedName("orderNumber")
    var orderNumber: String? = null
        private set

    @SerializedName("orderStatus")
    var orderStatus: String? = null
        private set

    @SerializedName("groupOrders")
    var groupOrders: List<TradeItOrderStatusParcelable> = ArrayList()
        private set

    constructor(orderStatusDetails: OrderStatusDetails) {
        this.groupOrderType = orderStatusDetails.groupOrderType
        this.orderExpiration = orderStatusDetails.orderExpiration
        this.orderType = orderStatusDetails.orderType
        this.groupOrderId = orderStatusDetails.groupOrderId
        this.orderLegs = mapOrderLegToTradeItOrderLegParcelable(orderStatusDetails.orderLegs)
        this.orderNumber = orderStatusDetails.orderNumber
        this.orderStatus = orderStatusDetails.orderStatus
        this.groupOrders = mapOrderStatusDetailsToTradeItOrderStatusParcelable(orderStatusDetails.groupOrders)
    }

    override fun toString(): String {
        return "TradeItOrderStatusParcelable{" +
                "groupOrderType='" + groupOrderType + '\''.toString() +
                ", orderExpiration='" + orderExpiration + '\''.toString() +
                ", orderType='" + orderType + '\''.toString() +
                ", groupOrderId='" + groupOrderId + '\''.toString() +
                ", orderLegs=" + orderLegs +
                ", orderNumber='" + orderNumber + '\''.toString() +
                ", orderStatus='" + orderStatus + '\''.toString() +
                ", groupOrders=" + groupOrders +
                '}'.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val that = other as TradeItOrderStatusParcelable?

        if (if (groupOrderType != null) groupOrderType != that!!.groupOrderType else that!!.groupOrderType != null)
            return false
        if (if (orderExpiration != null) orderExpiration != that.orderExpiration else that.orderExpiration != null)
            return false
        if (if (orderType != null) orderType != that.orderType else that.orderType != null)
            return false
        if (if (groupOrderId != null) groupOrderId != that.groupOrderId else that.groupOrderId != null)
            return false
        if (orderLegs != that.orderLegs) return false
        if (if (orderNumber != null) orderNumber != that.orderNumber else that.orderNumber != null)
            return false
        return if (if (orderStatus != null) orderStatus != that.orderStatus else that.orderStatus != null) false else groupOrders == that.groupOrders

    }

    override fun hashCode(): Int {
        var result = if (groupOrderType != null) groupOrderType!!.hashCode() else 0
        result = 31 * result + if (orderExpiration != null) orderExpiration!!.hashCode() else 0
        result = 31 * result + if (orderType != null) orderType!!.hashCode() else 0
        result = 31 * result + if (groupOrderId != null) groupOrderId!!.hashCode() else 0
        result = 31 * result + (orderLegs?.hashCode() ?: 0)
        result = 31 * result + if (orderNumber != null) orderNumber!!.hashCode() else 0
        result = 31 * result + if (orderStatus != null) orderStatus!!.hashCode() else 0
        result = 31 * result + groupOrders.hashCode()
        return result
    }

    private fun mapOrderLegToTradeItOrderLegParcelable(orderLegs: List<OrderLeg>): List<TradeItOrderLegParcelable> {
        val orderLegParcelables = ArrayList<TradeItOrderLegParcelable>()
        for (orderLeg in orderLegs) {
            orderLegParcelables.add(TradeItOrderLegParcelable(orderLeg))
        }
        return orderLegParcelables
    }


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.groupOrderType)
        dest.writeString(this.orderExpiration)
        dest.writeString(this.orderType)
        dest.writeString(this.groupOrderId)
        dest.writeTypedList(this.orderLegs)
        dest.writeString(this.orderNumber)
        dest.writeString(this.orderStatus)
        dest.writeList(this.groupOrders)
    }

    protected constructor(`in`: Parcel) {
        this.groupOrderType = `in`.readString()
        this.orderExpiration = `in`.readString()
        this.orderType = `in`.readString()
        this.groupOrderId = `in`.readString()
        this.orderLegs = `in`.createTypedArrayList(TradeItOrderLegParcelable.CREATOR)
        this.orderNumber = `in`.readString()
        this.orderStatus = `in`.readString()
        this.groupOrders = ArrayList()
        `in`.readList(this.groupOrders, TradeItOrderStatusParcelable::class.java.getClassLoader())
    }

    companion object {

        fun mapOrderStatusDetailsToTradeItOrderStatusParcelable(orderStatusDetailsList: List<OrderStatusDetails>): List<TradeItOrderStatusParcelable> {
            val orderStatusParcelableList = ArrayList<TradeItOrderStatusParcelable>()
            for (orderStatusDetails in orderStatusDetailsList) {
                orderStatusParcelableList.add(TradeItOrderStatusParcelable(orderStatusDetails))
            }
            return orderStatusParcelableList
        }
        @JvmField
        val CREATOR: Parcelable.Creator<TradeItOrderStatusParcelable> = object : Parcelable.Creator<TradeItOrderStatusParcelable> {
            override fun createFromParcel(source: Parcel): TradeItOrderStatusParcelable {
                return TradeItOrderStatusParcelable(source)
            }

            override fun newArray(size: Int): Array<TradeItOrderStatusParcelable?> {
                return arrayOfNulls(size)
            }
        }
    }
}
