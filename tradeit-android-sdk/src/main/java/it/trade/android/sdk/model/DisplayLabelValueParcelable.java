package it.trade.android.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import it.trade.model.reponse.DisplayLabelValue;

public class DisplayLabelValueParcelable implements Parcelable {

    private String displayLabel;
    private String value;


    public DisplayLabelValueParcelable(DisplayLabelValue displayLabelValue) {
        this.displayLabel  = displayLabelValue.displayLabel;
        this.value = displayLabelValue.value;
    }

    public static List<DisplayLabelValueParcelable> mapDisplayLabelValuesToDisplayLabelValueParcelables(List<DisplayLabelValue> displayLabelValues) {
        List<DisplayLabelValueParcelable> displayLabelValueParcelables = new ArrayList<>();
        if (displayLabelValues != null) {
            for (DisplayLabelValue displayLabelValue: displayLabelValues) {
                displayLabelValueParcelables.add(new DisplayLabelValueParcelable(displayLabelValue));
            }
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

    public String getDisplayLabel() {
        return displayLabel;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DisplayLabelValueParcelable that = (DisplayLabelValueParcelable) o;

        if (displayLabel != null ? !displayLabel.equals(that.displayLabel) : that.displayLabel != null)
            return false;
        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        int result = displayLabel != null ? displayLabel.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
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
