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

import it.trade.android.sdk.TradeItConfigurationBuilder;
import it.trade.android.sdk.TradeItSDK;
import it.trade.api.TradeItApiClient;
import it.trade.model.reponse.TradeItBrokerAccount;
import it.trade.model.reponse.TradeItOAuthAccessTokenResponse;
import it.trade.model.request.TradeItEnvironment;
import it.trade.model.request.TradeItOAuthAccessTokenRequest;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class TradeItLinkedBrokerAccountParcelableTest {

    private TradeItLinkedBrokerAccountParcelable linkedBrokerAccount;

    @Before
    public void createTradeItLinkedBrokerAccount() {
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

        apiClient.setSessionToken("MyToken");
        TradeItLinkedBrokerParcelable linkedBroker = new TradeItLinkedBrokerParcelable(apiClient, linkedLogin, TradeItSDK.getLinkedBrokerCache());

        TradeItBrokerAccount account = new TradeItBrokerAccount();
        account.accountNumber = "MyAccountNumber";
        account.accountBaseCurrency = "MyAccountBaseCurrency";
        account.name = "MyAccountName";
        account.userCanDisableMargin = true;
        linkedBrokerAccount = new TradeItLinkedBrokerAccountParcelable(linkedBroker, account);
    }

    @Test
    public void linkedBrokerAccount_ParcelableWriteRead() {
        // Set up the Parcelable object to send and receive.
        TradeItPositionParcelable position = new TradeItPositionParcelable();
        position.quantity = 12.00;
        position.symbol = "GE";
        position.lastPrice = 29.84;
        List<TradeItPositionParcelable> positions = new ArrayList<>();
        positions.add(position);
        linkedBrokerAccount.setPositions(positions);

        TradeItBalanceParcelable balance = new TradeItBalanceParcelable();
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
        TradeItLinkedBrokerAccountParcelable createdFromParcel = TradeItLinkedBrokerAccountParcelable.CREATOR.createFromParcel(parcel);
        TradeItApiClient apiClient = createdFromParcel.getTradeItApiClient();
        String accountBaseCurrency = createdFromParcel.getAccountBaseCurrency();
        String accountName = createdFromParcel.getAccountName();
        String accountNumber = createdFromParcel.getAccountNumber();
        boolean userCanDisableMargin = createdFromParcel.userCanDisableMargin;
        TradeItBalanceParcelable createdBalance = createdFromParcel.getBalance();
        List<TradeItPositionParcelable> createdPositions = createdFromParcel.getPositions();

        // Verify that the received data is correct.
        assertThat(apiClient, notNullValue());
        assertThat(apiClient.getSessionToken(), is("MyToken"));

        assertThat(accountBaseCurrency, is("MyAccountBaseCurrency"));
        assertThat(accountName, is("MyAccountName"));
        assertThat(accountNumber, is("MyAccountNumber"));
        assertThat(userCanDisableMargin, is(linkedBrokerAccount.userCanDisableMargin));

        assertThat(createdBalance, is(balance));
        assertThat(createdPositions, is(positions));
    }
}
