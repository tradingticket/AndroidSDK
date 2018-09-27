package it.trade.android.sdk.model

import android.os.Parcel
import android.support.test.filters.SmallTest
import android.support.test.runner.AndroidJUnit4
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class TradeItPlaceStockOrEtfOrderResponseParcelableTest {
    private var placeOrderResponse: TradeItPlaceStockOrEtfOrderResponseParcelable? = null

    @Before
    fun createTradeItPreviewStockOrEtfOrder() {
        placeOrderResponse = TradeItPlaceStockOrEtfOrderResponseParcelable()
    }

    @Test
    fun tradeItPreviewStockOrEtfOrder_ParcelableWriteRead() {
        // Set up the Parcelable object to send and receive.
        placeOrderResponse!!.orderNumber = "MyOrderId"
        placeOrderResponse!!.confirmationMessage = "MyConfirmationMessage"
        placeOrderResponse!!.broker = "MyBroker"
        placeOrderResponse!!.timestamp = "My time stamp"
        placeOrderResponse!!.orderInfo = TradeItOrderInfoParcelable()
        placeOrderResponse!!.orderInfo?.expiration = "MyExpiration"
        placeOrderResponse!!.orderInfo?.symbol = "MySymbol"
        placeOrderResponse!!.orderInfo?.quantity = 4.0
        placeOrderResponse!!.orderInfo?.price = TradeItPriceParcelable()
        placeOrderResponse!!.orderInfo?.price?.ask = 20.5

        // Write the data.
        val parcel = Parcel.obtain()
        placeOrderResponse!!.writeToParcel(parcel, placeOrderResponse!!.describeContents())

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0)

        // Read the data.
        val createdFromParcel = TradeItPlaceStockOrEtfOrderResponseParcelable.CREATOR.createFromParcel(parcel)
        val orderInfoParcelable = createdFromParcel.orderInfo
        val broker = createdFromParcel.broker
        val confirmationMessage = createdFromParcel.confirmationMessage
        val timestamp = createdFromParcel.timestamp
        val orderNumber = createdFromParcel.orderNumber

        // Verify that the received data is correct.
        assertThat(orderNumber, `is`(placeOrderResponse!!.orderNumber))
        assertThat(timestamp, `is`(placeOrderResponse!!.timestamp))
        assertThat(broker, `is`(placeOrderResponse!!.broker))
        assertThat(confirmationMessage, `is`(placeOrderResponse!!.confirmationMessage))
        assertThat(orderInfoParcelable?.action, `is`(placeOrderResponse!!.orderInfo?.action))
        assertThat(orderInfoParcelable?.price?.ask, `is`(placeOrderResponse!!.orderInfo?.price?.ask))
    }
}
