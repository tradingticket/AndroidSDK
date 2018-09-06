package it.trade.android.sdk.model

import android.os.Parcel
import android.os.Parcelable
import it.trade.model.reponse.TradeItPreviewCryptoOrderResponse

class TradeItPreviewCryptoOrderResponseParcelable: Parcelable {
    var orderId: String = ""

    var orderDetails: TradeItCryptoPreviewOrderDetailsParcelable? = null

    internal constructor (response: TradeItPreviewCryptoOrderResponse) {
        this.orderId = response.orderId
        this.orderDetails = TradeItCryptoPreviewOrderDetailsParcelable(response.orderDetails)
    }

    constructor(parcel: Parcel) {
        orderId = parcel.readString()
        orderDetails = parcel.readParcelable(TradeItCryptoPreviewOrderDetailsParcelable::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(orderId)
        parcel.writeParcelable(orderDetails, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<TradeItPreviewCryptoOrderResponseParcelable> = object : Parcelable.Creator<TradeItPreviewCryptoOrderResponseParcelable> {
            override fun createFromParcel(source: Parcel): TradeItPreviewCryptoOrderResponseParcelable = TradeItPreviewCryptoOrderResponseParcelable(source)
            override fun newArray(size: Int): Array<TradeItPreviewCryptoOrderResponseParcelable?> = arrayOfNulls(size)
        }
    }
}