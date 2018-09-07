package it.trade.android.sdk.model

import android.os.Parcel
import android.support.test.InstrumentationRegistry
import android.support.test.filters.SmallTest
import android.support.test.runner.AndroidJUnit4
import it.trade.android.sdk.TradeItConfigurationBuilder
import it.trade.android.sdk.TradeItSDK
import it.trade.model.reponse.TradeItBrokerAccount
import it.trade.model.reponse.TradeItOAuthAccessTokenResponse
import it.trade.model.request.TradeItEnvironment
import it.trade.model.request.TradeItLinkedLogin
import it.trade.model.request.TradeItOAuthAccessTokenRequest
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*


@RunWith(AndroidJUnit4::class)
@SmallTest
class TradeItLinkedBrokerParcelableTest {

    private var linkedBroker: TradeItLinkedBrokerParcelable? = null

    @Before
    fun createTradeItLinkedBroker() {
        val instrumentationCtx = InstrumentationRegistry.getTargetContext()
        TradeItSDK.clearConfig()
        TradeItSDK.configure(TradeItConfigurationBuilder(instrumentationCtx.applicationContext, "tradeit-test-api-key", TradeItEnvironment.QA))

        val oAuthAccessTokenRequest = TradeItOAuthAccessTokenRequest(
            "MyApiKey",
            "MyOauthVerifier"
        )
        val oAuthAccessTokenResponse = TradeItOAuthAccessTokenResponse()
        oAuthAccessTokenResponse.userId = "MyUserId"
        oAuthAccessTokenResponse.userToken = "MyUserToken"
        oAuthAccessTokenResponse.broker = "MyBroker"
        val linkedLogin = TradeItLinkedLoginParcelable(oAuthAccessTokenRequest, oAuthAccessTokenResponse)
        val apiClient = TradeItApiClientParcelable("MyApiKey", TradeItSDK.environment!!)

        apiClient.sessionToken = "MySessionToken"
        linkedBroker = TradeItLinkedBrokerParcelable(apiClient, linkedLogin, TradeItSDK.linkedBrokerCache!!)
    }

    @Test
    fun linkedBroker_ParcelableWriteRead() {
        // Set up the Parcelable object to send and receive.
        val date = Date()
        linkedBroker!!.accountsLastUpdated = date
        val accounts = ArrayList<TradeItLinkedBrokerAccountParcelable>()
        val account = TradeItBrokerAccount()
        account.accountNumber = "MyAccountNumber"
        account.accountBaseCurrency = "MyAccountBaseCurrency"
        account.name = "MyAccountName"
        accounts.add(TradeItLinkedBrokerAccountParcelable(linkedBroker!!, account))
        linkedBroker!!.accounts = accounts

        // Write the data.
        val parcel = Parcel.obtain()
        linkedBroker!!.writeToParcel(parcel, linkedBroker!!.describeContents())

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0)

        // Read the data.
        val createdFromParcel = TradeItLinkedBrokerParcelable.CREATOR.createFromParcel(parcel)
        val apiClient = createdFromParcel.apiClient
        val linkedLogin = createdFromParcel.linkedLogin
        val linkedBrokerAccounts = createdFromParcel.accounts
        val accountLastUpdated = createdFromParcel.accountsLastUpdated
        // Verify that the received data is correct.
        assertThat(apiClient, notNullValue())

        assertThat(apiClient!!.sessionToken, `is`("MySessionToken"))

        assertThat<TradeItLinkedLogin>(linkedLogin, notNullValue())
        assertThat(linkedLogin!!.userId, `is`("MyUserId"))
        assertThat(linkedLogin.userToken, `is`("MyUserToken"))

        assertThat(linkedBrokerAccounts, notNullValue())
        assertThat(linkedBrokerAccounts.size, `is`(1))
        assertThat(linkedBrokerAccounts[0].accountName, `is`("MyAccountName"))
        assertThat(linkedBrokerAccounts[0].accountBaseCurrency, `is`("MyAccountBaseCurrency"))
        assertThat(linkedBrokerAccounts[0].accountNumber, `is`("MyAccountNumber"))

        assertThat(accountLastUpdated, `is`(date))
    }
}
