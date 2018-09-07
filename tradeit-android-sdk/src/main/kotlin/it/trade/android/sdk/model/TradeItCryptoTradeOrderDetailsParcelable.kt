package it.trade.android.sdk.model

import android.os.Parcel
import android.os.Parcelable
import it.trade.model.reponse.CryptoTradeOrderDetails

class TradeItCryptoTradeOrderDetailsParcelable : Parcelable {

    var orderLimitPrice: Double? = null
    var orderStopPrice: Double? = null
    var orderExpiration: String = ""
    var orderAction: String = ""
    var orderPair: String = ""
    var orderPriceType: String = ""
    var orderQuantity: Double = 0.0
    var orderQuantityType: String = ""

    constructor(parcel: Parcel) {
        orderLimitPrice = parcel.readValue(Double::class.java.classLoader) as? Double
        orderStopPrice = parcel.readValue(Double::class.java.classLoader) as? Double
        orderExpiration = parcel.readString()
        orderAction = parcel.readString()
        orderPair = parcel.readString()
        orderPriceType = parcel.readString()
        orderQuantity = parcel.readDouble()
        orderQuantityType = parcel.readString()
    }

    internal constructor() {}

    internal constructor(cryptoTradeOrderDetails: CryptoTradeOrderDetails) {
        this.orderLimitPrice = cryptoTradeOrderDetails.orderLimitPrice
        this.orderStopPrice = cryptoTradeOrderDetails.orderStopPrice
        this.orderExpiration = cryptoTradeOrderDetails.orderExpiration
        this.orderAction = cryptoTradeOrderDetails.orderAction
        this.orderPair = cryptoTradeOrderDetails.orderPair
        this.orderPriceType = cryptoTradeOrderDetails.orderPriceType
        this.orderQuantity = cryptoTradeOrderDetails.orderQuantity
        this.orderQuantityType = cryptoTradeOrderDetails.orderQuantityType
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(orderLimitPrice)
        parcel.writeValue(orderStopPrice)
        parcel.writeString(orderExpiration)
        parcel.writeString(orderAction)
        parcel.writeString(orderPair)
        parcel.writeString(orderPriceType)
        parcel.writeDouble(orderQuantity)
        parcel.writeString(orderQuantityType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<TradeItCryptoTradeOrderDetailsParcelable> = object : Parcelable.Creator<TradeItCryptoTradeOrderDetailsParcelable> {
            override fun createFromParcel(source: Parcel): TradeItCryptoTradeOrderDetailsParcelable = TradeItCryptoTradeOrderDetailsParcelable(source)
            override fun newArray(size: Int): Array<TradeItCryptoTradeOrderDetailsParcelable?> = arrayOfNulls(size)
        }
    }

}