package it.trade.android.sdk.model

import android.os.Parcel
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.test.suitebuilder.annotation.SmallTest
import it.trade.android.sdk.TradeItConfigurationBuilder
import it.trade.android.sdk.TradeItSDK
import it.trade.android.sdk.enums.TradeItOrderAction
import it.trade.android.sdk.enums.TradeItOrderExpirationType
import it.trade.android.sdk.enums.TradeItOrderPriceType
import it.trade.android.sdk.enums.TradeItOrderQuantityType
import it.trade.api.TradeItApiClient
import it.trade.model.reponse.TradeItBrokerAccount
import it.trade.model.reponse.TradeItOAuthAccessTokenResponse
import it.trade.model.request.TradeItEnvironment
import it.trade.model.request.TradeItOAuthAccessTokenRequest
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal

@RunWith(AndroidJUnit4::class)
@SmallTest
class TradeItCryptoOrderParcelableTest {
    private var order: TradeItCryptoOrderParcelable? = null
    private var linkedBrokerAccount: TradeItLinkedBrokerAccountParcelable? = null

    @Before
    fun createTradeItOrder() {
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
        val linkedBroker = TradeItLinkedBrokerParcelable(apiClient, linkedLogin, TradeItSDK.linkedBrokerCache!!)

        val account = TradeItBrokerAccount()
        account.accountNumber = "MyAccountNumber"
        account.accountBaseCurrency = "MyAccountBaseCurrency"
        account.name = "MyAccountName"

        linkedBrokerAccount = TradeItLinkedBrokerAccountParcelable(linkedBroker, account)

        order = TradeItCryptoOrderParcelable(linkedBrokerAccount!!, "MySymbol")
    }

    @Test
    fun order_ParcelableWriteRead() {
        // Set up the Parcelable object to send and receive.
        order!!.action = TradeItOrderAction.BUY
        order!!.expiration = TradeItOrderExpirationType.GOOD_FOR_DAY
        order!!.priceType = TradeItOrderPriceType.LIMIT
        order!!.limitPrice = BigDecimal(20.50)
        order!!.quantity = BigDecimal(3.14159)
//        order!!.quoteLastPrice = 21.50
        order!!.orderQuantityType = TradeItOrderQuantityType.QUOTE_CURRENCY
        // Write the data.
        val parcel = Parcel.obtain()
        order!!.writeToParcel(parcel, order!!.describeContents())

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0)

        // Read the data.
        val createdFromParcel = TradeItCryptoOrderParcelable.CREATOR.createFromParcel(parcel)
        val createdLinkedBrokerAccount = createdFromParcel.linkedBrokerAccount
        val action = createdFromParcel.action
        val expiration = createdFromParcel.expiration
        val limitPrice = createdFromParcel.limitPrice
        val priceType = createdFromParcel.priceType
        val quantity = createdFromParcel.quantity
        val stopPrice = createdFromParcel.stopPrice
//        val lastPrice = createdFromParcel.quoteLastPrice
        val symbol = createdFromParcel.symbol
        val quantityType = createdFromParcel.orderQuantityType

        // Verify that the received data is correct.
        assertThat(createdLinkedBrokerAccount, `is`<TradeItLinkedBrokerAccountParcelable>(linkedBrokerAccount))
        assertThat<TradeItApiClient>(createdLinkedBrokerAccount!!.tradeItApiClient, notNullValue())
        assertThat(createdLinkedBrokerAccount.tradeItApiClient!!.sessionToken, `is`("MySessionToken"))

        assertThat(action, `is`(order!!.action))
        assertThat(expiration, `is`(order!!.expiration))
        assertThat(limitPrice, `is`(order!!.limitPrice))
        assertThat(priceType, `is`(order!!.priceType))
        assertThat(quantity, `is`(order!!.quantity))
        assertThat(stopPrice, `is`(order!!.stopPrice))
//        assertThat(lastPrice, `is`(order!!.quoteLastPrice))
        assertThat(symbol, `is`(order!!.symbol))
        assertThat(quantityType, `is`(order!!.orderQuantityType))
    }
}
