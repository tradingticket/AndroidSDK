package it.trade.android.sdk.model

import android.os.Parcel
import android.os.Parcelable

import it.trade.model.reponse.Price

class TradeItPriceParcelable : Parcelable {

    var type: String
        internal set
    var limitPrice: Double? = null
        internal set
    var stopPrice: Double? = null
        internal set
    var last: Double? = null
        internal set
    var bid: Double? = null
        internal set
    var ask: Double? = null
        internal set
    var timestamp: String
        internal set

    internal constructor(price: Price) {
        this.type = price.type
        this.limitPrice = price.limitPrice
        this.stopPrice = price.stopPrice
        this.last = price.last
        this.bid = price.bid
        this.ask = price.ask
        this.timestamp = price.timestamp
    }

    internal constructor() {}

    override fun toString(): String {
        return "TradeItPriceParcelable{" +
                "type='" + type + '\''.toString() +
                ", limitPrice=" + limitPrice +
                ", stopPrice=" + stopPrice +
                ", last=" + last +
                ", bid=" + bid +
                ", ask=" + ask +
                ", timestamp='" + timestamp + '\''.toString() +
                '}'.toString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.type)
        dest.writeValue(this.limitPrice)
        dest.writeValue(this.stopPrice)
        dest.writeValue(this.last)
        dest.writeValue(this.bid)
        dest.writeValue(this.ask)
        dest.writeString(this.timestamp)
    }

    protected constructor(`in`: Parcel) {
        this.type = `in`.readString()
        this.limitPrice = `in`.readValue(Double::class.java!!.getClassLoader()) as Double
        this.stopPrice = `in`.readValue(Double::class.java!!.getClassLoader()) as Double
        this.last = `in`.readValue(Double::class.java!!.getClassLoader()) as Double
        this.bid = `in`.readValue(Double::class.java!!.getClassLoader()) as Double
        this.ask = `in`.readValue(Double::class.java!!.getClassLoader()) as Double
        this.timestamp = `in`.readString()
    }

    companion object {

        val CREATOR: Parcelable.Creator<TradeItPriceParcelable> = object : Parcelable.Creator<TradeItPriceParcelable> {
            override fun createFromParcel(source: Parcel): TradeItPriceParcelable {
                return TradeItPriceParcelable(source)
            }

            override fun newArray(size: Int): Array<TradeItPriceParcelable> {
                return arrayOfNulls(size)
            }
        }
    }
}
