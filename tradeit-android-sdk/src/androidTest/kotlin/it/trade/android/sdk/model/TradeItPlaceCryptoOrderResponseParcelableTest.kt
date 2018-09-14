package it.trade.android.sdk.model

import android.os.Parcel
import android.support.test.runner.AndroidJUnit4
import android.test.suitebuilder.annotation.SmallTest
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class TradeItPlaceCryptoOrderResponseParcelableTest {
    private var placeCryptoOrderResponse: TradeItPlaceCryptoOrderResponseParcelable? = null

    @Before
    fun createTradeItPlaceCryptoOrder() {
        placeCryptoOrderResponse = TradeItPlaceCryptoOrderResponseParcelable()
    }

    @Test
    fun tradeItPlaceCryptoOrder_ParcelableWriteRead() {
        // Set up the Parcelable object to send and receive.
        placeCryptoOrderResponse!!.orderNumber = "MyOrderId"
        placeCryptoOrderResponse!!.confirmationMessage = "MyConfirmationMessage"
        placeCryptoOrderResponse!!.broker = "MyBroker"
        placeCryptoOrderResponse!!.timestamp = "My time stamp"
        placeCryptoOrderResponse!!.orderDetails = TradeItCryptoTradeOrderDetailsParcelable()
        placeCryptoOrderResponse!!.orderDetails!!.orderExpiration = "MyExpiration"
        placeCryptoOrderResponse!!.orderDetails!!.orderPair = "MySymbol"
        placeCryptoOrderResponse!!.orderDetails!!.orderQuantity = 4.0
        placeCryptoOrderResponse!!.orderDetails!!.orderQuantityType = "MyOrderQuantityType"
        placeCryptoOrderResponse!!.orderDetails!!.orderLimitPrice = 10.0

        // Write the data.
        val parcel = Parcel.obtain()
        placeCryptoOrderResponse!!.writeToParcel(parcel, placeCryptoOrderResponse!!.describeContents())

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0)

        // Read the data.
        val createdFromParcel = TradeItPlaceCryptoOrderResponseParcelable.CREATOR.createFromParcel(parcel)
        val orderDetailsParcelable = createdFromParcel.orderDetails
        val broker = createdFromParcel.broker
        val confirmationMessage = createdFromParcel.confirmationMessage
        val timestamp = createdFromParcel.timestamp
        val orderNumber = createdFromParcel.orderNumber

        // Verify that the received data is correct.
        assertThat(orderNumber, `is`(placeCryptoOrderResponse!!.orderNumber))
        assertThat(timestamp, `is`(placeCryptoOrderResponse!!.timestamp))
        assertThat(broker, `is`(placeCryptoOrderResponse!!.broker))
        assertThat(confirmationMessage, `is`(placeCryptoOrderResponse!!.confirmationMessage))
        assertThat(
            orderDetailsParcelable?.orderAction,
            `is`(placeCryptoOrderResponse!!.orderDetails!!.orderAction)
        )
        assertThat(
            orderDetailsParcelable?.orderLimitPrice,
            `is`(placeCryptoOrderResponse!!.orderDetails!!.orderLimitPrice)
        )
        assertThat(
            orderDetailsParcelable?.orderQuantityType,
            `is`(placeCryptoOrderResponse!!.orderDetails!!.orderQuantityType)
        )
        assertThat(
            orderDetailsParcelable?.orderQuantity,
            `is`(placeCryptoOrderResponse!!.orderDetails!!.orderQuantity)
        )
        assertThat(
            orderDetailsParcelable?.orderPair,
            `is`(placeCryptoOrderResponse!!.orderDetails!!.orderPair)
        )
        assertThat(
            orderDetailsParcelable?.orderExpiration,
            `is`(placeCryptoOrderResponse!!.orderDetails!!.orderExpiration)
        )
    }
}
