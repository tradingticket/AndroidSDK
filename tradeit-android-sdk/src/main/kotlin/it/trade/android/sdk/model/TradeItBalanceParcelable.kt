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

    @SerializedName("buyingPowerLabel")
    var buyingPowerLabel: String? = null

    @SerializedName("accountBaseCurrency")
    var accountBaseCurrency: String? = "USD"

    @SerializedName("marginCash")
    var marginCash: Double? = null

    internal constructor() {}

    internal constructor(accountOverview: TradeItAccountOverview) {
        this.availableCash = accountOverview.availableCash
        this.buyingPower = accountOverview.buyingPower
        this.dayAbsoluteReturn = accountOverview.dayAbsoluteReturn
        this.dayPercentReturn = accountOverview.dayPercentReturn
        this.totalAbsoluteReturn = accountOverview.totalAbsoluteReturn
        this.totalPercentReturn = accountOverview.totalPercentReturn
        this.totalValue = accountOverview.totalValue
        this.buyingPowerLabel = accountOverview.buyingPowerLabel
        this.accountBaseCurrency = accountOverview.accountBaseCurrency
        this.marginCash = accountOverview.marginCash
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
                ", buyingPowerLabel=" + buyingPowerLabel +
                ", accountBaseCurrency=" + accountBaseCurrency +
                ", marginCash=" + marginCash +
                '}'.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TradeItBalanceParcelable

        if (availableCash != other.availableCash) return false
        if (buyingPower != other.buyingPower) return false
        if (dayAbsoluteReturn != other.dayAbsoluteReturn) return false
        if (dayPercentReturn != other.dayPercentReturn) return false
        if (totalAbsoluteReturn != other.totalAbsoluteReturn) return false
        if (totalPercentReturn != other.totalPercentReturn) return false
        if (totalValue != other.totalValue) return false
        if (buyingPowerLabel != other.buyingPowerLabel) return false
        if (accountBaseCurrency != other.accountBaseCurrency) return false
        if (marginCash != other.marginCash) return false

        return true
    }

    override fun hashCode(): Int {
        var result = availableCash?.hashCode() ?: 0
        result = 31 * result + (buyingPower?.hashCode() ?: 0)
        result = 31 * result + (dayAbsoluteReturn?.hashCode() ?: 0)
        result = 31 * result + (dayPercentReturn?.hashCode() ?: 0)
        result = 31 * result + (totalAbsoluteReturn?.hashCode() ?: 0)
        result = 31 * result + (totalPercentReturn?.hashCode() ?: 0)
        result = 31 * result + (totalValue?.hashCode() ?: 0)
        result = 31 * result + (buyingPowerLabel?.hashCode() ?: 0)
        result = 31 * result + (accountBaseCurrency?.hashCode() ?: 0)
        result = 31 * result + (marginCash?.hashCode() ?: 0)
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
        dest.writeString(this.buyingPowerLabel)
        dest.writeString(this.accountBaseCurrency)
        dest.writeValue(this.marginCash)
    }

    protected constructor(`in`: Parcel) {
        this.availableCash = `in`.readValue(Double::class.java.getClassLoader()) as? Double
        this.buyingPower = `in`.readValue(Double::class.java.getClassLoader()) as? Double
        this.dayAbsoluteReturn = `in`.readValue(Double::class.java.getClassLoader()) as? Double
        this.dayPercentReturn = `in`.readValue(Double::class.java.getClassLoader()) as? Double
        this.totalAbsoluteReturn = `in`.readValue(Double::class.java.getClassLoader()) as? Double
        this.totalPercentReturn = `in`.readValue(Double::class.java.getClassLoader()) as? Double
        this.totalValue = `in`.readValue(Double::class.java.getClassLoader()) as? Double
        this.buyingPowerLabel = `in`.readString()
        this.accountBaseCurrency = `in`.readString()
        this.marginCash = `in`.readValue(Double::class.java.getClassLoader()) as? Double
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
