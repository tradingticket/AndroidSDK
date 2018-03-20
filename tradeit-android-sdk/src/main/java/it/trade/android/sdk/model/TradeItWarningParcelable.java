package it.trade.android.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import it.trade.model.reponse.Warning;
import it.trade.model.reponse.WarningLink;

public class TradeItWarningParcelable implements Parcelable {
    protected String message;
    protected boolean requiresAcknowledgement;
    protected List<TradeItWarningLinkParcelable> links;

    TradeItWarningParcelable(Warning warning) {
        this.message = warning.message;
        this.requiresAcknowledgement = warning.requiresAcknowledgement;
        this.links = mapLinks(warning.links);
    }

    public String getMessage() {
        return message;
    }

    public boolean isRequiresAcknowledgement() {
        return requiresAcknowledgement;
    }

    public List<TradeItWarningLinkParcelable> getLinks() {
        return links;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TradeItWarningParcelable that = (TradeItWarningParcelable) o;

        if (requiresAcknowledgement != that.requiresAcknowledgement) return false;
        if (message != null ? !message.equals(that.message) : that.message != null) return false;
        return links != null ? links.equals(that.links) : that.links == null;
    }

    @Override
    public int hashCode() {
        int result = message != null ? message.hashCode() : 0;
        result = 31 * result + (requiresAcknowledgement ? 1 : 0);
        result = 31 * result + (links != null ? links.hashCode() : 0);
        return result;
    }

    private static List<TradeItWarningLinkParcelable> mapLinks(List<WarningLink> links) {
        List<TradeItWarningLinkParcelable> mappedValues = new ArrayList<>();
        if (links != null) {
            for (WarningLink link: links) {
                mappedValues.add(new TradeItWarningLinkParcelable(link));
            }
        }
        return mappedValues;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.message);
        dest.writeByte(this.requiresAcknowledgement ? (byte) 1 : (byte) 0);
        dest.writeList(this.links);
    }

    protected TradeItWarningParcelable(Parcel in) {
        this.message = in.readString();
        this.requiresAcknowledgement = in.readByte() != 0;
        this.links = new ArrayList<TradeItWarningLinkParcelable>();
        in.readList(this.links, TradeItWarningLinkParcelable.class.getClassLoader());
    }

    public static final Parcelable.Creator<TradeItWarningParcelable> CREATOR = new Parcelable.Creator<TradeItWarningParcelable>() {
        @Override
        public TradeItWarningParcelable createFromParcel(Parcel source) {
            return new TradeItWarningParcelable(source);
        }

        @Override
        public TradeItWarningParcelable[] newArray(int size) {
            return new TradeItWarningParcelable[size];
        }
    };
}
