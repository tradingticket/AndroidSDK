package it.trade.android.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import it.trade.model.reponse.TradeItBrokerAccount;

public class DisplayLabelValueParcelable implements Parcelable {

    private String displayLabel;
    private String value;


    public DisplayLabelValueParcelable(TradeItBrokerAccount.OrderCapability.DisplayLabelValue displayLabelValue) {
        this.displayLabel  = displayLabelValue.displayLabel;
        this.value = displayLabelValue.value;
    }

    public static List<DisplayLabelValueParcelable> mapDisplayLabelValuesToDisplayLabelValueParcelables(List<TradeItBrokerAccount.OrderCapability.DisplayLabelValue> displayLabelValues) {
        List<DisplayLabelValueParcelable> displayLabelValueParcelables = new ArrayList<>();
        for (TradeItBrokerAccount.OrderCapability.DisplayLabelValue displayLabelValue: displayLabelValues) {
            displayLabelValueParcelables.add(new DisplayLabelValueParcelable(displayLabelValue));
        }
        return displayLabelValueParcelables;
    }

    @Override
    public String toString() {
        return "DisplayLabelValueParcelable{" +
                "displayLabel='" + displayLabel + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.displayLabel);
        dest.writeString(this.value);
    }

    protected DisplayLabelValueParcelable(Parcel in) {
        this.displayLabel = in.readString();
        this.value = in.readString();
    }

    public static final Parcelable.Creator<DisplayLabelValueParcelable> CREATOR = new Parcelable.Creator<DisplayLabelValueParcelable>() {
        @Override
        public DisplayLabelValueParcelable createFromParcel(Parcel source) {
            return new DisplayLabelValueParcelable(source);
        }

        @Override
        public DisplayLabelValueParcelable[] newArray(int size) {
            return new DisplayLabelValueParcelable[size];
        }
    };
}
