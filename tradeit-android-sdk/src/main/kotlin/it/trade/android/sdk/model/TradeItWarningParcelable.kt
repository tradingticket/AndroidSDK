package it.trade.android.sdk.model

import android.os.Parcel
import android.os.Parcelable
import it.trade.model.reponse.Warning
import it.trade.model.reponse.WarningLink
import java.util.*

class TradeItWarningParcelable : Parcelable {
    var message: String? = null
        protected set
    var isRequiresAcknowledgement: Boolean = false
        protected set
    var links: List<TradeItWarningLinkParcelable>? = null
        protected set

    internal constructor(warning: Warning) {
        this.message = warning.message
        this.isRequiresAcknowledgement = warning.requiresAcknowledgement
        this.links = mapLinks(warning.links)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val that = other as TradeItWarningParcelable?

        if (isRequiresAcknowledgement != that!!.isRequiresAcknowledgement) return false
        if (if (message != null) message != that.message else that.message != null) return false
        return if (links != null) links == that.links else that.links == null
    }

    override fun hashCode(): Int {
        var result = if (message != null) message!!.hashCode() else 0
        result = 31 * result + if (isRequiresAcknowledgement) 1 else 0
        result = 31 * result + if (links != null) links!!.hashCode() else 0
        return result
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.message)
        dest.writeByte(if (this.isRequiresAcknowledgement) 1.toByte() else 0.toByte())
        dest.writeList(this.links)
    }

    protected constructor(`in`: Parcel) {
        this.message = `in`.readString()
        this.isRequiresAcknowledgement = `in`.readByte().toInt() != 0
        this.links = ArrayList()
        `in`.readList(this.links, TradeItWarningLinkParcelable::class.java.getClassLoader())
    }

    companion object {

        private fun mapLinks(links: List<WarningLink>?): List<TradeItWarningLinkParcelable> {
            val mappedValues = ArrayList<TradeItWarningLinkParcelable>()
            if (links != null) {
                for (link in links) {
                    mappedValues.add(TradeItWarningLinkParcelable(link))
                }
            }
            return mappedValues
        }
        @JvmField
        val CREATOR: Parcelable.Creator<TradeItWarningParcelable> = object : Parcelable.Creator<TradeItWarningParcelable> {
            override fun createFromParcel(source: Parcel): TradeItWarningParcelable {
                return TradeItWarningParcelable(source)
            }

            override fun newArray(size: Int): Array<TradeItWarningParcelable?> {
                return arrayOfNulls(size)
            }
        }
    }
}
