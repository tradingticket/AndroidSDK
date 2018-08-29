package it.trade.android.sdk.model

import android.os.Parcel
import android.os.Parcelable

import it.trade.model.reponse.TradeItPosition

class TradeItPositionParcelable : TradeItPosition, Parcelable {

    constructor(position: TradeItPosition) {
        this.costbasis = position.costbasis
        this.holdingType = position.holdingType
        this.lastPrice = position.lastPrice
        this.quantity = position.quantity
        this.symbol = position.symbol
        this.symbolClass = position.symbolClass
        this.todayGainLossDollar = position.todayGainLossDollar
        this.todayGainLossPercentage = position.totalGainLossPercentage
        this.totalGainLossDollar = position.totalGainLossDollar
        this.totalGainLossPercentage = position.totalGainLossPercentage
        this.todayGainLossAbsolute = position.todayGainLossAbsolute
        this.totalGainLossAbsolute = position.totalGainLossAbsolute
        this.exchange = position.exchange
        this.currency = position.currency
    }

    constructor() : super() {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(this.costbasis)
        dest.writeString(this.holdingType)
        dest.writeValue(this.lastPrice)
        dest.writeValue(this.quantity)
        dest.writeString(this.symbol)
        dest.writeString(this.symbolClass)
        dest.writeValue(this.todayGainLossDollar)
        dest.writeValue(this.todayGainLossPercentage)
        dest.writeValue(this.totalGainLossDollar)
        dest.writeValue(this.totalGainLossPercentage)
        dest.writeValue(this.todayGainLossAbsolute)
        dest.writeValue(this.totalGainLossAbsolute)
        dest.writeString(this.exchange)
        dest.writeString(this.currency)
    }

    protected constructor(`in`: Parcel) {
        this.costbasis = `in`.readValue(Double::class.java.getClassLoader()) as? Double
        this.holdingType = `in`.readString()
        this.lastPrice = `in`.readValue(Double::class.java.getClassLoader()) as? Double
        this.quantity = `in`.readValue(Double::class.java.getClassLoader()) as? Double
        this.symbol = `in`.readString()
        this.symbolClass = `in`.readString()
        this.todayGainLossDollar = `in`.readValue(Double::class.java.getClassLoader()) as? Double
        this.todayGainLossPercentage = `in`.readValue(Double::class.java.getClassLoader()) as? Double
        this.totalGainLossDollar = `in`.readValue(Double::class.java.getClassLoader()) as? Double
        this.totalGainLossPercentage = `in`.readValue(Double::class.java.getClassLoader()) as? Double
        this.todayGainLossAbsolute = `in`.readValue(Double::class.java.getClassLoader()) as? Double
        this.totalGainLossAbsolute = `in`.readValue(Double::class.java.getClassLoader()) as? Double
        this.exchange = `in`.readString()
        this.currency = `in`.readString()
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<TradeItPositionParcelable> = object : Parcelable.Creator<TradeItPositionParcelable> {
            override fun createFromParcel(source: Parcel): TradeItPositionParcelable {
                return TradeItPositionParcelable(source)
            }

            override fun newArray(size: Int): Array<TradeItPositionParcelable?> {
                return arrayOfNulls(size)
            }
        }
    }
}
