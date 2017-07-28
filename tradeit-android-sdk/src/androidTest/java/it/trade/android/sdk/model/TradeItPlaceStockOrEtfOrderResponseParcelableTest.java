package it.trade.android.sdk.model;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class TradeItPlaceStockOrEtfOrderResponseParcelableTest {
    private TradeItPlaceStockOrEtfOrderResponseParcelable placeOrderResponse;

    @Before
    public void createTradeItPreviewStockOrEtfOrder() {
        placeOrderResponse = new TradeItPlaceStockOrEtfOrderResponseParcelable();
    }

    @Test
    public void tradeItPreviewStockOrEtfOrder_ParcelableWriteRead() {
        // Set up the Parcelable object to send and receive.
        placeOrderResponse.orderNumber = "MyOrderId";
        placeOrderResponse.confirmationMessage = "MyConfirmationMessage";
        placeOrderResponse.broker = "MyBroker";
        placeOrderResponse.timestamp = "My time stamp";
        placeOrderResponse.orderInfo = new TradeItOrderInfoParcelable();
        placeOrderResponse.orderInfo.expiration = "MyExpiration";
        placeOrderResponse.orderInfo.symbol = "MySymbol";
        placeOrderResponse.orderInfo.quantity = 4;
        placeOrderResponse.orderInfo.price = new TradeItPriceParcelable();
        placeOrderResponse.orderInfo.price.ask = 20.5;

        // Write the data.
        Parcel parcel = Parcel.obtain();
        placeOrderResponse.writeToParcel(parcel, placeOrderResponse.describeContents());

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0);

        // Read the data.
        TradeItPlaceStockOrEtfOrderResponseParcelable createdFromParcel = TradeItPlaceStockOrEtfOrderResponseParcelable.CREATOR.createFromParcel(parcel);
        TradeItOrderInfoParcelable orderInfoParcelable = createdFromParcel.orderInfo;
        String broker = createdFromParcel.broker;
        String confirmationMessage = createdFromParcel.confirmationMessage;
        String timestamp = createdFromParcel.timestamp;
        String orderNumber = createdFromParcel.orderNumber;

        // Verify that the received data is correct.
        assertThat(orderNumber, is(placeOrderResponse.orderNumber));
        assertThat(timestamp, is(placeOrderResponse.timestamp));
        assertThat(broker, is(placeOrderResponse.broker));
        assertThat(confirmationMessage, is(placeOrderResponse.confirmationMessage));
        assertThat(orderInfoParcelable.action, is(placeOrderResponse.orderInfo.action));
        assertThat(orderInfoParcelable.price.ask, is(placeOrderResponse.orderInfo.price.ask));
    }
}
