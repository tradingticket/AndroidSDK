package it.trade.android.sdk.model

import android.os.Parcel
import android.os.Parcelable

import it.trade.android.sdk.enums.TradeItOrderPriceType

class TradeItOrderPriceTypeParcelable : Parcelable {
    var priceType: TradeItOrderPriceType
    var displayLabel: String

    internal constructor(priceType: TradeItOrderPriceType, displayLabel: String) {
        this.priceType = priceType
        this.displayLabel = displayLabel
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TradeItOrderPriceTypeParcelable

        if (priceType != other.priceType) return false
        if (displayLabel != other.displayLabel) return false

        return true
    }

    override fun hashCode(): Int {
        var result = priceType.hashCode()
        result = 31 * result + displayLabel.hashCode()
        return result
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(this.priceType.ordinal)
        dest.writeString(this.displayLabel)
    }

    protected constructor(`in`: Parcel) {
        val tmpPriceType = `in`.readInt()
        this.priceType = TradeItOrderPriceType.values()[tmpPriceType]
        this.displayLabel = `in`.readString()
    }

    companion object {
        @JvmField
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
