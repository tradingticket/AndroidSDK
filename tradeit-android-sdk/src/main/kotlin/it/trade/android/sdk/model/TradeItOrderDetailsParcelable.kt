package it.trade.android.sdk.model

import android.os.Parcel
import android.os.Parcelable
import it.trade.model.reponse.OrderDetails
import it.trade.model.reponse.Warning
import java.util.*

class TradeItOrderDetailsParcelable : Parcelable {
    var orderSymbol: String = ""
    var orderAction: String = ""
        protected set
    var orderQuantity: Double = 0.0
        protected set
    var orderExpiration: String = ""
        protected set
    var orderPrice: String = ""
        protected set
    var orderValueLabel: String = ""
        protected set
    var orderCommissionLabel: String = ""
    var orderMessage: String = ""
        protected set
    var lastPrice: String? = ""
        protected set
    var bidPrice: String? = ""
        protected set
    var askPrice: String? = ""
        protected set
    var timestamp: String? = ""
        protected set
    var buyingPower: Double? = null
        protected set
    var availableCash: Double? = null
        protected set
    var estimatedOrderCommission: Double? = null
        protected set
    var longHoldings: Double? = null
        protected set
    var shortHoldings: Double? = null
        protected set
    var estimatedOrderValue: Double? = null
        protected set
    var estimatedTotalValue: Double? = null
        protected set
    var warnings: List<TradeItWarningParcelable> = ArrayList()

    internal constructor() {}

    internal constructor(orderDetails: OrderDetails) {
        this.askPrice = orderDetails.askPrice
        this.availableCash = orderDetails.availableCash
        this.bidPrice = orderDetails.bidPrice
        this.buyingPower = orderDetails.buyingPower
        this.estimatedOrderCommission = orderDetails.estimatedOrderCommission
        this.estimatedOrderValue = orderDetails.estimatedOrderValue
        this.estimatedTotalValue = orderDetails.estimatedTotalValue
        this.lastPrice = orderDetails.lastPrice
        this.orderAction = orderDetails.orderAction
        this.orderSymbol = orderDetails.orderSymbol
        this.orderExpiration = orderDetails.orderExpiration
        this.orderPrice = orderDetails.orderPrice
        this.orderQuantity = orderDetails.orderQuantity
        this.orderMessage = orderDetails.orderMessage
        this.orderValueLabel = orderDetails.orderValueLabel
        this.orderCommissionLabel = orderDetails.orderCommissionLabel
        this.timestamp = orderDetails.timestamp
        this.longHoldings = orderDetails.longHoldings
        this.shortHoldings = orderDetails.shortHoldings
        this.warnings = mapWarnings(orderDetails.warnings)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.orderSymbol)
        dest.writeString(this.orderAction)
        dest.writeValue(this.orderQuantity)
        dest.writeString(this.orderExpiration)
        dest.writeString(this.orderPrice)
        dest.writeString(this.orderValueLabel)
        dest.writeString(this.orderCommissionLabel)
        dest.writeString(this.orderMessage)
        dest.writeString(this.lastPrice)
        dest.writeString(this.bidPrice)
        dest.writeString(this.askPrice)
        dest.writeString(this.timestamp)
        dest.writeValue(this.buyingPower)
        dest.writeValue(this.availableCash)
        dest.writeValue(this.estimatedOrderCommission)
        dest.writeValue(this.longHoldings)
        dest.writeValue(this.shortHoldings)
        dest.writeValue(this.estimatedOrderValue)
        dest.writeValue(this.estimatedTotalValue)
        dest.writeTypedList(this.warnings)
    }

    protected constructor(`in`: Parcel) {
        this.orderSymbol = `in`.readString()
        this.orderAction = `in`.readString()
        this.orderQuantity = `in`.readValue(Double::class.java.getClassLoader()) as Double
        this.orderExpiration = `in`.readString()
        this.orderPrice = `in`.readString()
        this.orderValueLabel = `in`.readString()
        this.orderCommissionLabel = `in`.readString()
        this.orderMessage = `in`.readString()
        this.lastPrice = `in`.readString()
        this.bidPrice = `in`.readString()
        this.askPrice = `in`.readString()
        this.timestamp = `in`.readString()
        this.buyingPower = `in`.readValue(Double::class.java.getClassLoader()) as? Double
        this.availableCash = `in`.readValue(Double::class.java.getClassLoader()) as? Double
        this.estimatedOrderCommission = `in`.readValue(Double::class.java.getClassLoader()) as? Double
        this.longHoldings = `in`.readValue(Double::class.java.getClassLoader()) as? Double
        this.shortHoldings = `in`.readValue(Double::class.java.getClassLoader()) as? Double
        this.estimatedOrderValue = `in`.readValue(Double::class.java.getClassLoader()) as? Double
        this.estimatedTotalValue = `in`.readValue(Double::class.java.getClassLoader()) as? Double
        this.warnings = `in`.createTypedArrayList(TradeItWarningParcelable.CREATOR)
    }

    companion object {

        private fun mapWarnings(warnings: List<Warning>?): List<TradeItWarningParcelable> {
            val mappedValues = ArrayList<TradeItWarningParcelable>()
            if (warnings != null) {
                for (warning in warnings) {
                    mappedValues.add(TradeItWarningParcelable(warning))
                }
            }
            return mappedValues
        }
        @JvmField
        val CREATOR: Parcelable.Creator<TradeItOrderDetailsParcelable> = object : Parcelable.Creator<TradeItOrderDetailsParcelable> {
            override fun createFromParcel(source: Parcel): TradeItOrderDetailsParcelable {
                return TradeItOrderDetailsParcelable(source)
            }

            override fun newArray(size: Int): Array<TradeItOrderDetailsParcelable?> {
                return arrayOfNulls(size)
            }
        }
    }
}
