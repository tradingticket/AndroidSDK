package it.trade.android.sdk.model

import android.os.Parcel
import android.os.Parcelable

import it.trade.android.sdk.enums.TradeItOrderPriceType
import it.trade.android.sdk.enums.TradeItOrderQuantityType

class TradeItOrderPriceTypeParcelable : Parcelable {
    var priceType: TradeItOrderPriceType
    var displayLabel: String
    var supportedOrderQuantityTypes: List<TradeItOrderQuantityType> = arrayListOf()

    internal constructor(
        priceType: TradeItOrderPriceType,
        displayLabel: String,
        supportedOrderQuantityTypes: List<TradeItOrderQuantityType>
    ) {
        this.priceType = priceType
        this.displayLabel = displayLabel
        this.supportedOrderQuantityTypes = supportedOrderQuantityTypes
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TradeItOrderPriceTypeParcelable

        if (priceType != other.priceType) return false
        if (displayLabel != other.displayLabel) return false
        if (supportedOrderQuantityTypes != other.supportedOrderQuantityTypes) return false

        return true
    }

    override fun hashCode(): Int {
        var result = priceType.hashCode()
        result = 31 * result + displayLabel.hashCode()
        result = 31 * result + supportedOrderQuantityTypes.hashCode()
        return result
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(this.priceType.ordinal)
        dest.writeString(this.displayLabel)
        dest.writeList(this.supportedOrderQuantityTypes)
    }

    protected constructor(`in`: Parcel) {
        val tmpPriceType = `in`.readInt()
        this.priceType = TradeItOrderPriceType.values()[tmpPriceType]
        this.displayLabel = `in`.readString()
        this.supportedOrderQuantityTypes = ArrayList()
        `in`.readList(this.supportedOrderQuantityTypes, TradeItOrderQuantityType::class.java.getClassLoader())
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
