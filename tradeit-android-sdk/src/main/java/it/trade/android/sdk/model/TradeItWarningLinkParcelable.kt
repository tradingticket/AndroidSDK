package it.trade.android.sdk.model

import android.os.Parcel
import android.os.Parcelable

import it.trade.model.reponse.WarningLink

class TradeItWarningLinkParcelable : Parcelable {
    var label: String? = null
        protected set
    var url: String? = null
        protected set

    internal constructor(link: WarningLink) {
        this.label = link.label
        this.url = link.url
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val that = o as TradeItWarningLinkParcelable?

        if (if (label != null) label != that!!.label else that!!.label != null) return false
        return if (url != null) url == that.url else that.url == null
    }

    override fun hashCode(): Int {
        var result = if (label != null) label!!.hashCode() else 0
        result = 31 * result + if (url != null) url!!.hashCode() else 0
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
