package it.trade.android.sdk.model.orderstatus

import android.os.Parcel
import android.os.Parcelable

import com.google.gson.annotations.SerializedName

import it.trade.model.reponse.PriceInfo

class TradeItPriceInfoParcelable : Parcelable {

    @SerializedName("trailPrice")
    private var trailPrice: Double? = null

    @SerializedName("limitPrice")
    private var limitPrice: Double? = null

    @SerializedName("type")
    private var type: String? = null

    @SerializedName("stopPrice")
    private var stopPrice: Double? = null

    internal constructor(priceInfo: PriceInfo) {
        this.trailPrice = priceInfo.trailPrice
        this.limitPrice = priceInfo.limitPrice
        this.type = priceInfo.type
        this.stopPrice = priceInfo.stopPrice
    }

    override fun toString(): String {
        return "TradeItPriceInfoParcelable{" +
                ", trailPrice=" + trailPrice +
                ", limitPrice=" + limitPrice +
                ", type='" + type + '\''.toString() +
                ", stopPrice=" + stopPrice +
                '}'.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val that = other as TradeItPriceInfoParcelable?

        if (if (trailPrice != null) trailPrice != that?.trailPrice else that?.trailPrice != null)
            return false
        if (if (limitPrice != null) limitPrice != that?.limitPrice else that?.limitPrice != null)
            return false
        if (if (type != null) type != that?.type else that?.type != null) return false
        return if (stopPrice != null) stopPrice == that?.stopPrice else that?.stopPrice == null

    }

    override fun hashCode(): Int {
        var result = if (trailPrice != null) trailPrice!!.hashCode() else 0
        result = 31 * result + if (limitPrice != null) limitPrice!!.hashCode() else 0
        result = 31 * result + if (type != null) type!!.hashCode() else 0
        result = 31 * result + if (stopPrice != null) stopPrice!!.hashCode() else 0
        return result
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(this.trailPrice)
        dest.writeValue(this.limitPrice)
        dest.writeString(this.type)
        dest.writeValue(this.stopPrice)
    }

    protected constructor(`in`: Parcel) {
        this.trailPrice = `in`.readValue(Double::class.java.getClassLoader()) as? Double
        this.limitPrice = `in`.readValue(Double::class.java.getClassLoader()) as? Double
        this.type = `in`.readString()
        this.stopPrice = `in`.readValue(Double::class.java.getClassLoader()) as? Double
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<TradeItPriceInfoParcelable> = object : Parcelable.Creator<TradeItPriceInfoParcelable> {
            override fun createFromParcel(source: Parcel): TradeItPriceInfoParcelable {
                return TradeItPriceInfoParcelable(source)
            }

            override fun newArray(size: Int): Array<TradeItPriceInfoParcelable?> {
                return arrayOfNulls(size)
            }
        }
    }
}
