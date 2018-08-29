package it.trade.android.sdk.model

import android.os.Parcel
import android.os.Parcelable

import it.trade.android.sdk.enums.TradeItOrderAction

class TradeItOrderActionParcelable : Parcelable {
    var action: TradeItOrderAction
    var displayLabel: String

    internal constructor(action: TradeItOrderAction, displayLabel: String) {
        this.action = action
        this.displayLabel = displayLabel
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TradeItOrderActionParcelable

        if (action != other.action) return false
        if (displayLabel != other.displayLabel) return false

        return true
    }

    override fun hashCode(): Int {
        var result = action.hashCode()
        result = 31 * result + displayLabel.hashCode()
        return result
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(this.action.ordinal)
        dest.writeString(this.displayLabel)
    }

    protected constructor(`in`: Parcel) {
        val tmpAction = `in`.readInt()
        this.action = TradeItOrderAction.values()[tmpAction]
        this.displayLabel = `in`.readString()
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
