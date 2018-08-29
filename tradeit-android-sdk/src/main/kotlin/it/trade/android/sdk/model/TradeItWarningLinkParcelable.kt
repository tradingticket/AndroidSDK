package it.trade.android.sdk.model

import android.os.Parcel
import android.os.Parcelable

import it.trade.model.reponse.WarningLink

class TradeItWarningLinkParcelable : Parcelable {
    var label: String
        protected set
    var url: String
        protected set

    internal constructor(link: WarningLink) {
        this.label = link.label
        this.url = link.url
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TradeItWarningLinkParcelable

        if (label != other.label) return false
        if (url != other.url) return false

        return true
    }

    override fun hashCode(): Int {
        var result = label.hashCode()
        result = 31 * result + url.hashCode()
        return result
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.label)
        dest.writeString(this.url)
    }

    protected constructor(`in`: Parcel) {
        this.label = `in`.readString()
        this.url = `in`.readString()
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<TradeItWarningLinkParcelable> = object : Parcelable.Creator<TradeItWarningLinkParcelable> {
            override fun createFromParcel(source: Parcel): TradeItWarningLinkParcelable {
                return TradeItWarningLinkParcelable(source)
            }

            override fun newArray(size: Int): Array<TradeItWarningLinkParcelable?> {
                return arrayOfNulls(size)
            }
        }
    }
}
