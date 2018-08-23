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
import java.util.Date;
import java.util.List;

import it.trade.android.sdk.TradeItConfigurationBuilder;
import it.trade.android.sdk.TradeItSDK;
import it.trade.api.TradeItApiClient;
import it.trade.model.reponse.TradeItBrokerAccount;
import it.trade.model.reponse.TradeItOAuthAccessTokenResponse;
import it.trade.model.request.TradeItEnvironment;
import it.trade.model.request.TradeItLinkedLogin;
import it.trade.model.request.TradeItOAuthAccessTokenRequest;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;


@RunWith(AndroidJUnit4.class)
@SmallTest
public class TradeItLinkedBrokerParcelableTest {

    private TradeItLinkedBrokerParcelable linkedBroker;

    @Before
    public void createTradeItLinkedBroker() {
        Context instrumentationCtx = InstrumentationRegistry.getTargetContext();
        TradeItSDK.clearConfig();
        TradeItSDK.configure(new TradeItConfigurationBuilder(instrumentationCtx.getApplicationContext(), "tradeit-test-api-key", TradeItEnvironment.QA));

        TradeItOAuthAccessTokenRequest oAuthAccessTokenRequest = new TradeItOAuthAccessTokenRequest("MyApiKey","MyOauthVerifier");

        TradeItOAuthAccessTokenResponse oAuthAccessTokenResponse = new TradeItOAuthAccessTokenResponse();
        oAuthAccessTokenResponse.userId = "MyUserId";
        oAuthAccessTokenResponse.userToken = "MyUserToken";
        oAuthAccessTokenResponse.broker = "MyBroker";
        TradeItLinkedLoginParcelable linkedLogin = new TradeItLinkedLoginParcelable(oAuthAccessTokenRequest, oAuthAccessTokenResponse);
        TradeItApiClientParcelable apiClient = new TradeItApiClientParcelable("MyApiKey", TradeItSDK.getEnvironment());

        apiClient.setSessionToken("MySessionToken");
        linkedBroker = new TradeItLinkedBrokerParcelable(apiClient, linkedLogin, TradeItSDK.getLinkedBrokerCache());
    }

    @Test
    public void linkedBroker_ParcelableWriteRead() {
        // Set up the Parcelable object to send and receive.
        Date date = new Date();
        linkedBroker.setAccountsLastUpdated(date);
        List<TradeItLinkedBrokerAccountParcelable> accounts = new ArrayList<>();
        TradeItBrokerAccount account = new TradeItBrokerAccount();
        account.accountNumber = "MyAccountNumber";
        account.accountBaseCurrency = "MyAccountBaseCurrency";
        account.name = "MyAccountName";
        accounts.add(new TradeItLinkedBrokerAccountParcelable(linkedBroker, account));
        linkedBroker.setAccounts(accounts);

        // Write the data.
        Parcel parcel = Parcel.obtain();
        linkedBroker.writeToParcel(parcel, linkedBroker.describeContents());

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0);

        // Read the data.
        TradeItLinkedBrokerParcelable createdFromParcel = TradeItLinkedBrokerParcelable.CREATOR.createFromParcel(parcel);
        TradeItApiClient apiClient = createdFromParcel.getApiClient();
        TradeItLinkedLogin linkedLogin = createdFromParcel.getLinkedLogin();
        List<TradeItLinkedBrokerAccountParcelable> linkedBrokerAccounts = createdFromParcel.getAccounts();
        Date accountLastUpdated = createdFromParcel.getAccountsLastUpdated();
        // Verify that the received data is correct.
        assertThat(apiClient, notNullValue());

        assertThat(apiClient.getSessionToken(), is("MySessionToken"));

        assertThat(linkedLogin, notNullValue());
        assertThat(linkedLogin.userId, is("MyUserId"));
        assertThat(linkedLogin.userToken, is("MyUserToken"));

        assertThat(linkedBrokerAccounts, notNullValue());
        assertThat(linkedBrokerAccounts.size(), is(1));
        assertThat(linkedBrokerAccounts.get(0).getAccountName(), is("MyAccountName"));
        assertThat(linkedBrokerAccounts.get(0).getAccountBaseCurrency(), is("MyAccountBaseCurrency"));
        assertThat(linkedBrokerAccounts.get(0).getAccountNumber(), is("MyAccountNumber"));

        assertThat(accountLastUpdated, is(date));
    }
}
