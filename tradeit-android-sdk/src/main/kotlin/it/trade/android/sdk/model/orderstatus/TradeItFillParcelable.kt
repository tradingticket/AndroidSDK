package it.trade.android.sdk.model.orderstatus

import android.os.Parcel
import android.os.Parcelable

import com.google.gson.annotations.SerializedName

import it.trade.model.reponse.Fill

class TradeItFillParcelable : Parcelable {

    @SerializedName("timestampFormat")
    var timestampFormat: String? = null
        private set

    @SerializedName("price")
    var price: Double? = null
        private set

    @SerializedName("timestamp")
    var timestamp: String? = null
        private set

    @SerializedName("quantity")
    var quantity: Int? = null
        private set

    internal constructor(fill: Fill) {
        this.timestampFormat = fill.timestampFormat
        this.price = fill.price
        this.timestamp = fill.timestamp
        this.quantity = fill.quantity
    }

    override fun toString(): String {
        return "TradeItFillParcelable{" +
                "timestampFormat='" + timestampFormat + '\''.toString() +
                ", price=" + price +
                ", timestamp='" + timestamp + '\''.toString() +
                ", quantity=" + quantity +
                '}'.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val that = other as TradeItFillParcelable?

        if (if (timestampFormat != null) timestampFormat != that!!.timestampFormat else that!!.timestampFormat != null)
            return false
        if (if (price != null) price != that.price else that.price != null) return false
        if (if (timestamp != null) timestamp != that.timestamp else that.timestamp != null)
            return false
        return if (quantity != null) quantity == that.quantity else that.quantity == null

    }

    override fun hashCode(): Int {
        var result = if (timestampFormat != null) timestampFormat!!.hashCode() else 0
        result = 31 * result + if (price != null) price!!.hashCode() else 0
        result = 31 * result + if (timestamp != null) timestamp!!.hashCode() else 0
        result = 31 * result + if (quantity != null) quantity!!.hashCode() else 0
        return result
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.timestampFormat)
        dest.writeValue(this.price)
        dest.writeString(this.timestamp)
        dest.writeValue(this.quantity)
    }

    protected constructor(`in`: Parcel) {
        this.timestampFormat = `in`.readString()
        this.price = `in`.readValue(Double::class.java.getClassLoader()) as? Double
        this.timestamp = `in`.readString()
        this.quantity = `in`.readValue(Int::class.java.getClassLoader()) as? Int
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<TradeItFillParcelable> = object : Parcelable.Creator<TradeItFillParcelable> {
            override fun createFromParcel(source: Parcel): TradeItFillParcelable {
                return TradeItFillParcelable(source)
            }

            override fun newArray(size: Int): Array<TradeItFillParcelable?> {
                return arrayOfNulls(size)
            }
        }
    }
}
