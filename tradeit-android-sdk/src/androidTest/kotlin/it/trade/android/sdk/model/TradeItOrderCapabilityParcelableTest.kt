package it.trade.android.sdk.model

import android.os.Parcel
import android.support.test.filters.SmallTest
import android.support.test.runner.AndroidJUnit4

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import java.util.Arrays

import it.trade.android.sdk.enums.TradeItOrderAction
import it.trade.android.sdk.enums.TradeItOrderExpirationType
import it.trade.android.sdk.enums.TradeItOrderPriceType
import it.trade.model.reponse.DisplayLabelValue
import it.trade.model.reponse.Instrument
import it.trade.model.reponse.OrderCapability

import org.hamcrest.Matchers.`is`
import org.hamcrest.core.IsNull.nullValue
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThat

@RunWith(AndroidJUnit4::class)
@SmallTest
class TradeItOrderCapabilityParcelableTest {

    private var orderCapabilityParcelable: TradeItOrderCapabilityParcelable? = null
    private var action: DisplayLabelValue? = null
    private var priceType: DisplayLabelValue? = null
    private var expirationType: DisplayLabelValue? = null

    @Before
    fun createTradeItOrderCapabilityParcelable() {
        val orderCapability = OrderCapability()
        action = DisplayLabelValue("Buy", "buy")
        priceType = DisplayLabelValue("Market", "market")
        expirationType = DisplayLabelValue("Good for day", "day")
        orderCapability.instrument = Instrument.EQUITIES
        orderCapability.actions = Arrays.asList(action!!)
        orderCapability.priceTypes = Arrays.asList(priceType!!)
        orderCapability.expirationTypes = Arrays.asList(expirationType!!)
        orderCapabilityParcelable = TradeItOrderCapabilityParcelable(orderCapability)
    }

    @Test
    fun orderCapabilities_ParcelableWriteRead() {
        // Write the data.
        val parcel = Parcel.obtain()
        orderCapabilityParcelable!!.writeToParcel(parcel, orderCapabilityParcelable!!.describeContents())

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0)

        // Read the data.
        val createdFromParcel = TradeItOrderCapabilityParcelable.CREATOR.createFromParcel(parcel)
        val instrument = createdFromParcel.instrument
        val actions = createdFromParcel.actions
        val expirationTypes = createdFromParcel.expirationTypes
        val priceTypes = createdFromParcel.priceTypes

        // Verify that the received data is correct.
        assertThat(instrument, `is`(Instrument.EQUITIES))
        assertFalse(actions.isEmpty())
        assertThat(actions[0].action.actionValue, `is`(action!!.value))
        assertThat(actions[0].displayLabel, `is`(action!!.displayLabel))
        assertFalse(expirationTypes.isEmpty())
        assertThat(expirationTypes[0].expirationType.expirationValue, `is`(expirationType!!.value))
        assertThat(expirationTypes[0].displayLabel, `is`(expirationType!!.displayLabel))
        assertFalse(priceTypes.isEmpty())
        assertThat(priceTypes[0].priceType.priceTypeValue, `is`(priceType!!.value))
        assertThat(priceTypes[0].displayLabel, `is`(priceType!!.displayLabel))
    }

    @Test
    fun orderCapabilities_getActionForEnumForBuy() {
        val actionParcelable = orderCapabilityParcelable!!.getActionForEnum(TradeItOrderAction.BUY)
        assertThat(actionParcelable!!.displayLabel, `is`("Buy"))
        assertThat(actionParcelable.action, `is`(TradeItOrderAction.BUY))
    }

    @Test
    fun orderCapabilities_getActionForEnumForUnknown() {
        val actionParcelable = orderCapabilityParcelable!!.getActionForEnum(TradeItOrderAction.SELL_SHORT)
        assertThat(actionParcelable, nullValue())
    }

    @Test
    fun orderCapabilities_getPriceTypeForEnumForMarket() {
        val priceTypeParcelable = orderCapabilityParcelable!!.getPriceTypeForEnum(TradeItOrderPriceType.MARKET)
        assertThat(priceTypeParcelable!!.displayLabel, `is`("Market"))
        assertThat(priceTypeParcelable.priceType, `is`(TradeItOrderPriceType.MARKET))
    }

    @Test
    fun orderCapabilities_getPriceTypeForEnumForUnknown() {
        val actionParcelable = orderCapabilityParcelable!!.getPriceTypeForEnum(TradeItOrderPriceType.LIMIT)
        assertThat(actionParcelable, nullValue())
    }


    @Test
    fun orderCapabilities_getExpirationTypeForEnumForDay() {
        val expirationTypeParcelable = orderCapabilityParcelable!!.getExpirationTypeForEnum(TradeItOrderExpirationType.GOOD_FOR_DAY)
        assertThat(expirationTypeParcelable!!.displayLabel, `is`("Good for day"))
        assertThat(expirationTypeParcelable.expirationType, `is`(TradeItOrderExpirationType.GOOD_FOR_DAY))
    }

    @Test
    fun orderCapabilities_getExpirationTypeForEnumForUnknown() {
        val expirationTypeParcelable = orderCapabilityParcelable!!.getExpirationTypeForEnum(TradeItOrderExpirationType.GOOD_UNTIL_CANCELED)
        assertThat(expirationTypeParcelable, nullValue())
    }
}
