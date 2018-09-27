package it.trade.android.sdk.model

import android.os.Parcel
import android.os.Parcelable

import it.trade.model.reponse.OrderInfo
import it.trade.model.reponse.Price

class TradeItOrderInfoParcelable : Parcelable {
    var action: String? = ""
        internal set
    var quantity: Double? = null
        internal set
    var symbol: String = ""
        internal set
    var price: TradeItPriceParcelable? = null
        internal set
    var expiration: String? = ""
        internal set
    var orderQuantityType: String? = ""

    internal constructor(orderInfo: OrderInfo) {
        this.action = orderInfo.action
        this.quantity = orderInfo.quantity
        this.symbol = orderInfo.symbol
        this.price = TradeItPriceParcelable(orderInfo.price)
        this.expiration = orderInfo.expiration
        this.orderQuantityType = orderInfo.orderQuantityType
    }

    internal constructor() {}

    override fun toString(): String {
        return "TradeItOrderInfoParcelable{" +
                "action='" + action + '\''.toString() +
                ", quantity=" + quantity +
                ", symbol='" + symbol + '\''.toString() +
                ", price=" + price +
                ", expiration='" + expiration + '\''.toString() +
                ", orderQuantityType='" + orderQuantityType + '\''.toString() +
                '}'.toString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.action)
        dest.writeValue(this.quantity)
        dest.writeString(this.symbol)
        dest.writeParcelable(this.price, flags)
        dest.writeString(this.expiration)
        dest.writeString(this.orderQuantityType)
    }

    protected constructor(`in`: Parcel) {
        this.action = `in`.readString()
        this.quantity = `in`.readValue(Double::class.java.getClassLoader()) as? Double
        this.symbol = `in`.readString()
        this.price = `in`.readParcelable(Price::class.java.getClassLoader())
        this.expiration = `in`.readString()
        this.orderQuantityType = `in`.readString()
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<TradeItOrderInfoParcelable> = object : Parcelable.Creator<TradeItOrderInfoParcelable> {
            override fun createFromParcel(source: Parcel): TradeItOrderInfoParcelable {
                return TradeItOrderInfoParcelable(source)
            }

            override fun newArray(size: Int): Array<TradeItOrderInfoParcelable?> {
                return arrayOfNulls(size)
            }
        }
    }
}
