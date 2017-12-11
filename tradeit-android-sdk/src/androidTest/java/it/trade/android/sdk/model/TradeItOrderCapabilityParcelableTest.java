package it.trade.android.sdk.model;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import it.trade.model.reponse.DisplayLabelValue;
import it.trade.model.reponse.Instrument;
import it.trade.model.reponse.OrderCapability;

import static org.hamcrest.Matchers.is;
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
        List<DisplayLabelValueParcelable> actions = createdFromParcel.getActions();
        List<DisplayLabelValueParcelable> expirationTypes  = createdFromParcel.getExpirationTypes();
        List<DisplayLabelValueParcelable> priceTypes  = createdFromParcel.getPriceTypes();

        // Verify that the received data is correct.
        assertThat(instrument, is(Instrument.EQUITIES));
        assertFalse(actions.isEmpty());
        assertThat(actions.get(0).getValue(), is(action.value));
        assertThat(actions.get(0).getDisplayLabel(), is(action.displayLabel));
        assertFalse(expirationTypes.isEmpty());
        assertThat(expirationTypes.get(0).getValue(), is(expirationType.value));
        assertThat(expirationTypes.get(0).getDisplayLabel(), is(expirationType.displayLabel));
        assertFalse(priceTypes.isEmpty());
        assertThat(priceTypes.get(0).getValue(), is(priceType.value));
        assertThat(priceTypes.get(0).getDisplayLabel(), is(priceType.displayLabel));

    }
}
