package it.trade.android.sdk.model

import android.os.Parcel
import android.os.Parcelable

import it.trade.model.reponse.TradeItPlaceStockOrEtfOrderResponse

class TradeItPlaceStockOrEtfOrderResponseParcelable : Parcelable {

    var broker: String = ""
        internal set
    var confirmationMessage: String = ""
        internal set
    var orderNumber: String = ""
        internal set
    var timestamp: String = ""
        internal set
    var orderInfo: TradeItOrderInfoParcelable? = null
        internal set

    internal constructor(response: TradeItPlaceStockOrEtfOrderResponse) {
        this.broker = response.broker
        this.confirmationMessage = response.confirmationMessage
        this.orderNumber = response.orderNumber
        this.timestamp = response.timestamp
        this.orderInfo = TradeItOrderInfoParcelable(response.orderInfo)
    }

    internal constructor() {}

    override fun toString(): String {
        return "TradeItPlaceStockOrEtfOrderResponseParcelable{" +
                "broker='" + broker + '\''.toString() +
                ", confirmationMessage='" + confirmationMessage + '\''.toString() +
                ", orderNumber='" + orderNumber + '\''.toString() +
                ", timestamp='" + timestamp + '\''.toString() +
                ", orderInfo=" + orderInfo +
                '}'.toString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.broker)
        dest.writeString(this.confirmationMessage)
        dest.writeString(this.orderNumber)
        dest.writeString(this.timestamp)
        dest.writeParcelable(this.orderInfo, flags)
    }

    protected constructor(`in`: Parcel) {
        this.broker = `in`.readString()
        this.confirmationMessage = `in`.readString()
        this.orderNumber = `in`.readString()
        this.timestamp = `in`.readString()
        this.orderInfo = `in`.readParcelable(TradeItOrderInfoParcelable::class.java!!.getClassLoader())
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<TradeItPlaceStockOrEtfOrderResponseParcelable> = object : Parcelable.Creator<TradeItPlaceStockOrEtfOrderResponseParcelable> {
            override fun createFromParcel(source: Parcel): TradeItPlaceStockOrEtfOrderResponseParcelable {
                return TradeItPlaceStockOrEtfOrderResponseParcelable(source)
            }

            override fun newArray(size: Int): Array<TradeItPlaceStockOrEtfOrderResponseParcelable?> {
                return arrayOfNulls(size)
            }
        }
    }
}
