package it.trade.android.sdk.manager

import android.content.Context
import android.content.SharedPreferences
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.test.suitebuilder.annotation.LargeTest
import android.util.Log

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

import it.trade.android.sdk.TradeItConfigurationBuilder
import it.trade.android.sdk.TradeItSDK
import it.trade.android.sdk.internal.TradeItKeystoreService
import it.trade.model.TradeItErrorResult
import it.trade.model.callback.TradeItCallback
import it.trade.model.reponse.TradeItAvailableBrokersResponse
import it.trade.model.request.TradeItEnvironment

import android.support.test.InstrumentationRegistry.getInstrumentation
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.hamcrest.Matchers.nullValue
import org.junit.Assert.assertThat

@RunWith(AndroidJUnit4::class)
@LargeTest
class TradeItLinkedBrokerManagerTest {

    private val lock = CountDownLatch(1)
    private var linkedBrokerManager: TradeItLinkedBrokerManager? = null
    private var instrumentationCtx: Context? = null

    @Before
    fun cleanSharedPrefs() {
        val sharedPreferences = getInstrumentation().targetContext.getSharedPreferences(TradeItKeystoreService.TRADE_IT_SHARED_PREFS_KEY, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.commit()

    }

    @Before
    fun createTradeItLinkedBrokerManager() {
        instrumentationCtx = InstrumentationRegistry.getTargetContext()
        TradeItSDK.clearConfig()
        TradeItSDK.configure(TradeItConfigurationBuilder(instrumentationCtx!!.applicationContext, "tradeit-test-api-key", TradeItEnvironment.QA))
        linkedBrokerManager = TradeItSDK.linkedBrokerManager
    }

    @Test
    @Throws(InterruptedException::class)
    fun getAvailableBrokers() {
        linkedBrokerManager!!.getAvailableBrokers(object : TradeItCallback<List<TradeItAvailableBrokersResponse.Broker>> {
            override fun onSuccess(brokerList: List<TradeItAvailableBrokersResponse.Broker>) {
                assertThat("The broker list is not empty", brokerList.isEmpty(), `is`(false))
                lock.countDown()
            }

            override fun onError(error: TradeItErrorResult) {
                assertThat("fails to get the broker list", error, nullValue())
                lock.countDown()
            }
        })
        val notExpired = lock.await(EXPIRED_TIME, TimeUnit.MILLISECONDS)
        assertThat("The call to getAvailableBrokers is not expired", notExpired, `is`(true))
    }

    @Test
    @Throws(InterruptedException::class)
    fun getOAuthLoginPopupUrlForMobile() {
        linkedBrokerManager!!.getOAuthLoginPopupUrl("Dummy", "myinternalappcallback", object : TradeItCallback<String> {

            override fun onSuccess(oAuthUrl: String) {
                assertThat("oAuthUrl is not null", oAuthUrl, notNullValue())
                lock.countDown()
            }

            override fun onError(error: TradeItErrorResult) {
                Log.e(this.javaClass.getName(), error.toString())
                assertThat("fails to get the Oauth login popup url", error, nullValue())
                lock.countDown()
            }
        })

        val notExpired = lock.await(EXPIRED_TIME, TimeUnit.MILLISECONDS)
        assertThat("The call to getOAuthLoginPopupUrl is not expired", notExpired, `is`(true))
    }

    companion object {
        private val EXPIRED_TIME = 10000L
    }
}
