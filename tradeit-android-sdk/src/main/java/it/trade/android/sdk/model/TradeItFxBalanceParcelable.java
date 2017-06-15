package it.trade.android.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import it.trade.model.reponse.TradeItFxAccountOverview;

public class TradeItFxBalanceParcelable implements Parcelable {

    public Double buyingPowerBaseCurrency;

    public Double marginBalanceBaseCurrency;

    public Double realizedProfitAndLossBaseCurrency;

    public Double totalValueBaseCurrency;

    public Double totalValueUSD;

    public Double unrealizedProfitAndLossBaseCurrency;

    TradeItFxBalanceParcelable() {}

    TradeItFxBalanceParcelable(TradeItFxAccountOverview fxAccountOverview) {
        this.buyingPowerBaseCurrency = fxAccountOverview.buyingPowerBaseCurrency;
        this.marginBalanceBaseCurrency = fxAccountOverview.marginBalanceBaseCurrency;
        this.realizedProfitAndLossBaseCurrency = fxAccountOverview.realizedProfitAndLossBaseCurrency;
        this.totalValueBaseCurrency = fxAccountOverview.totalValueBaseCurrency;
        this.totalValueUSD = fxAccountOverview.totalValueUSD;
        this.unrealizedProfitAndLossBaseCurrency = fxAccountOverview.unrealizedProfitAndLossBaseCurrency;
    }

    @Override
    public String toString() {
        return "TradeItFxBalanceParcelable{" +
                "buyingPowerBaseCurrency=" + buyingPowerBaseCurrency +
                ", marginBalanceBaseCurrency=" + marginBalanceBaseCurrency +
                ", realizedProfitAndLossBaseCurrency=" + realizedProfitAndLossBaseCurrency +
                ", totalValueBaseCurrency=" + totalValueBaseCurrency +
                ", totalValueUSD=" + totalValueUSD +
                ", unrealizedProfitAndLossBaseCurrency=" + unrealizedProfitAndLossBaseCurrency +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.buyingPowerBaseCurrency);
        dest.writeValue(this.marginBalanceBaseCurrency);
        dest.writeValue(this.realizedProfitAndLossBaseCurrency);
        dest.writeValue(this.totalValueBaseCurrency);
        dest.writeValue(this.totalValueUSD);
        dest.writeValue(this.unrealizedProfitAndLossBaseCurrency);
    }

    protected TradeItFxBalanceParcelable(Parcel in) {
        this.buyingPowerBaseCurrency = (Double) in.readValue(Double.class.getClassLoader());
        this.marginBalanceBaseCurrency = (Double) in.readValue(Double.class.getClassLoader());
        this.realizedProfitAndLossBaseCurrency = (Double) in.readValue(Double.class.getClassLoader());
        this.totalValueBaseCurrency = (Double) in.readValue(Double.class.getClassLoader());
        this.totalValueUSD = (Double) in.readValue(Double.class.getClassLoader());
        this.unrealizedProfitAndLossBaseCurrency = (Double) in.readValue(Double.class.getClassLoader());
    }

    public static final Parcelable.Creator<TradeItFxBalanceParcelable> CREATOR = new Parcelable.Creator<TradeItFxBalanceParcelable>() {
        @Override
        public TradeItFxBalanceParcelable createFromParcel(Parcel source) {
            return new TradeItFxBalanceParcelable(source);
        }

        @Override
        public TradeItFxBalanceParcelable[] newArray(int size) {
            return new TradeItFxBalanceParcelable[size];
        }
    };
}
