package it.trade.android.sdk.model.orderstatus

import android.os.Parcel
import android.os.Parcelable

import com.google.gson.annotations.SerializedName

import it.trade.model.reponse.PriceInfo

class TradeItPriceInfoParcelable : Parcelable {

    @SerializedName("conditionType")
    private var conditionType: String? = null

    @SerializedName("initialStopPrice")
    private var initialStopPrice: Double? = null

    @SerializedName("conditionSymbol")
    private var conditionSymbol: String? = null

    @SerializedName("trailPrice")
    private var trailPrice: Double? = null

    @SerializedName("conditionFollowPrice")
    private var conditionFollowPrice: Double? = null

    @SerializedName("limitPrice")
    private var limitPrice: Double? = null

    @SerializedName("triggerPrice")
    private var triggerPrice: Double? = null

    @SerializedName("conditionPrice")
    private var conditionPrice: Double? = null

    @SerializedName("bracketLimitPrice")
    private var bracketLimitPrice: Double? = null

    @SerializedName("type")
    private var type: String? = null

    @SerializedName("stopPrice")
    private var stopPrice: Double? = null

    internal constructor(priceInfo: PriceInfo) {
        this.conditionType = priceInfo.conditionType
        this.initialStopPrice = priceInfo.initialStopPrice
        this.conditionSymbol = priceInfo.conditionSymbol
        this.trailPrice = priceInfo.trailPrice
        this.conditionFollowPrice = priceInfo.conditionFollowPrice
        this.limitPrice = priceInfo.limitPrice
        this.triggerPrice = priceInfo.triggerPrice
        this.conditionPrice = priceInfo.conditionPrice
        this.bracketLimitPrice = priceInfo.bracketLimitPrice
        this.type = priceInfo.type
        this.stopPrice = priceInfo.stopPrice
    }

    override fun toString(): String {
        return "TradeItPriceInfoParcelable{" +
                "conditionType='" + conditionType + '\''.toString() +
                ", initialStopPrice=" + initialStopPrice +
                ", conditionSymbol='" + conditionSymbol + '\''.toString() +
                ", trailPrice=" + trailPrice +
                ", conditionFollowPrice=" + conditionFollowPrice +
                ", limitPrice=" + limitPrice +
                ", triggerPrice=" + triggerPrice +
                ", conditionPrice=" + conditionPrice +
                ", bracketLimitPrice=" + bracketLimitPrice +
                ", type='" + type + '\''.toString() +
                ", stopPrice=" + stopPrice +
                '}'.toString()
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val that = o as TradeItPriceInfoParcelable?

        if (if (conditionType != null) conditionType != that!!.conditionType else that!!.conditionType != null)
            return false
        if (if (initialStopPrice != null) initialStopPrice != that.initialStopPrice else that.initialStopPrice != null)
            return false
        if (if (conditionSymbol != null) conditionSymbol != that.conditionSymbol else that.conditionSymbol != null)
            return false
        if (if (trailPrice != null) trailPrice != that.trailPrice else that.trailPrice != null)
            return false
        if (if (conditionFollowPrice != null) conditionFollowPrice != that.conditionFollowPrice else that.conditionFollowPrice != null)
            return false
        if (if (limitPrice != null) limitPrice != that.limitPrice else that.limitPrice != null)
            return false
        if (if (triggerPrice != null) triggerPrice != that.triggerPrice else that.triggerPrice != null)
            return false
        if (if (conditionPrice != null) conditionPrice != that.conditionPrice else that.conditionPrice != null)
            return false
        if (if (bracketLimitPrice != null) bracketLimitPrice != that.bracketLimitPrice else that.bracketLimitPrice != null)
            return false
        if (if (type != null) type != that.type else that.type != null) return false
        return if (stopPrice != null) stopPrice == that.stopPrice else that.stopPrice == null

    }

    override fun hashCode(): Int {
        var result = if (conditionType != null) conditionType!!.hashCode() else 0
        result = 31 * result + if (initialStopPrice != null) initialStopPrice!!.hashCode() else 0
        result = 31 * result + if (conditionSymbol != null) conditionSymbol!!.hashCode() else 0
        result = 31 * result + if (trailPrice != null) trailPrice!!.hashCode() else 0
        result = 31 * result + if (conditionFollowPrice != null) conditionFollowPrice!!.hashCode() else 0
        result = 31 * result + if (limitPrice != null) limitPrice!!.hashCode() else 0
        result = 31 * result + if (triggerPrice != null) triggerPrice!!.hashCode() else 0
        result = 31 * result + if (conditionPrice != null) conditionPrice!!.hashCode() else 0
        result = 31 * result + if (bracketLimitPrice != null) bracketLimitPrice!!.hashCode() else 0
        result = 31 * result + if (type != null) type!!.hashCode() else 0
        result = 31 * result + if (stopPrice != null) stopPrice!!.hashCode() else 0
        return result
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.conditionType)
        dest.writeValue(this.initialStopPrice)
        dest.writeString(this.conditionSymbol)
        dest.writeValue(this.trailPrice)
        dest.writeValue(this.conditionFollowPrice)
        dest.writeValue(this.limitPrice)
        dest.writeValue(this.triggerPrice)
        dest.writeValue(this.conditionPrice)
        dest.writeValue(this.bracketLimitPrice)
        dest.writeString(this.type)
        dest.writeValue(this.stopPrice)
    }

    protected constructor(`in`: Parcel) {
        this.conditionType = `in`.readString()
        this.initialStopPrice = `in`.readValue(Double::class.java!!.getClassLoader()) as Double
        this.conditionSymbol = `in`.readString()
        this.trailPrice = `in`.readValue(Double::class.java!!.getClassLoader()) as Double
        this.conditionFollowPrice = `in`.readValue(Double::class.java!!.getClassLoader()) as Double
        this.limitPrice = `in`.readValue(Double::class.java!!.getClassLoader()) as Double
        this.triggerPrice = `in`.readValue(Double::class.java!!.getClassLoader()) as Double
        this.conditionPrice = `in`.readValue(Double::class.java!!.getClassLoader()) as Double
        this.bracketLimitPrice = `in`.readValue(Double::class.java!!.getClassLoader()) as Double
        this.type = `in`.readString()
        this.stopPrice = `in`.readValue(Double::class.java!!.getClassLoader()) as Double
    }

    companion object {

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
