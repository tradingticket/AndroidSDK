package it.trade.android.sdk.model

import android.content.Context
import android.os.Parcel
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.test.suitebuilder.annotation.SmallTest

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import it.trade.android.sdk.TradeItConfigurationBuilder
import it.trade.android.sdk.TradeItSDK
import it.trade.android.sdk.model.orderstatus.TradeItOrderStatusParcelable
import it.trade.model.reponse.OrderLeg
import it.trade.model.reponse.OrderStatusDetails
import it.trade.model.reponse.PriceInfo
import it.trade.model.request.TradeItEnvironment

import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertThat

@RunWith(AndroidJUnit4::class)
@SmallTest
class TradeItOrderStatusParcelableTest {

    private var orderStatusParcelable: TradeItOrderStatusParcelable? = null

    @Before
    fun createTradeItOrder() {
        val instrumentationCtx = InstrumentationRegistry.getTargetContext()
        TradeItSDK.clearConfig()
        TradeItSDK.configure(TradeItConfigurationBuilder(instrumentationCtx.applicationContext, "tradeit-test-api-key", TradeItEnvironment.QA))

        val orderStatusDetails = OrderStatusDetails()
        orderStatusDetails.orderExpiration = "GTC"
        orderStatusDetails.orderType = "EQUITY_OR_ETF"
        orderStatusDetails.orderStatus = "OPEN"
        orderStatusDetails.orderNumber = "1"
        val orderLeg = OrderLeg()
        orderLeg.symbol = "GE"
        orderLeg.filledQuantity = 0
        orderLeg.orderedQuantity = 10
        orderLeg.action = "BUY"
        val priceInfo = PriceInfo()
        priceInfo.type = "LIMIT"
        priceInfo.limitPrice = 20.3
        orderLeg.priceInfo = priceInfo
        orderStatusDetails.orderLegs.add(orderLeg)

        orderStatusParcelable = TradeItOrderStatusParcelable(orderStatusDetails)
    }

    @Test
    fun order_ParcelableWriteRead() {

        // Write the data.
        val parcel = Parcel.obtain()
        orderStatusParcelable!!.writeToParcel(parcel, orderStatusParcelable!!.describeContents())

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0)

        // Read the data.
        val createdFromParcel = TradeItOrderStatusParcelable.CREATOR.createFromParcel(parcel)

        // Verify that the received data is correct.
        assertThat(createdFromParcel.groupOrderId, `is`(orderStatusParcelable!!.groupOrderId))
        assertThat(createdFromParcel.groupOrders, `is`(orderStatusParcelable!!.groupOrders))
        assertThat(createdFromParcel.groupOrderType, `is`(orderStatusParcelable!!.groupOrderType))
        assertThat(createdFromParcel.orderExpiration, `is`(orderStatusParcelable!!.orderExpiration))
        assertThat<List<TradeItOrderLegParcelable>>(createdFromParcel.orderLegs, `is`<List<TradeItOrderLegParcelable>>(orderStatusParcelable!!.orderLegs))
        assertThat(createdFromParcel.orderNumber, `is`(orderStatusParcelable!!.orderNumber))
        assertThat(createdFromParcel.orderStatus, `is`(orderStatusParcelable!!.orderStatus))
        assertThat(createdFromParcel.orderType, `is`(orderStatusParcelable!!.orderType))
    }
}
