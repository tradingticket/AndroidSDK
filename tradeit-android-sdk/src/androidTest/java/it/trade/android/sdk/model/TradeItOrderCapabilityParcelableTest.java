package it.trade.android.sdk.model;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import it.trade.android.sdk.enums.TradeItOrderAction;
import it.trade.android.sdk.enums.TradeItOrderExpirationType;
import it.trade.android.sdk.enums.TradeItOrderPriceType;
import it.trade.model.reponse.DisplayLabelValue;
import it.trade.model.reponse.Instrument;
import it.trade.model.reponse.OrderCapability;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class TradeItOrderCapabilityParcelableTest {

    private TradeItOrderCapabilityParcelable orderCapabilityParcelable;
    private DisplayLabelValue action;
    private DisplayLabelValue priceType;
    private DisplayLabelValue expirationType;

    @Before
    public void createTradeItOrderCapabilityParcelable() {
        OrderCapability orderCapability = new OrderCapability();
        action = new DisplayLabelValue("Buy", "buy");
        priceType = new DisplayLabelValue("Market", "market");
        expirationType = new DisplayLabelValue("Good for day", "day");
        orderCapability.setInstrument(Instrument.EQUITIES);
        orderCapability.actions = Arrays.asList(action);
        orderCapability.priceTypes = Arrays.asList(priceType);
        orderCapability.expirationTypes = Arrays.asList(expirationType);
        orderCapabilityParcelable = new TradeItOrderCapabilityParcelable(orderCapability);
    }

    @Test
    public void orderCapabilities_ParcelableWriteRead() {
        // Write the data.
        Parcel parcel = Parcel.obtain();
        orderCapabilityParcelable.writeToParcel(parcel, orderCapabilityParcelable.describeContents());

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0);

        // Read the data.
        TradeItOrderCapabilityParcelable createdFromParcel = TradeItOrderCapabilityParcelable.CREATOR.createFromParcel(parcel);
        Instrument instrument = createdFromParcel.getInstrument();
        List<TradeItOrderActionParcelable> actions = createdFromParcel.getActions();
        List<TradeItOrderExpirationTypeParcelable> expirationTypes = createdFromParcel.getExpirationTypes();
        List<TradeItOrderPriceTypeParcelable> priceTypes  = createdFromParcel.getPriceTypes();

        // Verify that the received data is correct.
        assertThat(instrument, is(Instrument.EQUITIES));
        assertFalse(actions.isEmpty());
        assertThat(actions.get(0).getAction().getActionValue(), is(action.value));
        assertThat(actions.get(0).getDisplayLabel(), is(action.displayLabel));
        assertFalse(expirationTypes.isEmpty());
        assertThat(expirationTypes.get(0).getExpirationType().getExpirationValue(), is(expirationType.value));
        assertThat(expirationTypes.get(0).getDisplayLabel(), is(expirationType.displayLabel));
        assertFalse(priceTypes.isEmpty());
        assertThat(priceTypes.get(0).getPriceType().getPriceTypeValue(), is(priceType.value));
        assertThat(priceTypes.get(0).getDisplayLabel(), is(priceType.displayLabel));
    }

    @Test
    public void orderCapabilities_getActionForEnumForBuy() {
        TradeItOrderActionParcelable actionParcelable = orderCapabilityParcelable.getActionForEnum(TradeItOrderAction.BUY);
        assertThat(actionParcelable.getDisplayLabel(), is("Buy"));
        assertThat(actionParcelable.getAction(), is(TradeItOrderAction.BUY));
    }

    @Test
    public void orderCapabilities_getActionForEnumForUnknown() {
        TradeItOrderActionParcelable actionParcelable = orderCapabilityParcelable.getActionForEnum(TradeItOrderAction.SELL_SHORT);
        assertThat(actionParcelable, nullValue());
    }

    @Test
    public void orderCapabilities_getPriceTypeForEnumForMarket() {
        TradeItOrderPriceTypeParcelable priceTypeParcelable = orderCapabilityParcelable.getPriceTypeForEnum(TradeItOrderPriceType.MARKET);
        assertThat(priceTypeParcelable.getDisplayLabel(), is("Market"));
        assertThat(priceTypeParcelable.getPriceType(), is(TradeItOrderPriceType.MARKET));
    }

    @Test
    public void orderCapabilities_getPriceTypeForEnumForUnknown() {
        TradeItOrderPriceTypeParcelable actionParcelable = orderCapabilityParcelable.getPriceTypeForEnum(TradeItOrderPriceType.LIMIT);
        assertThat(actionParcelable, nullValue());
    }


    @Test
    public void orderCapabilities_getExpirationTypeForEnumForDay() {
        TradeItOrderExpirationTypeParcelable expirationTypeParcelable = orderCapabilityParcelable.getExpirationTypeForEnum(TradeItOrderExpirationType.GOOD_FOR_DAY);
        assertThat(expirationTypeParcelable.getDisplayLabel(), is("Good for day"));
        assertThat(expirationTypeParcelable.getExpirationType(), is(TradeItOrderExpirationType.GOOD_FOR_DAY));
    }

    @Test
    public void orderCapabilities_getExpirationTypeForEnumForUnknown() {
        TradeItOrderExpirationTypeParcelable expirationTypeParcelable = orderCapabilityParcelable.getExpirationTypeForEnum(TradeItOrderExpirationType.GOOD_UNTIL_CANCELED);
        assertThat(expirationTypeParcelable, nullValue());
    }
}
