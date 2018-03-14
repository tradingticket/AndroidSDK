package it.trade.android.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import it.trade.model.reponse.WarningLink;

public class TradeItWarningLinkParcelable implements Parcelable {
    protected String label;
    protected String url;

    TradeItWarningLinkParcelable(WarningLink link) {
        this.label = link.label;
        this.url = link.url;
    }

    public String getLabel() {
        return label;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TradeItWarningLinkParcelable that = (TradeItWarningLinkParcelable) o;

        if (label != null ? !label.equals(that.label) : that.label != null) return false;
        return url != null ? url.equals(that.url) : that.url == null;
    }

    @Override
    public int hashCode() {
        int result = label != null ? label.hashCode() : 0;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.label);
        dest.writeString(this.url);
    }

    protected TradeItWarningLinkParcelable(Parcel in) {
        this.label = in.readString();
        this.url = in.readString();
    }

    public static final Parcelable.Creator<TradeItWarningLinkParcelable> CREATOR = new Parcelable.Creator<TradeItWarningLinkParcelable>() {
        @Override
        public TradeItWarningLinkParcelable createFromParcel(Parcel source) {
            return new TradeItWarningLinkParcelable(source);
        }

        @Override
        public TradeItWarningLinkParcelable[] newArray(int size) {
            return new TradeItWarningLinkParcelable[size];
        }
    };
}
