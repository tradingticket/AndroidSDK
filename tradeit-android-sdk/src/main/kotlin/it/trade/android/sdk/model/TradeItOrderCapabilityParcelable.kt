package it.trade.android.sdk.model

import android.os.Parcel
import android.os.Parcelable
import it.trade.android.sdk.enums.TradeItOrderAction
import it.trade.android.sdk.enums.TradeItOrderExpirationType
import it.trade.android.sdk.enums.TradeItOrderPriceType
import it.trade.model.reponse.DisplayLabelValue
import it.trade.model.reponse.Instrument
import it.trade.model.reponse.OrderCapability

class TradeItOrderCapabilityParcelable : Parcelable {

    var instrument: Instrument
        private set
    var actions: List<TradeItOrderActionParcelable> = ArrayList()
        private set
    var priceTypes: List<TradeItOrderPriceTypeParcelable> = ArrayList()
        private set
    var expirationTypes: List<TradeItOrderExpirationTypeParcelable> = ArrayList()
        private set

    constructor(orderCapability: OrderCapability) {
        this.instrument = orderCapability.instrument
        this.actions = mapOrderActionToEnum(orderCapability.actions)
        this.priceTypes = mapOrderPriceTypesToEnum(orderCapability.priceTypes)
        this.expirationTypes = mapOrderExpirationTypesToEnum(orderCapability.expirationTypes)
    }

    fun getActionForEnum(actionEnum: TradeItOrderAction): TradeItOrderActionParcelable? {
        for (action in actions) {
            if (action.action == actionEnum) {
                return action
            }
        }
        return null
    }

    fun getPriceTypeForEnum(priceTypeEnum: TradeItOrderPriceType): TradeItOrderPriceTypeParcelable? {
        for (priceType in priceTypes) {
            if (priceType.priceType == priceTypeEnum) {
                return priceType
            }
        }
        return null
    }

    fun getExpirationTypeForEnum(expirationEnum: TradeItOrderExpirationType): TradeItOrderExpirationTypeParcelable? {
        for (expiration in expirationTypes) {
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TradeItOrderCapabilityParcelable

        if (instrument != other.instrument) return false
        if (actions != other.actions) return false
        if (priceTypes != other.priceTypes) return false
        if (expirationTypes != other.expirationTypes) return false

        return true
    }

    override fun hashCode(): Int {
        var result = instrument.hashCode()
        result = 31 * result + actions.hashCode()
        result = 31 * result + priceTypes.hashCode()
        result = 31 * result + expirationTypes.hashCode()
        return result
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(this.instrument.ordinal)
        dest.writeList(this.actions)
        dest.writeList(this.priceTypes)
        dest.writeList(this.expirationTypes)
    }

    protected constructor(`in`: Parcel) {
        val tmpInstrument = `in`.readInt()
        this.instrument = Instrument.values()[tmpInstrument]
        this.actions = ArrayList()
        `in`.readList(this.actions, TradeItOrderActionParcelable::class.java.getClassLoader())
        this.priceTypes = ArrayList()
        `in`.readList(this.priceTypes, TradeItOrderPriceTypeParcelable::class.java.getClassLoader())
        this.expirationTypes = ArrayList()
        `in`.readList(this.expirationTypes, TradeItOrderExpirationTypeParcelable::class.java.getClassLoader())
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
        @JvmField
        val CREATOR: Parcelable.Creator<TradeItOrderCapabilityParcelable> = object : Parcelable.Creator<TradeItOrderCapabilityParcelable> {
            override fun createFromParcel(source: Parcel): TradeItOrderCapabilityParcelable {
                return TradeItOrderCapabilityParcelable(source)
            }

            override fun newArray(size: Int): Array<TradeItOrderCapabilityParcelable?> {
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
