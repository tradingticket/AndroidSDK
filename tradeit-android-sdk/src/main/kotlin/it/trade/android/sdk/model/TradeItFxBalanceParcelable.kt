package it.trade.android.sdk.model

import android.os.Parcel
import android.os.Parcelable

import com.google.gson.annotations.SerializedName

import it.trade.model.reponse.TradeItFxAccountOverview

class TradeItFxBalanceParcelable : Parcelable {

    @SerializedName("buyingPowerBaseCurrency")
    var buyingPowerBaseCurrency: Double? = null

    @SerializedName("marginBalanceBaseCurrency")
    var marginBalanceBaseCurrency: Double? = null

    @SerializedName("realizedProfitAndLossBaseCurrency")
    var realizedProfitAndLossBaseCurrency: Double? = null

    @SerializedName("totalValueBaseCurrency")
    var totalValueBaseCurrency: Double? = null

    @SerializedName("totalValueUSD")
    var totalValueUSD: Double? = null

    @SerializedName("unrealizedProfitAndLossBaseCurrency")
    var unrealizedProfitAndLossBaseCurrency: Double? = null

    internal constructor() {}

    internal constructor(fxAccountOverview: TradeItFxAccountOverview) {
        this.buyingPowerBaseCurrency = fxAccountOverview.buyingPowerBaseCurrency
        this.marginBalanceBaseCurrency = fxAccountOverview.marginBalanceBaseCurrency
        this.realizedProfitAndLossBaseCurrency = fxAccountOverview.realizedProfitAndLossBaseCurrency
        this.totalValueBaseCurrency = fxAccountOverview.totalValueBaseCurrency
        this.totalValueUSD = fxAccountOverview.totalValueUSD
        this.unrealizedProfitAndLossBaseCurrency = fxAccountOverview.unrealizedProfitAndLossBaseCurrency
    }

    override fun toString(): String {
        return "TradeItFxBalanceParcelable{" +
                "buyingPowerBaseCurrency=" + buyingPowerBaseCurrency +
                ", marginBalanceBaseCurrency=" + marginBalanceBaseCurrency +
                ", realizedProfitAndLossBaseCurrency=" + realizedProfitAndLossBaseCurrency +
                ", totalValueBaseCurrency=" + totalValueBaseCurrency +
                ", totalValueUSD=" + totalValueUSD +
                ", unrealizedProfitAndLossBaseCurrency=" + unrealizedProfitAndLossBaseCurrency +
                '}'.toString()
    }


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(this.buyingPowerBaseCurrency)
        dest.writeValue(this.marginBalanceBaseCurrency)
        dest.writeValue(this.realizedProfitAndLossBaseCurrency)
        dest.writeValue(this.totalValueBaseCurrency)
        dest.writeValue(this.totalValueUSD)
        dest.writeValue(this.unrealizedProfitAndLossBaseCurrency)
    }

    protected constructor(`in`: Parcel) {
        this.buyingPowerBaseCurrency = `in`.readValue(Double::class.java.getClassLoader()) as? Double
        this.marginBalanceBaseCurrency = `in`.readValue(Double::class.java.getClassLoader()) as? Double
        this.realizedProfitAndLossBaseCurrency = `in`.readValue(Double::class.java.getClassLoader()) as? Double
        this.totalValueBaseCurrency = `in`.readValue(Double::class.java.getClassLoader()) as? Double
        this.totalValueUSD = `in`.readValue(Double::class.java.getClassLoader()) as? Double
        this.unrealizedProfitAndLossBaseCurrency = `in`.readValue(Double::class.java.getClassLoader()) as? Double
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<TradeItFxBalanceParcelable> = object : Parcelable.Creator<TradeItFxBalanceParcelable> {
            override fun createFromParcel(source: Parcel): TradeItFxBalanceParcelable {
                return TradeItFxBalanceParcelable(source)
            }

            override fun newArray(size: Int): Array<TradeItFxBalanceParcelable?> {
                return arrayOfNulls(size)
            }
        }
    }
}
