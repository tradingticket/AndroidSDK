package it.trade.android.sdk.model

import android.os.Parcel
import android.os.Parcelable

import it.trade.android.sdk.enums.TradeItOrderExpirationType

class TradeItOrderExpirationTypeParcelable : Parcelable {
    var expirationType: TradeItOrderExpirationType
        private set
    var displayLabel: String

    internal constructor(expiration: TradeItOrderExpirationType, displayLabel: String) {
        this.expirationType = expiration
        this.displayLabel = displayLabel
    }

    fun setExpiration(expiration: TradeItOrderExpirationType) {
        this.expirationType = expiration
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TradeItOrderExpirationTypeParcelable

        if (expirationType != other.expirationType) return false
        if (displayLabel != other.displayLabel) return false

        return true
    }

    override fun hashCode(): Int {
        var result = expirationType.hashCode()
        result = 31 * result + displayLabel.hashCode()
        return result
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(this.expirationType.ordinal)
        dest.writeString(this.displayLabel)
    }

    protected constructor(`in`: Parcel) {
        val tmpExpiration = `in`.readInt()
        this.expirationType = TradeItOrderExpirationType.values()[tmpExpiration]
        this.displayLabel = `in`.readString()
    }

    companion object {
        @JvmField
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
