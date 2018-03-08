package it.trade.android.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import it.trade.android.sdk.enums.TradeItOrderExpirationType;

public class TradeItOrderExpirationTypeParcelable implements Parcelable {
    private TradeItOrderExpirationType expiration;
    private String displayLabel;

    TradeItOrderExpirationTypeParcelable(TradeItOrderExpirationType expiration, String displayLabel) {
        this.expiration = expiration;
        this.displayLabel = displayLabel;
    }

    public TradeItOrderExpirationType getExpirationType() {
        return expiration;
    }

    public void setExpiration(TradeItOrderExpirationType expiration) {
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

        TradeItOrderExpirationTypeParcelable that = (TradeItOrderExpirationTypeParcelable) o;

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

    protected TradeItOrderExpirationTypeParcelable(Parcel in) {
        int tmpExpiration = in.readInt();
        this.expiration = tmpExpiration == -1 ? null : TradeItOrderExpirationType.values()[tmpExpiration];
        this.displayLabel = in.readString();
    }

    public static final Parcelable.Creator<TradeItOrderExpirationTypeParcelable> CREATOR = new Parcelable.Creator<TradeItOrderExpirationTypeParcelable>() {
        @Override
        public TradeItOrderExpirationTypeParcelable createFromParcel(Parcel source) {
            return new TradeItOrderExpirationTypeParcelable(source);
        }

        @Override
        public TradeItOrderExpirationTypeParcelable[] newArray(int size) {
            return new TradeItOrderExpirationTypeParcelable[size];
        }
    };
}
