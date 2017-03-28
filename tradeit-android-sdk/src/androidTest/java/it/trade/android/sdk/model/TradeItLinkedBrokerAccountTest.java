package it.trade.android.sdk.model;

import android.content.Context;
import android.os.Parcel;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import it.trade.android.sdk.TradeItSDK;
import it.trade.tradeitapi.API.TradeItApiClient;
import it.trade.tradeitapi.model.TradeItBrokerAccount;
import it.trade.tradeitapi.model.TradeItEnvironment;
import it.trade.tradeitapi.model.TradeItGetAccountOverviewResponse;
import it.trade.tradeitapi.model.TradeItLinkedLogin;
import it.trade.tradeitapi.model.TradeItOAuthAccessTokenRequest;
import it.trade.tradeitapi.model.TradeItOAuthAccessTokenResponse;
import it.trade.tradeitapi.model.TradeItPosition;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class TradeItLinkedBrokerAccountTest {

    private TradeItLinkedBrokerAccount linkedBrokerAccount;

    @Before
    public void createTradeItLinkedBrokerAccount() {
        Context instrumentationCtx = InstrumentationRegistry.getTargetContext();
        TradeItSDK.clearConfig();
        TradeItSDK.configure(instrumentationCtx.getApplicationContext(), "tradeit-test-api-key", TradeItEnvironment.QA);

        TradeItOAuthAccessTokenRequest oAuthAccessTokenRequest = new TradeItOAuthAccessTokenRequest("MyOauthVerifier");
        oAuthAccessTokenRequest.apiKey = "MyApiKey";
        TradeItOAuthAccessTokenResponse oAuthAccessTokenResponse = new TradeItOAuthAccessTokenResponse();
        oAuthAccessTokenResponse.userId = "MyUserId";
        oAuthAccessTokenResponse.userToken = "MyUserToken";
        oAuthAccessTokenResponse.broker = "MyBroker";
        TradeItApiClient apiClient = new TradeItApiClient(new TradeItLinkedLogin(oAuthAccessTokenRequest, oAuthAccessTokenResponse), TradeItSDK.getEnvironment());
        apiClient.setSessionToken("MyToken");
        TradeItLinkedBroker linkedBroker = new TradeItLinkedBroker(apiClient);

        TradeItBrokerAccount account = new TradeItBrokerAccount();
        account.accountNumber = "MyAccountnumber";
        account.accountBaseCurrency = "MyAccountBaseCurrency";
        account.name = "MyAccountname";

        linkedBrokerAccount = new TradeItLinkedBrokerAccount(linkedBroker, account);
    }

    @Test
    public void linkedBrokerAccount_ParcelableWriteRead() {
        // Set up the Parcelable object to send and receive.
        TradeItPosition position = new TradeItPosition();
        position.quantity = 12.00;
        position.symbol = "GE";
        position.lastPrice = 29.84;
        List<TradeItPosition> positions = new ArrayList<>();
        positions.add(position);
        linkedBrokerAccount.setPositions(positions);

        TradeItGetAccountOverviewResponse balance = new TradeItGetAccountOverviewResponse();
        balance.availableCash = 1200.54;
        balance.buyingPower = 2604.45;
        balance.dayAbsoluteReturn = 100.00;
        balance.dayPercentReturn = 0.45;
        balance.totalAbsoluteReturn = -234.98;
        balance.totalPercentReturn = -2.34;
        balance.totalValue = 12983.34;
        linkedBrokerAccount.setBalance(balance);

        // Write the data.
        Parcel parcel = Parcel.obtain();
        linkedBrokerAccount.writeToParcel(parcel, linkedBrokerAccount.describeContents());

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0);

        // Read the data.
        TradeItLinkedBrokerAccount createdFromParcel = TradeItLinkedBrokerAccount.CREATOR.createFromParcel(parcel);
        TradeItApiClient apiClient = createdFromParcel.getTradeItApiClient();
        String accountBaseCurrency = createdFromParcel.getAccountBaseCurrency();
        String accountName = createdFromParcel.getAccountName();
        String accountNumber = createdFromParcel.getAccountNumber();
        TradeItGetAccountOverviewResponse createdBalance = createdFromParcel.getBalance();
        List<TradeItPosition> createdPositions = createdFromParcel.getPositions();

        // Verify that the received data is correct.
        assertThat(apiClient, notNullValue());
        assertThat(apiClient.getSessionToken(), is("MyToken"));

        assertThat(accountBaseCurrency, is("MyAccountBaseCurrency"));
        assertThat(accountName, is("MyAccountname"));
        assertThat(accountNumber, is("MyAccountnumber"));

        assertThat(createdBalance, is(balance));
        assertThat(createdPositions, is(positions));
    }
}
