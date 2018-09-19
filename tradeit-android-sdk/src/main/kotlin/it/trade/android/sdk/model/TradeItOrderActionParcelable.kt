package it.trade.android.sdk.model

import android.os.Parcel
import android.os.Parcelable

import it.trade.android.sdk.enums.TradeItOrderAction
import it.trade.android.sdk.enums.TradeItOrderQuantityType

class TradeItOrderActionParcelable : Parcelable {
    var action: TradeItOrderAction
    var displayLabel: String
    var supportedOrderQuantityTypes: List<TradeItOrderQuantityType>

    internal constructor(
        action: TradeItOrderAction,
        displayLabel: String,
        supportedOrderQuantityTypes: List<TradeItOrderQuantityType>
    ) {
        this.action = action
        this.displayLabel = displayLabel
        this.supportedOrderQuantityTypes = supportedOrderQuantityTypes
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TradeItOrderActionParcelable

        if (action != other.action) return false
        if (displayLabel != other.displayLabel) return false
        if (supportedOrderQuantityTypes != other.supportedOrderQuantityTypes) return false

        return true
    }

    override fun hashCode(): Int {
        var result = action.hashCode()
        result = 31 * result + displayLabel.hashCode()
        result = 31 * result + supportedOrderQuantityTypes.hashCode()
        return result
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(this.action.ordinal)
        dest.writeString(this.displayLabel)
        dest.writeList(this.supportedOrderQuantityTypes)
    }

    protected constructor(`in`: Parcel) {
        val tmpAction = `in`.readInt()
        this.action = TradeItOrderAction.values()[tmpAction]
        this.displayLabel = `in`.readString()
        this.supportedOrderQuantityTypes = ArrayList()
        `in`.readList(this.supportedOrderQuantityTypes, TradeItOrderQuantityType::class.java.getClassLoader())
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<TradeItOrderActionParcelable> = object : Parcelable.Creator<TradeItOrderActionParcelable> {
            override fun createFromParcel(source: Parcel): TradeItOrderActionParcelable {
                return TradeItOrderActionParcelable(source)
            }

            override fun newArray(size: Int): Array<TradeItOrderActionParcelable?> {
                return arrayOfNulls(size)
            }
        }
    }
}
