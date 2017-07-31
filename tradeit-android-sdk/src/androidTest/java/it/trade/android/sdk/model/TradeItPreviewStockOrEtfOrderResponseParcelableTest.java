package it.trade.android.sdk.model;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class TradeItPreviewStockOrEtfOrderResponseParcelableTest {
    protected TradeItPreviewStockOrEtfOrderResponseParcelable previewOrderResponse;

    @Before
    public void createTradeItPreviewStockOrEtfOrder() {
        previewOrderResponse = new TradeItPreviewStockOrEtfOrderResponseParcelable();
    }

    @Test
    public void tradeItPreviewStockOrEtfOrder_ParcelableWriteRead() {
        // Set up the Parcelable object to send and receive.
        previewOrderResponse.orderId = "MyOrderId";
        previewOrderResponse.ackWarningsList = Arrays.asList("MyAckWarning");
        previewOrderResponse.warningsList = Arrays.asList("MyWarningList");
        previewOrderResponse.orderDetails = new TradeItOrderDetailsParcelable();
        previewOrderResponse.orderDetails.orderSymbol = "GE";

        // Write the data.
        Parcel parcel = Parcel.obtain();
        previewOrderResponse.writeToParcel(parcel, previewOrderResponse.describeContents());

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0);

        // Read the data.
        TradeItPreviewStockOrEtfOrderResponseParcelable createdFromParcel = TradeItPreviewStockOrEtfOrderResponseParcelable.CREATOR.createFromParcel(parcel);
        TradeItOrderDetailsParcelable orderDetailsParcelable = createdFromParcel.getOrderDetails();
        List<String> ackWarningsList = createdFromParcel.ackWarningsList;
        List<String> warningsList = createdFromParcel.warningsList;
        String orderId = createdFromParcel.orderId;

        // Verify that the received data is correct.
        assertThat(orderId, is(previewOrderResponse.orderId));
        assertThat(ackWarningsList, is(previewOrderResponse.ackWarningsList));
        assertThat(warningsList, is(previewOrderResponse.warningsList));
        assertThat(orderDetailsParcelable.orderSymbol, is(previewOrderResponse.orderDetails.orderSymbol));
    }
}
