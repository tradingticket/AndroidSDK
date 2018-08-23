package it.trade.android.sdk.model

import android.os.Parcel
import android.os.Parcelable

import it.trade.android.sdk.enums.TradeItOrderPriceType

class TradeItOrderPriceTypeParcelable : Parcelable {
    var priceType: TradeItOrderPriceType? = null
    var displayLabel: String? = null

    internal constructor(priceType: TradeItOrderPriceType, displayLabel: String) {
        this.priceType = priceType
        this.displayLabel = displayLabel
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val that = o as TradeItOrderPriceTypeParcelable?

        if (priceType != that!!.priceType) return false
        return if (displayLabel != null) displayLabel == that.displayLabel else that.displayLabel == null
    }

    override fun hashCode(): Int {
        var result = if (priceType != null) priceType!!.hashCode() else 0
        result = 31 * result + if (displayLabel != null) displayLabel!!.hashCode() else 0
        return result
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(if (this.priceType == null) -1 else this.priceType!!.ordinal)
        dest.writeString(this.displayLabel)
    }

    protected constructor(`in`: Parcel) {
        val tmpPriceType = `in`.readInt()
        this.priceType = if (tmpPriceType == -1) null else TradeItOrderPriceType.values()[tmpPriceType]
        this.displayLabel = `in`.readString()
    }

    companion object {

        val CREATOR: Parcelable.Creator<TradeItOrderPriceTypeParcelable> = object : Parcelable.Creator<TradeItOrderPriceTypeParcelable> {
            override fun createFromParcel(source: Parcel): TradeItOrderPriceTypeParcelable {
                return TradeItOrderPriceTypeParcelable(source)
            }

            override fun newArray(size: Int): Array<TradeItOrderPriceTypeParcelable?> {
                return arrayOfNulls(size)
            }
        }
    }
}
