package it.trade.android.sdk.model

import android.os.Parcel
import android.support.test.filters.SmallTest
import android.support.test.runner.AndroidJUnit4
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
class TradeItPreviewStockOrEtfOrderResponseParcelableTest {
    protected var previewOrderResponse: TradeItPreviewStockOrEtfOrderResponseParcelable? = null

    @Before
    fun createTradeItPreviewStockOrEtfOrder() {
        previewOrderResponse = TradeItPreviewStockOrEtfOrderResponseParcelable()
    }

    @Test
    fun tradeItPreviewStockOrEtfOrder_ParcelableWriteRead() {
        val link = WarningLink()
        link.label = "Label"
        link.url = "URL"

        val warning = Warning()
        warning.message = "Warning"
        warning.requiresAcknowledgement = true
        warning.links = Arrays.asList(link)

        val warningParcelable = TradeItWarningParcelable(warning)

        // Set up the Parcelable object to send and receive.
        previewOrderResponse?.orderId = "MyOrderId"
        previewOrderResponse?.ackWarningsList = Arrays.asList("MyAckWarning")
        previewOrderResponse?.warningsList = Arrays.asList("MyWarningList")
        previewOrderResponse?.orderDetails = TradeItOrderDetailsParcelable()
        previewOrderResponse?.orderDetails?.orderSymbol = "GE"
        previewOrderResponse?.orderDetails?.orderCommissionLabel = "MyOrderCommissionLabel"
        previewOrderResponse?.orderDetails?.warnings = Arrays.asList(warningParcelable)
        // Write the data.
        val parcel = Parcel.obtain()
        previewOrderResponse?.writeToParcel(parcel, previewOrderResponse?.describeContents()!!)

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0)

        // Read the data.
        val createdFromParcel = TradeItPreviewStockOrEtfOrderResponseParcelable.CREATOR.createFromParcel(parcel)
        val orderDetailsParcelable = createdFromParcel.orderDetails
        val ackWarningsList = createdFromParcel.ackWarningsList
        val warningsList = createdFromParcel.warningsList
        val warnings = createdFromParcel.orderDetails?.warnings

        val orderId = createdFromParcel.orderId

        // Verify that the received data is correct.
        assertThat(orderId, `is`(previewOrderResponse?.orderId))
        assertThat(ackWarningsList, `is`(previewOrderResponse?.ackWarningsList))
        assertThat(warningsList, `is`(previewOrderResponse?.warningsList))
        assertThat(orderDetailsParcelable?.orderSymbol, `is`(previewOrderResponse?.orderDetails?.orderSymbol))
        assertThat(orderDetailsParcelable?.orderCommissionLabel, `is`(previewOrderResponse?.orderDetails?.orderCommissionLabel))
        assertThat(warnings, `is`(previewOrderResponse?.orderDetails?.warnings))
    }
}
