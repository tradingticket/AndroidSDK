package it.trade.android.sdk.model

import android.os.Parcel
import android.support.test.runner.AndroidJUnit4
import android.test.suitebuilder.annotation.SmallTest
import it.trade.model.reponse.Warning
import it.trade.model.reponse.WarningLink
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
@SmallTest
class TradeItPreviewCryptoOrderResponseParcelableTest {
    protected var previewCryptoOrderResponse: TradeItPreviewCryptoOrderResponseParcelable? = null

    @Before
    fun createTradeItPreviewCryptoOrder() {
        previewCryptoOrderResponse = TradeItPreviewCryptoOrderResponseParcelable()
    }

    @Test
    fun tradeItPreviewCryptoOrder_ParcelableWriteRead() {
        val link = WarningLink()
        link.label = "Label"
        link.url = "URL"

        val warning = Warning()
        warning.message = "Warning"
        warning.requiresAcknowledgement = true
        warning.links = Arrays.asList(link)

        val warningParcelable = TradeItWarningParcelable(warning)

        // Set up the Parcelable object to send and receive.
        previewCryptoOrderResponse?.orderId = "MyOrderId"
        previewCryptoOrderResponse?.orderDetails = TradeItCryptoPreviewOrderDetailsParcelable()
        previewCryptoOrderResponse?.orderDetails?.orderPair = "MyOrderPair"
        previewCryptoOrderResponse?.orderDetails?.orderQuantityType = "MyOrderquantityType"
        previewCryptoOrderResponse?.orderDetails?.orderCommissionLabel = "MyOrderCommissionLabel"
        previewCryptoOrderResponse?.orderDetails?.orderQuantity = 100.0
        previewCryptoOrderResponse?.orderDetails?.warnings = Arrays.asList(warningParcelable)

        // Write the data.
        val parcel = Parcel.obtain()
        previewCryptoOrderResponse?.writeToParcel(parcel, previewCryptoOrderResponse?.describeContents()!!)

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0)

        // Read the data.
        val createdFromParcel = TradeItPreviewCryptoOrderResponseParcelable.CREATOR.createFromParcel(parcel)
        val orderDetailsParcelable = createdFromParcel.orderDetails
        val warnings = createdFromParcel.orderDetails?.warnings

        val orderId = createdFromParcel.orderId

        // Verify that the received data is correct.
        assertThat(orderId, `is`(previewCryptoOrderResponse?.orderId))
        assertThat(orderDetailsParcelable?.orderPair, `is`(previewCryptoOrderResponse?.orderDetails?.orderPair))
        assertThat(orderDetailsParcelable?.orderQuantity, `is`(previewCryptoOrderResponse?.orderDetails?.orderQuantity))
        assertThat(orderDetailsParcelable?.orderQuantityType, `is`(previewCryptoOrderResponse?.orderDetails?.orderQuantityType))
        assertThat(orderDetailsParcelable?.orderCommissionLabel, `is`(previewCryptoOrderResponse?.orderDetails?.orderCommissionLabel))
        assertThat(warnings, `is`(previewCryptoOrderResponse?.orderDetails?.warnings))
    }
}
