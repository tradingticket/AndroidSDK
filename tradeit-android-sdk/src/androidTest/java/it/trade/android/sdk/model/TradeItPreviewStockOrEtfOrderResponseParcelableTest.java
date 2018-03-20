package it.trade.android.sdk.model;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import it.trade.model.reponse.Warning;
import it.trade.model.reponse.WarningLink;

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
        WarningLink link = new WarningLink();
        link.label = "Label";
        link.url = "URL";

        Warning warning = new Warning();
        warning.message = "Warning";
        warning.requiresAcknowledgement = true;
        warning.links = Arrays.asList(link);

        TradeItWarningParcelable warningParcelable = new TradeItWarningParcelable(warning);

        // Set up the Parcelable object to send and receive.
        previewOrderResponse.orderId = "MyOrderId";
        previewOrderResponse.ackWarningsList = Arrays.asList("MyAckWarning");
        previewOrderResponse.warningsList = Arrays.asList("MyWarningList");
        previewOrderResponse.orderDetails = new TradeItOrderDetailsParcelable();
        previewOrderResponse.orderDetails.orderSymbol = "GE";
        previewOrderResponse.orderDetails.orderCommissionLabel = "MyOrderCommissionLabel";
        previewOrderResponse.orderDetails.warnings = Arrays.asList(warningParcelable);
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
        List<TradeItWarningParcelable> warnings = createdFromParcel.orderDetails.warnings;

        String orderId = createdFromParcel.orderId;

        // Verify that the received data is correct.
        assertThat(orderId, is(previewOrderResponse.orderId));
        assertThat(ackWarningsList, is(previewOrderResponse.ackWarningsList));
        assertThat(warningsList, is(previewOrderResponse.warningsList));
        assertThat(orderDetailsParcelable.orderSymbol, is(previewOrderResponse.orderDetails.getOrderSymbol()));
        assertThat(orderDetailsParcelable.orderCommissionLabel, is(previewOrderResponse.orderDetails.getOrderCommissionLabel()));
        assertThat(warnings, is(previewOrderResponse.orderDetails.getWarnings()));
    }
}
