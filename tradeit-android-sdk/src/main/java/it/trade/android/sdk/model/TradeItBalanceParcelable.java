package it.trade.android.sdk.model;


import android.os.Parcel;
import android.os.Parcelable;

import it.trade.model.reponse.TradeItAccountOverview;

public class TradeItBalanceParcelable implements Parcelable {

    public Double availableCash;

    public Double buyingPower;

    public Double dayAbsoluteReturn;

    public Double dayPercentReturn;

    public Double totalAbsoluteReturn;

    public Double totalPercentReturn;

    public Double totalValue;

    TradeItBalanceParcelable() {}

    TradeItBalanceParcelable(TradeItAccountOverview accountOverview) {
        this.availableCash = accountOverview.availableCash;
        this.buyingPower = accountOverview.buyingPower;
        this.dayAbsoluteReturn = accountOverview.dayAbsoluteReturn;
        this.dayPercentReturn = accountOverview.dayPercentReturn;
        this.totalAbsoluteReturn = accountOverview.totalAbsoluteReturn;
        this.totalPercentReturn = accountOverview.totalPercentReturn;
        this.totalValue = accountOverview.totalValue;
    }

    @Override
    public String toString() {
        return "TradeItBalanceParcelable{" +
                "availableCash=" + availableCash +
                ", buyingPower=" + buyingPower +
                ", dayAbsoluteReturn=" + dayAbsoluteReturn +
                ", dayPercentReturn=" + dayPercentReturn +
                ", totalAbsoluteReturn=" + totalAbsoluteReturn +
                ", totalPercentReturn=" + totalPercentReturn +
                ", totalValue=" + totalValue +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TradeItBalanceParcelable that = (TradeItBalanceParcelable) o;

        if (availableCash != null ? !availableCash.equals(that.availableCash) : that.availableCash != null)
            return false;
        if (buyingPower != null ? !buyingPower.equals(that.buyingPower) : that.buyingPower != null)
            return false;
        if (dayAbsoluteReturn != null ? !dayAbsoluteReturn.equals(that.dayAbsoluteReturn) : that.dayAbsoluteReturn != null)
            return false;
        if (dayPercentReturn != null ? !dayPercentReturn.equals(that.dayPercentReturn) : that.dayPercentReturn != null)
            return false;
        if (totalAbsoluteReturn != null ? !totalAbsoluteReturn.equals(that.totalAbsoluteReturn) : that.totalAbsoluteReturn != null)
            return false;
        if (totalPercentReturn != null ? !totalPercentReturn.equals(that.totalPercentReturn) : that.totalPercentReturn != null)
            return false;
        return totalValue != null ? totalValue.equals(that.totalValue) : that.totalValue == null;

    }

    @Override
    public int hashCode() {
        int result = availableCash != null ? availableCash.hashCode() : 0;
        result = 31 * result + (buyingPower != null ? buyingPower.hashCode() : 0);
        result = 31 * result + (dayAbsoluteReturn != null ? dayAbsoluteReturn.hashCode() : 0);
        result = 31 * result + (dayPercentReturn != null ? dayPercentReturn.hashCode() : 0);
        result = 31 * result + (totalAbsoluteReturn != null ? totalAbsoluteReturn.hashCode() : 0);
        result = 31 * result + (totalPercentReturn != null ? totalPercentReturn.hashCode() : 0);
        result = 31 * result + (totalValue != null ? totalValue.hashCode() : 0);
        return result;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.availableCash);
        dest.writeValue(this.buyingPower);
        dest.writeValue(this.dayAbsoluteReturn);
        dest.writeValue(this.dayPercentReturn);
        dest.writeValue(this.totalAbsoluteReturn);
        dest.writeValue(this.totalPercentReturn);
        dest.writeValue(this.totalValue);
    }

    protected TradeItBalanceParcelable(Parcel in) {
        this.availableCash = (Double) in.readValue(Double.class.getClassLoader());
        this.buyingPower = (Double) in.readValue(Double.class.getClassLoader());
        this.dayAbsoluteReturn = (Double) in.readValue(Double.class.getClassLoader());
        this.dayPercentReturn = (Double) in.readValue(Double.class.getClassLoader());
        this.totalAbsoluteReturn = (Double) in.readValue(Double.class.getClassLoader());
        this.totalPercentReturn = (Double) in.readValue(Double.class.getClassLoader());
        this.totalValue = (Double) in.readValue(Double.class.getClassLoader());
    }

    public static final Parcelable.Creator<TradeItBalanceParcelable> CREATOR = new Parcelable.Creator<TradeItBalanceParcelable>() {
        @Override
        public TradeItBalanceParcelable createFromParcel(Parcel source) {
            return new TradeItBalanceParcelable(source);
        }

        @Override
        public TradeItBalanceParcelable[] newArray(int size) {
            return new TradeItBalanceParcelable[size];
        }
    };
}
