package it.trade.android.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import it.trade.android.sdk.enums.TradeItOrderAction;

public class TradeItOrderActionParcelable implements Parcelable {
    private TradeItOrderAction action;
    private String displayLabel;

    TradeItOrderActionParcelable(TradeItOrderAction action, String displayLabel) {
        this.action = action;
        this.displayLabel = displayLabel;
    }

    public TradeItOrderAction getAction() {
        return action;
    }

    public void setAction(TradeItOrderAction action) {
        this.action = action;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    public void setDisplayLabel(String displayLabel) {
        this.displayLabel = displayLabel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TradeItOrderActionParcelable that = (TradeItOrderActionParcelable) o;

        if (action != that.action) return false;
        return displayLabel != null ? displayLabel.equals(that.displayLabel) : that.displayLabel == null;
    }

    @Override
    public int hashCode() {
        int result = action != null ? action.hashCode() : 0;
        result = 31 * result + (displayLabel != null ? displayLabel.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.action == null ? -1 : this.action.ordinal());
        dest.writeString(this.displayLabel);
    }

    protected TradeItOrderActionParcelable(Parcel in) {
        int tmpAction = in.readInt();
        this.action = tmpAction == -1 ? null : TradeItOrderAction.values()[tmpAction];
        this.displayLabel = in.readString();
    }

    public static final Parcelable.Creator<TradeItOrderActionParcelable> CREATOR = new Parcelable.Creator<TradeItOrderActionParcelable>() {
        @Override
        public TradeItOrderActionParcelable createFromParcel(Parcel source) {
            return new TradeItOrderActionParcelable(source);
        }

        @Override
        public TradeItOrderActionParcelable[] newArray(int size) {
            return new TradeItOrderActionParcelable[size];
        }
    };
}
