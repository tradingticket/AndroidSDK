package it.trade.android.sdk.model


import android.os.Parcel
import android.os.Parcelable

import com.google.gson.annotations.SerializedName

import it.trade.model.reponse.TradeItAccountOverview

class TradeItBalanceParcelable : Parcelable {
    @SerializedName("availableCash")
    var availableCash: Double? = null

    @SerializedName("buyingPower")
    var buyingPower: Double? = null

    @SerializedName("dayAbsoluteReturn")
    var dayAbsoluteReturn: Double? = null

    @SerializedName("dayPercentReturn")
    var dayPercentReturn: Double? = null

    @SerializedName("totalAbsoluteReturn")
    var totalAbsoluteReturn: Double? = null

    @SerializedName("totalPercentReturn")
    var totalPercentReturn: Double? = null

    @SerializedName("totalValue")
    var totalValue: Double? = null

    internal constructor() {}

    internal constructor(accountOverview: TradeItAccountOverview) {
        this.availableCash = accountOverview.availableCash
        this.buyingPower = accountOverview.buyingPower
        this.dayAbsoluteReturn = accountOverview.dayAbsoluteReturn
        this.dayPercentReturn = accountOverview.dayPercentReturn
        this.totalAbsoluteReturn = accountOverview.totalAbsoluteReturn
        this.totalPercentReturn = accountOverview.totalPercentReturn
        this.totalValue = accountOverview.totalValue
    }

    override fun toString(): String {
        return "TradeItBalanceParcelable{" +
                "availableCash=" + availableCash +
                ", buyingPower=" + buyingPower +
                ", dayAbsoluteReturn=" + dayAbsoluteReturn +
                ", dayPercentReturn=" + dayPercentReturn +
                ", totalAbsoluteReturn=" + totalAbsoluteReturn +
                ", totalPercentReturn=" + totalPercentReturn +
                ", totalValue=" + totalValue +
                '}'.toString()
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val that = o as TradeItBalanceParcelable?

        if (if (availableCash != null) availableCash != that!!.availableCash else that!!.availableCash != null)
            return false
        if (if (buyingPower != null) buyingPower != that.buyingPower else that.buyingPower != null)
            return false
        if (if (dayAbsoluteReturn != null) dayAbsoluteReturn != that.dayAbsoluteReturn else that.dayAbsoluteReturn != null)
            return false
        if (if (dayPercentReturn != null) dayPercentReturn != that.dayPercentReturn else that.dayPercentReturn != null)
            return false
        if (if (totalAbsoluteReturn != null) totalAbsoluteReturn != that.totalAbsoluteReturn else that.totalAbsoluteReturn != null)
            return false
        if (if (totalPercentReturn != null) totalPercentReturn != that.totalPercentReturn else that.totalPercentReturn != null)
            return false
        return if (totalValue != null) totalValue == that.totalValue else that.totalValue == null

    }

    override fun hashCode(): Int {
        var result = if (availableCash != null) availableCash!!.hashCode() else 0
        result = 31 * result + if (buyingPower != null) buyingPower!!.hashCode() else 0
        result = 31 * result + if (dayAbsoluteReturn != null) dayAbsoluteReturn!!.hashCode() else 0
        result = 31 * result + if (dayPercentReturn != null) dayPercentReturn!!.hashCode() else 0
        result = 31 * result + if (totalAbsoluteReturn != null) totalAbsoluteReturn!!.hashCode() else 0
        result = 31 * result + if (totalPercentReturn != null) totalPercentReturn!!.hashCode() else 0
        result = 31 * result + if (totalValue != null) totalValue!!.hashCode() else 0
        return result
    }


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(this.availableCash)
        dest.writeValue(this.buyingPower)
        dest.writeValue(this.dayAbsoluteReturn)
        dest.writeValue(this.dayPercentReturn)
        dest.writeValue(this.totalAbsoluteReturn)
        dest.writeValue(this.totalPercentReturn)
        dest.writeValue(this.totalValue)
    }

    protected constructor(`in`: Parcel) {
        this.availableCash = `in`.readValue(Double::class.java!!.getClassLoader()) as? Double
        this.buyingPower = `in`.readValue(Double::class.java!!.getClassLoader()) as? Double
        this.dayAbsoluteReturn = `in`.readValue(Double::class.java!!.getClassLoader()) as? Double
        this.dayPercentReturn = `in`.readValue(Double::class.java!!.getClassLoader()) as? Double
        this.totalAbsoluteReturn = `in`.readValue(Double::class.java!!.getClassLoader()) as? Double
        this.totalPercentReturn = `in`.readValue(Double::class.java!!.getClassLoader()) as? Double
        this.totalValue = `in`.readValue(Double::class.java!!.getClassLoader()) as? Double
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<TradeItBalanceParcelable> = object : Parcelable.Creator<TradeItBalanceParcelable> {
            override fun createFromParcel(source: Parcel): TradeItBalanceParcelable {
                return TradeItBalanceParcelable(source)
            }

            override fun newArray(size: Int): Array<TradeItBalanceParcelable?> {
                return arrayOfNulls(size)
            }
        }
    }
}
