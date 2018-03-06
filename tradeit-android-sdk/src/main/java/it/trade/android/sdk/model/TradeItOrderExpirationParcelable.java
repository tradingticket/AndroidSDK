package it.trade.android.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import it.trade.android.sdk.enums.TradeItOrderExpiration;

public class TradeItOrderExpirationParcelable implements Parcelable {
    private TradeItOrderExpiration expiration;
    private String displayLabel;

    TradeItOrderExpirationParcelable(TradeItOrderExpiration expiration, String displayLabel) {
        this.expiration = expiration;
        this.displayLabel = displayLabel;
    }

    public TradeItOrderExpiration getExpiration() {
        return expiration;
    }

    public void setExpiration(TradeItOrderExpiration expiration) {
        this.expiration = expiration;
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

        TradeItOrderExpirationParcelable that = (TradeItOrderExpirationParcelable) o;

        if (expiration != that.expiration) return false;
        return displayLabel != null ? displayLabel.equals(that.displayLabel) : that.displayLabel == null;
    }

    @Override
    public int hashCode() {
        int result = expiration != null ? expiration.hashCode() : 0;
        result = 31 * result + (displayLabel != null ? displayLabel.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.expiration == null ? -1 : this.expiration.ordinal());
        dest.writeString(this.displayLabel);
    }

    protected TradeItOrderExpirationParcelable(Parcel in) {
        int tmpExpiration = in.readInt();
        this.expiration = tmpExpiration == -1 ? null : TradeItOrderExpiration.values()[tmpExpiration];
        this.displayLabel = in.readString();
    }

    public static final Parcelable.Creator<TradeItOrderExpirationParcelable> CREATOR = new Parcelable.Creator<TradeItOrderExpirationParcelable>() {
        @Override
        public TradeItOrderExpirationParcelable createFromParcel(Parcel source) {
            return new TradeItOrderExpirationParcelable(source);
        }

        @Override
        public TradeItOrderExpirationParcelable[] newArray(int size) {
            return new TradeItOrderExpirationParcelable[size];
        }
    };
}
