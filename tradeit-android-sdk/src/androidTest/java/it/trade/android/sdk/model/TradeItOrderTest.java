package it.trade.android.sdk.model;

import android.content.Context;
import android.os.Parcel;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import it.trade.android.sdk.TradeItSDK;
import it.trade.android.sdk.enums.TradeItOrderAction;
import it.trade.android.sdk.enums.TradeItOrderExpiration;
import it.trade.android.sdk.enums.TradeItOrderPriceType;
import it.trade.tradeitapi.API.TradeItApiClient;
import it.trade.tradeitapi.model.Account;
import it.trade.tradeitapi.model.TradeItEnvironment;
import it.trade.tradeitapi.model.TradeItLinkedLogin;
import it.trade.tradeitapi.model.TradeItOAuthAccessTokenRequest;
import it.trade.tradeitapi.model.TradeItOAuthAccessTokenResponse;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class TradeItOrderTest {
    private TradeItOrder order;
    private TradeItLinkedBrokerAccount linkedBrokerAccount;

    @Before
    public void createTradeItOrder() {
        Context instrumentationCtx = InstrumentationRegistry.getTargetContext();
        TradeItSDK.clearConfig();
        TradeItSDK.configure(instrumentationCtx.getApplicationContext(), "tradeit-test-api-key", TradeItEnvironment.QA);

        TradeItOAuthAccessTokenRequest oAuthAccessTokenRequest = new TradeItOAuthAccessTokenRequest("MyOauthVerifier");
        oAuthAccessTokenRequest.apiKey = "MyApiKey";
        TradeItOAuthAccessTokenResponse oAuthAccessTokenResponse = new TradeItOAuthAccessTokenResponse();
        oAuthAccessTokenResponse.userId = "MyUserId";
        oAuthAccessTokenResponse.userToken = "MyUserToken";

        TradeItLinkedBroker linkedBroker = new TradeItLinkedBroker(new TradeItApiClient(new TradeItLinkedLogin("MyBroker", oAuthAccessTokenRequest,
                oAuthAccessTokenResponse), TradeItSDK.getEnvironment()));

        Account account = new Account();
        account.accountNumber = "MyAccountnumber";
        account.accountBaseCurrency = "MyAccountBaseCurrency";
        account.name = "MyAccountname";

        linkedBrokerAccount = new TradeItLinkedBrokerAccount(linkedBroker, account);

        order = new TradeItOrder(linkedBrokerAccount, "MySymbol");
    }

    @Test
    public void order_ParcelableWriteRead() {
        // Set up the Parcelable object to send and receive.
        order.setAction(TradeItOrderAction.BUY);
        order.setExpiration(TradeItOrderExpiration.GOOD_FOR_DAY);
        order.setPriceType(TradeItOrderPriceType.LIMIT);
        order.setLimitPrice(20.50);
        order.setQuantity(1);
        order.setQuoteLastPrice(21.50);

        // Write the data.
        Parcel parcel = Parcel.obtain();
        order.writeToParcel(parcel, order.describeContents());

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0);

        // Read the data.
        TradeItOrder createdFromParcel = TradeItOrder.CREATOR.createFromParcel(parcel);
        TradeItLinkedBrokerAccount createdLinkedBrokerAccount = createdFromParcel.getLinkedBrokerAccount();
        TradeItOrderAction action = createdFromParcel.getAction();
        TradeItOrderExpiration expiration = createdFromParcel.getExpiration();
        Double limitPrice = createdFromParcel.getLimitPrice();
        TradeItOrderPriceType priceType = createdFromParcel.getPriceType();
        int quantity = createdFromParcel.getQuantity();
        Double stopPrice = createdFromParcel.getStopPrice();
        Double lastPrice = createdFromParcel.getQuoteLastPrice();
        String symbol = createdFromParcel.getSymbol();


        // Verify that the received data is correct.
        assertThat(createdLinkedBrokerAccount, is(linkedBrokerAccount));
        assertThat(createdLinkedBrokerAccount.getTradeItApiClient(), notNullValue());

        assertThat(action, is(order.getAction()));
        assertThat(expiration, is(order.getExpiration()));
        assertThat(limitPrice, is(order.getLimitPrice()));
        assertThat(priceType, is(order.getPriceType()));
        assertThat(quantity, is(order.getQuantity()));
        assertThat(stopPrice, is(order.getStopPrice()));
        assertThat(lastPrice, is(order.getQuoteLastPrice()));
        assertThat(symbol, is(order.getSymbol()));
    }
}
