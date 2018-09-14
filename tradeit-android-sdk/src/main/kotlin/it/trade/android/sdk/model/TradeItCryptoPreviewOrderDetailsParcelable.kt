package it.trade.android.sdk.model

import android.os.Parcel
import android.os.Parcelable
import it.trade.model.reponse.CryptoPreviewOrderDetails
import it.trade.model.reponse.Warning
import java.util.*

class TradeItCryptoPreviewOrderDetailsParcelable : Parcelable {
    var estimatedTotalValue: Double? = null

    var orderQuantityType: String = ""

    var orderCommissionLabel: String = ""

    var orderExpiration: String = ""

    var orderAction: String = ""

    var orderPriceType: String = ""

    var orderQuantity: Double = 0.0

    var orderLimitPrice: Double? = null

    var orderStopPrice: Double? = null

    var estimatedOrderCommission: Double? = null

    var estimatedOrderValue: Double? = null

    var orderPair: String = ""

    var warnings: List<TradeItWarningParcelable> = mutableListOf()

    override fun toString(): String {
        return "TradeItCryptoPreviewOrderDetailsParcelable(estimatedTotalValue=$estimatedTotalValue, " +
            "orderQuantityType='$orderQuantityType', " +
            "orderCommissionLabel='$orderCommissionLabel', " +
            "orderExpiration='$orderExpiration', " +
            "orderAction='$orderAction', " +
            "orderPriceType='$orderPriceType', " +
            "orderQuantity=$orderQuantity, " +
            "orderLimitPrice=$orderLimitPrice, " +
            "orderStopPrice=$orderStopPrice, " +
            "estimatedOrderCommission=$estimatedOrderCommission, " +
            "estimatedOrderValue=$estimatedOrderValue, " +
            "orderPair='$orderPair', " +
            "warnings=$warnings" +
            ")"
    }

    internal constructor() {}

    internal constructor(cryptoPreviewOrderDetails: CryptoPreviewOrderDetails) {
        estimatedTotalValue = cryptoPreviewOrderDetails.estimatedTotalValue
        orderQuantityType = cryptoPreviewOrderDetails.orderQuantityType
        orderCommissionLabel = cryptoPreviewOrderDetails.orderCommissionLabel
        orderExpiration =  cryptoPreviewOrderDetails.orderExpiration
        orderAction = cryptoPreviewOrderDetails.orderAction
        orderPriceType = cryptoPreviewOrderDetails.orderPriceType
        orderQuantity = cryptoPreviewOrderDetails.orderQuantity
        orderLimitPrice = cryptoPreviewOrderDetails.orderLimitPrice
        orderStopPrice = cryptoPreviewOrderDetails.orderStopPrice
        estimatedOrderCommission = cryptoPreviewOrderDetails.estimatedOrderCommission
        estimatedOrderValue = cryptoPreviewOrderDetails.estimatedOrderValue
        orderPair = cryptoPreviewOrderDetails.orderPair
        warnings = mapWarnings(cryptoPreviewOrderDetails.warnings)
    }

    constructor(parcel: Parcel) {
        estimatedTotalValue = parcel.readValue(Double::class.java.classLoader) as? Double
        orderQuantityType = parcel.readString()
        orderCommissionLabel = parcel.readString()
        orderExpiration = parcel.readString()
        orderAction = parcel.readString()
        orderPriceType = parcel.readString()
        orderQuantity = parcel.readDouble()
        orderLimitPrice = parcel.readValue(Double::class.java.classLoader) as? Double
        orderStopPrice = parcel.readValue(Double::class.java.classLoader) as? Double
        estimatedOrderCommission = parcel.readValue(Double::class.java.classLoader) as? Double
        estimatedOrderValue = parcel.readValue(Double::class.java.classLoader) as? Double
        orderPair = parcel.readString()
        warnings = parcel.createTypedArrayList(TradeItWarningParcelable.CREATOR)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(estimatedTotalValue)
        parcel.writeString(orderQuantityType)
        parcel.writeString(orderCommissionLabel)
        parcel.writeString(orderExpiration)
        parcel.writeString(orderAction)
        parcel.writeString(orderPriceType)
        parcel.writeDouble(orderQuantity)
        parcel.writeValue(orderLimitPrice)
        parcel.writeValue(orderStopPrice)
        parcel.writeValue(estimatedOrderCommission)
        parcel.writeValue(estimatedOrderValue)
        parcel.writeString(orderPair)
        parcel.writeTypedList(warnings)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        private fun mapWarnings(warnings: List<Warning>?): List<TradeItWarningParcelable> {
            val mappedValues = ArrayList<TradeItWarningParcelable>()
            warnings?.let { warnings ->
                warnings.forEach { warning ->
                    mappedValues.add(TradeItWarningParcelable(warning))
                }
            }
            return mappedValues
        }

        @JvmField
        val CREATOR: Parcelable.Creator<TradeItCryptoPreviewOrderDetailsParcelable> = object : Parcelable.Creator<TradeItCryptoPreviewOrderDetailsParcelable> {
            override fun createFromParcel(source: Parcel): TradeItCryptoPreviewOrderDetailsParcelable = TradeItCryptoPreviewOrderDetailsParcelable(source)
            override fun newArray(size: Int): Array<TradeItCryptoPreviewOrderDetailsParcelable?> = arrayOfNulls(size)
        }
    }
}