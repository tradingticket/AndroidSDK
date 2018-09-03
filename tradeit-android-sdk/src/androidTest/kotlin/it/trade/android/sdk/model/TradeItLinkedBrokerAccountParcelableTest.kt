package it.trade.android.sdk.model

import android.os.Parcel
import android.support.test.InstrumentationRegistry
import android.support.test.filters.SmallTest
import android.support.test.runner.AndroidJUnit4
import it.trade.android.sdk.TradeItConfigurationBuilder
import it.trade.android.sdk.TradeItSDK
import it.trade.model.reponse.*
import it.trade.model.request.TradeItEnvironment
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
class TradeItLinkedBrokerAccountParcelableTest {

    private var linkedBrokerAccount: TradeItLinkedBrokerAccountParcelable? = null

    @Before
    fun createTradeItLinkedBrokerAccount() {
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

        apiClient.sessionToken = "MyToken"
        val linkedBroker = TradeItLinkedBrokerParcelable(apiClient, linkedLogin, TradeItSDK.linkedBrokerCache!!)

        val account = TradeItBrokerAccount()
        account.accountNumber = "MyAccountNumber"
        account.accountBaseCurrency = "MyAccountBaseCurrency"
        account.name = "MyAccountName"
        account.userCanDisableMargin = true

        val orderCapability = OrderCapability()
        val action = DisplayLabelValue("Buy", "buy")
        orderCapability.actions = Arrays.asList(action)
        orderCapability.instrument = Instrument.EQUITIES
        account.orderCapabilities = Arrays.asList(orderCapability)

        linkedBrokerAccount = TradeItLinkedBrokerAccountParcelable(linkedBroker, account)
    }

    @Test
    fun linkedBrokerAccount_ParcelableWriteRead() {
        // Set up the Parcelable object to send and receive.
        val position = TradeItPositionParcelable()
        position.quantity = 12.00
        position.symbol = "GE"
        position.lastPrice = 29.84
        val positions = ArrayList<TradeItPositionParcelable>()
        positions.add(position)
        linkedBrokerAccount!!.positions = positions

        val balance = TradeItBalanceParcelable()
        balance.availableCash = 1200.54
        balance.buyingPower = 2604.45
        balance.dayAbsoluteReturn = 100.00
        balance.dayPercentReturn = 0.45
        balance.totalAbsoluteReturn = -234.98
        balance.totalPercentReturn = -2.34
        balance.totalValue = 12983.34
        linkedBrokerAccount!!.balance = balance

        // Write the data.
        val parcel = Parcel.obtain()
        linkedBrokerAccount!!.writeToParcel(parcel, linkedBrokerAccount!!.describeContents())

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0)

        // Read the data.
        val createdFromParcel = TradeItLinkedBrokerAccountParcelable.CREATOR.createFromParcel(parcel)
        val apiClient = createdFromParcel.tradeItApiClient
        val accountBaseCurrency = createdFromParcel.accountBaseCurrency
        val accountName = createdFromParcel.accountName
        val accountNumber = createdFromParcel.accountNumber
        val userCanDisableMargin = createdFromParcel.userCanDisableMargin
        val createdBalance = createdFromParcel.balance
        val createdPositions = createdFromParcel.positions
        val orderCapabilities = createdFromParcel.orderCapabilities

        val actionParcelable = orderCapabilities[0].actions[0]

        // Verify that the received data is correct.
        assertThat(apiClient, notNullValue())
        assertThat(apiClient!!.sessionToken, `is`("MyToken"))

        assertThat(accountBaseCurrency, `is`("MyAccountBaseCurrency"))
        assertThat(accountName, `is`("MyAccountName"))
        assertThat(accountNumber, `is`("MyAccountNumber"))
        assertThat(userCanDisableMargin, `is`(linkedBrokerAccount!!.userCanDisableMargin))

        assertThat(createdBalance, `is`(balance))
        assertThat(createdPositions, `is`<List<TradeItPositionParcelable>>(positions))
        assertThat(orderCapabilities.isEmpty(), `is`(false))
        assertThat(actionParcelable.action.actionValue, `is`("buy"))
        assertThat(actionParcelable.displayLabel, `is`("Buy"))
    }
}
