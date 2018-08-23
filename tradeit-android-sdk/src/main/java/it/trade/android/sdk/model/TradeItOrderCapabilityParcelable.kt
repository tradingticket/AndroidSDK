package it.trade.android.sdk.model

import android.os.Parcel
import android.os.Parcelable

import java.util.ArrayList

import it.trade.android.sdk.enums.TradeItOrderAction
import it.trade.android.sdk.enums.TradeItOrderExpirationType
import it.trade.android.sdk.enums.TradeItOrderPriceType
import it.trade.model.reponse.DisplayLabelValue
import it.trade.model.reponse.Instrument
import it.trade.model.reponse.OrderCapability

class TradeItOrderCapabilityParcelable : Parcelable {

    var instrument: Instrument? = null
        private set
    var actions: List<TradeItOrderActionParcelable>? = null
        private set
    var priceTypes: List<TradeItOrderPriceTypeParcelable>? = null
        private set
    var expirationTypes: List<TradeItOrderExpirationTypeParcelable>? = null
        private set

    constructor(orderCapability: OrderCapability) {
        this.instrument = orderCapability.instrument
        this.actions = mapOrderActionToEnum(orderCapability.actions)
        this.priceTypes = mapOrderPriceTypesToEnum(orderCapability.priceTypes)
        this.expirationTypes = mapOrderExpirationTypesToEnum(orderCapability.expirationTypes)
    }

    fun getActionForEnum(actionEnum: TradeItOrderAction): TradeItOrderActionParcelable? {
        for (action in actions!!) {
            if (action.action == actionEnum) {
                return action
            }
        }
        return null
    }

    fun getPriceTypeForEnum(priceTypeEnum: TradeItOrderPriceType): TradeItOrderPriceTypeParcelable? {
        for (priceType in priceTypes!!) {
            if (priceType.priceType == priceTypeEnum) {
                return priceType
            }
        }
        return null
    }

    fun getExpirationTypeForEnum(expirationEnum: TradeItOrderExpirationType): TradeItOrderExpirationTypeParcelable? {
        for (expiration in expirationTypes!!) {
            if (expiration.expirationType == expirationEnum) {
                return expiration
            }
        }
        return null
    }

    override fun toString(): String {
        return "TradeItOrderCapabilityParcelable{" +
                "instrument=" + instrument +
                ", actions=" + actions +
                ", priceTypes=" + priceTypes +
                ", expirationTypes=" + expirationTypes +
                '}'.toString()
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val that = o as TradeItOrderCapabilityParcelable?

        if (instrument != that!!.instrument) return false
        if (if (actions != null) actions != that.actions else that.actions != null) return false
        if (if (priceTypes != null) priceTypes != that.priceTypes else that.priceTypes != null)
            return false
        return if (expirationTypes != null) expirationTypes == that.expirationTypes else that.expirationTypes == null
    }

    override fun hashCode(): Int {
        var result = if (instrument != null) instrument!!.hashCode() else 0
        result = 31 * result + if (actions != null) actions!!.hashCode() else 0
        result = 31 * result + if (priceTypes != null) priceTypes!!.hashCode() else 0
        result = 31 * result + if (expirationTypes != null) expirationTypes!!.hashCode() else 0
        return result
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(if (this.instrument == null) -1 else this.instrument!!.ordinal)
        dest.writeList(this.actions)
        dest.writeList(this.priceTypes)
        dest.writeList(this.expirationTypes)
    }

    protected constructor(`in`: Parcel) {
        val tmpInstrument = `in`.readInt()
        this.instrument = if (tmpInstrument == -1) null else Instrument.values()[tmpInstrument]
        this.actions = ArrayList()
        `in`.readList(this.actions, TradeItOrderActionParcelable::class.java!!.getClassLoader())
        this.priceTypes = ArrayList()
        `in`.readList(this.priceTypes, TradeItOrderPriceTypeParcelable::class.java!!.getClassLoader())
        this.expirationTypes = ArrayList()
        `in`.readList(this.expirationTypes, TradeItOrderExpirationTypeParcelable::class.java!!.getClassLoader())
    }

    companion object {

        fun mapOrderCapabilitiesToTradeItOrderCapabilityParcelables(orderCapabilities: List<OrderCapability>?): List<TradeItOrderCapabilityParcelable> {
            val orderCapabilityParcelables = ArrayList<TradeItOrderCapabilityParcelable>()
            if (orderCapabilities != null) {
                for (orderCapability in orderCapabilities) {
                    orderCapabilityParcelables.add(TradeItOrderCapabilityParcelable(orderCapability))
                }
            }
            return orderCapabilityParcelables
        }

        val CREATOR: Parcelable.Creator<TradeItOrderCapabilityParcelable> = object : Parcelable.Creator<TradeItOrderCapabilityParcelable> {
            override fun createFromParcel(source: Parcel): TradeItOrderCapabilityParcelable {
                return TradeItOrderCapabilityParcelable(source)
            }

            override fun newArray(size: Int): Array<TradeItOrderCapabilityParcelable> {
                return arrayOfNulls(size)
            }
        }

        private fun mapOrderActionToEnum(displayLabelValues: List<DisplayLabelValue>?): List<TradeItOrderActionParcelable> {
            val mappedValues = ArrayList<TradeItOrderActionParcelable>()
            if (displayLabelValues != null) {
                for (displayLabelValue in displayLabelValues) {
                    val action = TradeItOrderAction.getActionForValue(displayLabelValue.value)
                    if (action != null) {
                        mappedValues.add(TradeItOrderActionParcelable(action, displayLabelValue.displayLabel))
                    }
                }
            }
            return mappedValues
        }

        private fun mapOrderPriceTypesToEnum(displayLabelValues: List<DisplayLabelValue>?): List<TradeItOrderPriceTypeParcelable> {
            val mappedValues = ArrayList<TradeItOrderPriceTypeParcelable>()
            if (displayLabelValues != null) {
                for (displayLabelValue in displayLabelValues) {
                    val priceType = TradeItOrderPriceType.getPriceTypeForValue(displayLabelValue.value)
                    if (priceType != null) {
                        mappedValues.add(TradeItOrderPriceTypeParcelable(priceType, displayLabelValue.displayLabel))
                    }
                }
            }
            return mappedValues
        }

        private fun mapOrderExpirationTypesToEnum(displayLabelValues: List<DisplayLabelValue>?): List<TradeItOrderExpirationTypeParcelable> {
            val mappedValues = ArrayList<TradeItOrderExpirationTypeParcelable>()
            if (displayLabelValues != null) {
                for (displayLabelValue in displayLabelValues) {
                    val expiration = TradeItOrderExpirationType.getExpirationForValue(displayLabelValue.value)
                    if (expiration != null) {
                        mappedValues.add(TradeItOrderExpirationTypeParcelable(expiration, displayLabelValue.displayLabel))
                    }
                }
            }
            return mappedValues
        }
    }
}
