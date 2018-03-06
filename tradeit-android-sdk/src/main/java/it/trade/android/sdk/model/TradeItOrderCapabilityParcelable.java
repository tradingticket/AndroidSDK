package it.trade.android.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import it.trade.android.sdk.enums.TradeItOrderAction;
import it.trade.android.sdk.enums.TradeItOrderExpiration;
import it.trade.android.sdk.enums.TradeItOrderPriceType;
import it.trade.model.reponse.DisplayLabelValue;
import it.trade.model.reponse.Instrument;
import it.trade.model.reponse.OrderCapability;

public class TradeItOrderCapabilityParcelable implements Parcelable {

    private Instrument instrument;
    private List<TradeItOrderActionParcelable> actions;
    private List<TradeItOrderPriceTypeParcelable> priceTypes;
    private List<TradeItOrderExpirationParcelable> expirationTypes;

    public TradeItOrderCapabilityParcelable(OrderCapability orderCapability) {
        this.instrument = orderCapability.getInstrument();
        this.actions = mapOrderActionToEnum(orderCapability.actions);
        this.priceTypes = mapOrderPriceTypesToEnum(orderCapability.priceTypes);
        this.expirationTypes = mapOrderExpirationTypesToEnum(orderCapability.expirationTypes);
    }

    public static List<TradeItOrderCapabilityParcelable> mapOrderCapabilitiesToTradeItOrderCapabilityParcelables(List<OrderCapability> orderCapabilities) {
        List<TradeItOrderCapabilityParcelable> orderCapabilityParcelables = new ArrayList<>();
        if (orderCapabilities != null) {
            for (OrderCapability orderCapability: orderCapabilities) {
                orderCapabilityParcelables.add(new TradeItOrderCapabilityParcelable(orderCapability));
            }
        }
        return orderCapabilityParcelables;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public List<TradeItOrderActionParcelable> getActions() {
        return actions;
    }

    public List<TradeItOrderPriceTypeParcelable> getPriceTypes() {
        return priceTypes;
    }

    public List<TradeItOrderExpirationParcelable> getExpirationTypes() {
        return expirationTypes;
    }

    @Override
    public String toString() {
        return "TradeItOrderCapabilityParcelable{" +
                "instrument=" + instrument +
                ", actions=" + actions +
                ", priceTypes=" + priceTypes +
                ", expirationTypes=" + expirationTypes +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TradeItOrderCapabilityParcelable that = (TradeItOrderCapabilityParcelable) o;

        if (instrument != that.instrument) return false;
        if (actions != null ? !actions.equals(that.actions) : that.actions != null) return false;
        if (priceTypes != null ? !priceTypes.equals(that.priceTypes) : that.priceTypes != null)
            return false;
        return expirationTypes != null ? expirationTypes.equals(that.expirationTypes) : that.expirationTypes == null;
    }

    @Override
    public int hashCode() {
        int result = instrument != null ? instrument.hashCode() : 0;
        result = 31 * result + (actions != null ? actions.hashCode() : 0);
        result = 31 * result + (priceTypes != null ? priceTypes.hashCode() : 0);
        result = 31 * result + (expirationTypes != null ? expirationTypes.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.instrument == null ? -1 : this.instrument.ordinal());
        dest.writeList(this.actions);
        dest.writeList(this.priceTypes);
        dest.writeList(this.expirationTypes);
    }

    protected TradeItOrderCapabilityParcelable(Parcel in) {
        int tmpInstrument = in.readInt();
        this.instrument = tmpInstrument == -1 ? null : Instrument.values()[tmpInstrument];
        this.actions = new ArrayList<>();
        in.readList(this.actions, TradeItOrderActionParcelable.class.getClassLoader());
        this.priceTypes = new ArrayList<>();
        in.readList(this.priceTypes, TradeItOrderPriceTypeParcelable.class.getClassLoader());
        this.expirationTypes = new ArrayList<>();
        in.readList(this.expirationTypes, TradeItOrderExpirationParcelable.class.getClassLoader());
    }

    public static final Parcelable.Creator<TradeItOrderCapabilityParcelable> CREATOR = new Parcelable.Creator<TradeItOrderCapabilityParcelable>() {
        @Override
        public TradeItOrderCapabilityParcelable createFromParcel(Parcel source) {
            return new TradeItOrderCapabilityParcelable(source);
        }

        @Override
        public TradeItOrderCapabilityParcelable[] newArray(int size) {
            return new TradeItOrderCapabilityParcelable[size];
        }
    };

    private static List<TradeItOrderActionParcelable> mapOrderActionToEnum(List<DisplayLabelValue> displayLabelValues) {
        List<TradeItOrderActionParcelable> mappedValues = new ArrayList<>();
        if (displayLabelValues != null) {
            for (DisplayLabelValue displayLabelValue: displayLabelValues) {
                TradeItOrderAction action = TradeItOrderAction.getActionForValue(displayLabelValue.value);
                if (action != null) {
                    mappedValues.add(new TradeItOrderActionParcelable(action, displayLabelValue.displayLabel));
                }
            }
        }
        return mappedValues;
    }

    private static List<TradeItOrderPriceTypeParcelable> mapOrderPriceTypesToEnum(List<DisplayLabelValue> displayLabelValues) {
        List<TradeItOrderPriceTypeParcelable> mappedValues = new ArrayList<>();
        if (displayLabelValues != null) {
            for (DisplayLabelValue displayLabelValue: displayLabelValues) {
                TradeItOrderPriceType priceType = TradeItOrderPriceType.getPriceTypeForValue(displayLabelValue.value);
                if (priceType != null) {
                    mappedValues.add(new TradeItOrderPriceTypeParcelable(priceType, displayLabelValue.displayLabel));
                }
            }
        }
        return mappedValues;
    }

    private static List<TradeItOrderExpirationParcelable> mapOrderExpirationTypesToEnum(List<DisplayLabelValue> displayLabelValues) {
        List<TradeItOrderExpirationParcelable> mappedValues = new ArrayList<>();
        if (displayLabelValues != null) {
            for (DisplayLabelValue displayLabelValue: displayLabelValues) {
                TradeItOrderExpiration expiration = TradeItOrderExpiration.getExpirationForValue(displayLabelValue.value);
                if (expiration != null) {
                    mappedValues.add(new TradeItOrderExpirationParcelable(expiration, displayLabelValue.displayLabel));
                }
            }
        }
        return mappedValues;
    }
}
