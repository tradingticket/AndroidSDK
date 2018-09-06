package it.trade.android.sdk.model

import android.os.Parcel
import android.os.Parcelable
import it.trade.model.reponse.TradeItPlaceCryptoOrderResponse

class TradeItPlaceCryptoOrderResponseParcelable: Parcelable {
    var orderNumber: String = ""
    var confirmationMessage: String = ""
    var broker: String = ""
    var accountBaseCurrency: String = ""
    var timestamp: String = ""
    var orderDetails: TradeItCryptoTradeOrderDetailsParcelable? = null

    internal constructor (response: TradeItPlaceCryptoOrderResponse) {
        this.orderNumber = response.orderNumber
        this.confirmationMessage = response.confirmationMessage
        this.broker = response.broker
        this.accountBaseCurrency = response.accountBaseCurrency
        this.timestamp = response.timestamp
        this.orderDetails = TradeItCryptoTradeOrderDetailsParcelable(response.orderDetails)
    }

    constructor(parcel: Parcel) {
        orderNumber = parcel.readString()
        confirmationMessage = parcel.readString()
        broker = parcel.readString()
        accountBaseCurrency = parcel.readString()
        timestamp = parcel.readString()
        orderDetails = parcel.readParcelable(TradeItCryptoTradeOrderDetailsParcelable::class.java.classLoader)
    }

    override fun toString(): String {
        return "TradeItPlaceCryptoOrderResponseParcelable(" +
            "orderNumber=$orderNumber, " +
            "confirmationMessage=$confirmationMessage, " +
            "broker=$broker, " +
            "accountBaseCurrency=$accountBaseCurrency, " +
            "timestamp=$timestamp, " +
            "orderDetails=$orderDetails" +
            ")"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(orderNumber)
        parcel.writeString(confirmationMessage)
        parcel.writeString(broker)
        parcel.writeString(accountBaseCurrency)
        parcel.writeString(timestamp)
        parcel.writeParcelable(orderDetails, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<TradeItPlaceCryptoOrderResponseParcelable> = object : Parcelable.Creator<TradeItPlaceCryptoOrderResponseParcelable> {
            override fun createFromParcel(source: Parcel): TradeItPlaceCryptoOrderResponseParcelable = TradeItPlaceCryptoOrderResponseParcelable(source)
            override fun newArray(size: Int): Array<TradeItPlaceCryptoOrderResponseParcelable?> = arrayOfNulls(size)
        }
    }


}