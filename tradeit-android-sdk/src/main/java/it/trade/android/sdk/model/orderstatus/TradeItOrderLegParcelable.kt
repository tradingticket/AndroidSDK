package it.trade.android.sdk.model.orderstatus

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import it.trade.model.reponse.Fill
import it.trade.model.reponse.OrderLeg
import java.util.*

class TradeItOrderLegParcelable : Parcelable {

    @SerializedName("priceInfo")
    var priceInfo: TradeItPriceInfoParcelable? = null
        private set

    @SerializedName("fills")
    var fills: List<TradeItFillParcelable> = ArrayList()
        private set

    @SerializedName("symbol")
    var symbol: String? = null
        private set

    @SerializedName("orderedQuantity")
    var orderedQuantity: Int? = null
        private set

    @SerializedName("filledQuantity")
    var filledQuantity: Int? = null
        private set

    @SerializedName("action")
    var action: String? = null
        private set

    internal constructor(orderLeg: OrderLeg) {
        this.priceInfo = TradeItPriceInfoParcelable(orderLeg.priceInfo)
        this.fills = mapFillToTradeItFillParcelable(orderLeg.fills)
        this.symbol = orderLeg.symbol
        this.orderedQuantity = orderLeg.orderedQuantity
        this.filledQuantity = orderLeg.filledQuantity
        this.action = orderLeg.action
    }

    override fun toString(): String {
        return "TradeItOrderLegParcelable{" +
                "priceInfo=" + priceInfo +
                ", fills=" + fills +
                ", symbol='" + symbol + '\''.toString() +
                ", orderedQuantity=" + orderedQuantity +
                ", filledQuantity=" + filledQuantity +
                ", action='" + action + '\''.toString() +
                '}'.toString()
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val that = o as TradeItOrderLegParcelable?

        if (if (priceInfo != null) priceInfo != that!!.priceInfo else that!!.priceInfo != null)
            return false
        if (fills != that.fills) return false
        if (if (symbol != null) symbol != that.symbol else that.symbol != null) return false
        if (if (orderedQuantity != null) orderedQuantity != that.orderedQuantity else that.orderedQuantity != null)
            return false
        if (if (filledQuantity != null) filledQuantity != that.filledQuantity else that.filledQuantity != null)
            return false
        return if (action != null) action == that.action else that.action == null

    }

    override fun hashCode(): Int {
        var result = if (priceInfo != null) priceInfo!!.hashCode() else 0
        result = 31 * result + fills.hashCode()
        result = 31 * result + if (symbol != null) symbol!!.hashCode() else 0
        result = 31 * result + if (orderedQuantity != null) orderedQuantity!!.hashCode() else 0
        result = 31 * result + if (filledQuantity != null) filledQuantity!!.hashCode() else 0
        result = 31 * result + if (action != null) action!!.hashCode() else 0
        return result
    }

    private fun mapFillToTradeItFillParcelable(fills: List<Fill>): List<TradeItFillParcelable> {
        val fillParcelableList = ArrayList<TradeItFillParcelable>()
        for (fill in fills) {
            fillParcelableList.add(TradeItFillParcelable(fill))
        }
        return fillParcelableList
    }


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeParcelable(this.priceInfo, flags)
        dest.writeTypedList(this.fills)
        dest.writeString(this.symbol)
        dest.writeValue(this.orderedQuantity)
        dest.writeValue(this.filledQuantity)
        dest.writeString(this.action)
    }

    protected constructor(`in`: Parcel) {
        this.priceInfo = `in`.readParcelable(TradeItPriceInfoParcelable::class.java!!.getClassLoader())
        this.fills = `in`.createTypedArrayList(TradeItFillParcelable.CREATOR)
        this.symbol = `in`.readString()
        this.orderedQuantity = `in`.readValue(Int::class.java!!.getClassLoader()) as Int
        this.filledQuantity = `in`.readValue(Int::class.java!!.getClassLoader()) as Int
        this.action = `in`.readString()
    }

    companion object {

        val CREATOR: Parcelable.Creator<TradeItOrderLegParcelable> = object : Parcelable.Creator<TradeItOrderLegParcelable> {
            override fun createFromParcel(source: Parcel): TradeItOrderLegParcelable {
                return TradeItOrderLegParcelable(source)
            }

            override fun newArray(size: Int): Array<TradeItOrderLegParcelable?> {
                return arrayOfNulls(size)
            }
        }
    }
}
