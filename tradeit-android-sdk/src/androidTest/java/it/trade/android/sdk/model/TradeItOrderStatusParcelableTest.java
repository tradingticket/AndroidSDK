package it.trade.android.sdk.model;

import android.content.Context;
import android.os.Parcel;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import it.trade.android.sdk.TradeItConfigurationBuilder;
import it.trade.android.sdk.TradeItSDK;
import it.trade.android.sdk.model.orderstatus.TradeItOrderStatusParcelable;
import it.trade.model.reponse.OrderLeg;
import it.trade.model.reponse.OrderStatusDetails;
import it.trade.model.reponse.PriceInfo;
import it.trade.model.request.TradeItEnvironment;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class TradeItOrderStatusParcelableTest {

    private TradeItOrderStatusParcelable orderStatusParcelable;

    @Before
    public void createTradeItOrder() {
        Context instrumentationCtx = InstrumentationRegistry.getTargetContext();
        TradeItSDK.clearConfig();
        TradeItSDK.configure(new TradeItConfigurationBuilder(instrumentationCtx.getApplicationContext(), "tradeit-test-api-key", TradeItEnvironment.QA));

        OrderStatusDetails orderStatusDetails = new OrderStatusDetails();
        orderStatusDetails.orderExpiration = "GTC";
        orderStatusDetails.orderType = "EQUITY_OR_ETF";
        orderStatusDetails.orderStatus = "OPEN";
        orderStatusDetails.orderNumber = "1";
        OrderLeg orderLeg = new OrderLeg();
        orderLeg.symbol = "GE";
        orderLeg.filledQuantity = 0;
        orderLeg.orderedQuantity = 10;
        orderLeg.action = "BUY";
        PriceInfo priceInfo = new PriceInfo();
        priceInfo.type = "LIMIT";
        priceInfo.limitPrice = 20.3;
        orderLeg.priceInfo = priceInfo;
        orderStatusDetails.orderLegs.add(orderLeg);

        orderStatusParcelable = new TradeItOrderStatusParcelable(orderStatusDetails);
    }

    @Test
    public void order_ParcelableWriteRead() {

        // Write the data.
        Parcel parcel = Parcel.obtain();
        orderStatusParcelable.writeToParcel(parcel, orderStatusParcelable.describeContents());

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0);

        // Read the data.
        TradeItOrderStatusParcelable createdFromParcel = TradeItOrderStatusParcelable.CREATOR.createFromParcel(parcel);

        // Verify that the received data is correct.
        assertThat(createdFromParcel.getGroupOrderId(), is(orderStatusParcelable.getGroupOrderId()));
        assertThat(createdFromParcel.getGroupOrders(), is(orderStatusParcelable.getGroupOrders()));
        assertThat(createdFromParcel.getGroupOrderType(), is(orderStatusParcelable.getGroupOrderType()));
        assertThat(createdFromParcel.getOrderExpiration(), is(orderStatusParcelable.getOrderExpiration()));
        assertThat(createdFromParcel.getOrderLegs(), is(orderStatusParcelable.getOrderLegs()));
        assertThat(createdFromParcel.getOrderNumber(), is(orderStatusParcelable.getOrderNumber()));
        assertThat(createdFromParcel.getOrderStatus(), is(orderStatusParcelable.getOrderStatus()));
        assertThat(createdFromParcel.getOrderType(), is(orderStatusParcelable.getOrderType()));
    }
}
