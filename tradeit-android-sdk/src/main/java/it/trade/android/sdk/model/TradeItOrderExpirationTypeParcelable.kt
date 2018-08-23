package it.trade.android.sdk.model

import android.os.Parcel
import android.os.Parcelable

import it.trade.android.sdk.enums.TradeItOrderExpirationType

class TradeItOrderExpirationTypeParcelable : Parcelable {
    var expirationType: TradeItOrderExpirationType? = null
        private set
    var displayLabel: String? = null

    internal constructor(expiration: TradeItOrderExpirationType, displayLabel: String) {
        this.expirationType = expiration
        this.displayLabel = displayLabel
    }

    fun setExpiration(expiration: TradeItOrderExpirationType) {
        this.expirationType = expiration
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val that = o as TradeItOrderExpirationTypeParcelable?

        if (expirationType != that!!.expirationType) return false
        return if (displayLabel != null) displayLabel == that.displayLabel else that.displayLabel == null
    }

    override fun hashCode(): Int {
        var result = if (expirationType != null) expirationType!!.hashCode() else 0
        result = 31 * result + if (displayLabel != null) displayLabel!!.hashCode() else 0
        return result
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(if (this.expirationType == null) -1 else this.expirationType!!.ordinal)
        dest.writeString(this.displayLabel)
    }

    protected constructor(`in`: Parcel) {
        val tmpExpiration = `in`.readInt()
        this.expirationType = if (tmpExpiration == -1) null else TradeItOrderExpirationType.values()[tmpExpiration]
        this.displayLabel = `in`.readString()
    }

    companion object {

        val CREATOR: Parcelable.Creator<TradeItOrderExpirationTypeParcelable> = object : Parcelable.Creator<TradeItOrderExpirationTypeParcelable> {
            override fun createFromParcel(source: Parcel): TradeItOrderExpirationTypeParcelable {
                return TradeItOrderExpirationTypeParcelable(source)
            }

            override fun newArray(size: Int): Array<TradeItOrderExpirationTypeParcelable?> {
                return arrayOfNulls(size)
            }
        }
    }
}
