package it.trade.android.sdk.model

import android.os.Parcel
import android.os.Parcelable
import it.trade.model.reponse.OrderDetails
import it.trade.model.reponse.TradeItPreviewStockOrEtfOrderResponse

class TradeItPreviewStockOrEtfOrderResponseParcelable : Parcelable {

    var orderId: String = ""

    @get:Deprecated("Use orderDetails.warnings")
    var ackWarningsList: List<String> = arrayListOf()

    @get:Deprecated("Use orderDetails.warnings")
    var warningsList: List<String> = arrayListOf()
    var orderDetails: TradeItOrderDetailsParcelable? = null

    internal constructor(response: TradeItPreviewStockOrEtfOrderResponse) {
        this.orderId = response.orderId
        this.ackWarningsList = response.ackWarningsList
        this.warningsList = response.warningsList
        this.orderDetails = TradeItOrderDetailsParcelable(response.orderDetails)
    }

    override fun toString(): String {
        return "TradeItPreviewStockOrEtfOrderResponseParcelable{" +
                "orderId='" + orderId + '\''.toString() +
                ", orderDetails=" + orderDetails +
                '}'.toString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeStringList(this.ackWarningsList)
        dest.writeParcelable(this.orderDetails, flags)
        dest.writeString(this.orderId)
        dest.writeStringList(this.warningsList)
    }

    constructor() {}

    protected constructor(`in`: Parcel) {
        this.ackWarningsList = `in`.createStringArrayList()
        this.orderDetails = `in`.readParcelable(OrderDetails::class.java.getClassLoader())
        this.orderId = `in`.readString()
        this.warningsList = `in`.createStringArrayList()
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<TradeItPreviewStockOrEtfOrderResponseParcelable> = object : Parcelable.Creator<TradeItPreviewStockOrEtfOrderResponseParcelable> {
            override fun createFromParcel(source: Parcel): TradeItPreviewStockOrEtfOrderResponseParcelable {
                return TradeItPreviewStockOrEtfOrderResponseParcelable(source)
            }

            override fun newArray(size: Int): Array<TradeItPreviewStockOrEtfOrderResponseParcelable?> {
                return arrayOfNulls(size)
            }
        }
    }
}
