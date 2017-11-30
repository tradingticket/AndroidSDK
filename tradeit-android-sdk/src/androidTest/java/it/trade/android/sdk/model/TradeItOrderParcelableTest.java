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
import it.trade.android.sdk.enums.TradeItOrderAction;
import it.trade.android.sdk.enums.TradeItOrderExpiration;
import it.trade.android.sdk.enums.TradeItOrderPriceType;
import it.trade.model.reponse.TradeItBrokerAccount;
import it.trade.model.reponse.TradeItOAuthAccessTokenResponse;
import it.trade.model.request.TradeItEnvironment;
import it.trade.model.request.TradeItOAuthAccessTokenRequest;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class TradeItOrderParcelableTest {
    private TradeItOrderParcelable order;
    private TradeItLinkedBrokerAccountParcelable linkedBrokerAccount;

    @Before
    public void createTradeItOrder() {
        Context instrumentationCtx = InstrumentationRegistry.getTargetContext();
        TradeItSDK.clearConfig();
        TradeItSDK.configure(new TradeItConfigurationBuilder(instrumentationCtx.getApplicationContext(), "tradeit-test-api-key", TradeItEnvironment.QA));

        TradeItOAuthAccessTokenRequest oAuthAccessTokenRequest = new TradeItOAuthAccessTokenRequest("MyOauthVerifier");
        oAuthAccessTokenRequest.apiKey = "MyApiKey";
        TradeItOAuthAccessTokenResponse oAuthAccessTokenResponse = new TradeItOAuthAccessTokenResponse();
        oAuthAccessTokenResponse.userId = "MyUserId";
        oAuthAccessTokenResponse.userToken = "MyUserToken";
        oAuthAccessTokenResponse.broker = "MyBroker";
        TradeItLinkedLoginParcelable linkedLogin = new TradeItLinkedLoginParcelable(oAuthAccessTokenRequest, oAuthAccessTokenResponse);
        TradeItApiClientParcelable apiClient = new TradeItApiClientParcelable("MyApiKey", TradeItSDK.getEnvironment());

        apiClient.setSessionToken("MySessionToken");
        TradeItLinkedBrokerParcelable linkedBroker = new TradeItLinkedBrokerParcelable(apiClient, linkedLogin, TradeItSDK.getLinkedBrokerCache());

        TradeItBrokerAccount account = new TradeItBrokerAccount();
        account.accountNumber = "MyAccountNumber";
        account.accountBaseCurrency = "MyAccountBaseCurrency";
        account.name = "MyAccountName";

        linkedBrokerAccount = new TradeItLinkedBrokerAccountParcelable(linkedBroker, account);

        order = new TradeItOrderParcelable(linkedBrokerAccount, "MySymbol");
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
        order.setUserDisabledMargin(true);
        // Write the data.
        Parcel parcel = Parcel.obtain();
        order.writeToParcel(parcel, order.describeContents());

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0);

        // Read the data.
        TradeItOrderParcelable createdFromParcel = TradeItOrderParcelable.CREATOR.createFromParcel(parcel);
        TradeItLinkedBrokerAccountParcelable createdLinkedBrokerAccount = createdFromParcel.getLinkedBrokerAccount();
        TradeItOrderAction action = createdFromParcel.getAction();
        TradeItOrderExpiration expiration = createdFromParcel.getExpiration();
        Double limitPrice = createdFromParcel.getLimitPrice();
        TradeItOrderPriceType priceType = createdFromParcel.getPriceType();
        int quantity = createdFromParcel.getQuantity();
        Double stopPrice = createdFromParcel.getStopPrice();
        Double lastPrice = createdFromParcel.getQuoteLastPrice();
        String symbol = createdFromParcel.getSymbol();
        boolean userDisabledMargin = createdFromParcel.isUserDisabledMargin();

        // Verify that the received data is correct.
        assertThat(createdLinkedBrokerAccount, is(linkedBrokerAccount));
        assertThat(createdLinkedBrokerAccount.getTradeItApiClient(), notNullValue());
        assertThat(createdLinkedBrokerAccount.getTradeItApiClient().getSessionToken(), is("MySessionToken"));

        assertThat(action, is(order.getAction()));
        assertThat(expiration, is(order.getExpiration()));
        assertThat(limitPrice, is(order.getLimitPrice()));
        assertThat(priceType, is(order.getPriceType()));
        assertThat(quantity, is(order.getQuantity()));
        assertThat(stopPrice, is(order.getStopPrice()));
        assertThat(lastPrice, is(order.getQuoteLastPrice()));
        assertThat(symbol, is(order.getSymbol()));
        assertThat(userDisabledMargin, is(order.isUserDisabledMargin()));
    }
}
