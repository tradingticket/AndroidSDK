package it.trade.android.sdk.model

import android.os.Parcel
import android.os.Parcelable

import it.trade.android.sdk.enums.TradeItOrderAction

class TradeItOrderActionParcelable : Parcelable {
    var action: TradeItOrderAction? = null
    var displayLabel: String? = null

    internal constructor(action: TradeItOrderAction, displayLabel: String) {
        this.action = action
        this.displayLabel = displayLabel
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val that = o as TradeItOrderActionParcelable?

        if (action != that!!.action) return false
        return if (displayLabel != null) displayLabel == that.displayLabel else that.displayLabel == null
    }

    override fun hashCode(): Int {
        var result = if (action != null) action!!.hashCode() else 0
        result = 31 * result + if (displayLabel != null) displayLabel!!.hashCode() else 0
        return result
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(if (this.action == null) -1 else this.action!!.ordinal)
        dest.writeString(this.displayLabel)
    }

    protected constructor(`in`: Parcel) {
        val tmpAction = `in`.readInt()
        this.action = if (tmpAction == -1) null else TradeItOrderAction.values()[tmpAction]
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
