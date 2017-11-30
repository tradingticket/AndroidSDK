package it.trade.android.sdk.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import it.trade.android.sdk.TradeItConfigurationBuilder;
import it.trade.android.sdk.TradeItSDK;
import it.trade.android.sdk.internal.TradeItKeystoreService;
import it.trade.model.TradeItErrorResult;
import it.trade.model.callback.TradeItCallback;
import it.trade.model.reponse.TradeItAvailableBrokersResponse;
import it.trade.model.request.TradeItEnvironment;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TradeItLinkedBrokerManagerTest {

    private CountDownLatch lock = new CountDownLatch(1);
    private TradeItLinkedBrokerManager linkedBrokerManager;
    private Context instrumentationCtx;
    private static long EXPIRED_TIME = 10000l;

    @Before
    public void cleanSharedPrefs() {
        SharedPreferences sharedPreferences =
                getInstrumentation().getTargetContext().getSharedPreferences(TradeItKeystoreService.TRADE_IT_SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();

    }

    @Before
    public void createTradeItLinkedBrokerManager() {
        instrumentationCtx = InstrumentationRegistry.getTargetContext();
        TradeItSDK.clearConfig();
        TradeItSDK.configure(new TradeItConfigurationBuilder(instrumentationCtx.getApplicationContext(), "tradeit-test-api-key", TradeItEnvironment.QA));
        linkedBrokerManager = TradeItSDK.getLinkedBrokerManager();
    }

    @Test
    public void getAvailableBrokers() throws InterruptedException {
        linkedBrokerManager.getAvailableBrokers(new TradeItCallback<List<TradeItAvailableBrokersResponse.Broker>>() {
            @Override
            public void onSuccess(List<TradeItAvailableBrokersResponse.Broker> brokerList) {
                assertThat("The broker list is not empty", brokerList.isEmpty(), is(false));
                lock.countDown();
            }

            @Override
            public void onError(TradeItErrorResult error) {
                assertThat("fails to get the broker list", error, nullValue());
                lock.countDown();
            }
        });
        boolean notExpired = lock.await(EXPIRED_TIME, TimeUnit.MILLISECONDS);
        assertThat("The call to getAvailableBrokers is not expired", notExpired, is(true));
    }

//    @Test TODO This should be tested in the UI tests if possible
//    public void testCacheIsCorrect() throws InterruptedException {
//        linkBrokerOldMethodAndAuthenticationAndRefreshBalanceAndPositions();
//        List<TradeItLinkedBrokerParcelable> linkedBrokers = linkedBrokerManager.getLinkedBrokers();
//        List<TradeItLinkedBrokerAccountParcelable> accounts = linkedBrokers.get(0).getAccounts();
//        TradeItLinkedBrokerAccountParcelable account = accounts.get(0);
//
//        //reset to reload from cache
//        createTradeItLinkedBrokerManager();
//
//        List<TradeItLinkedBrokerParcelable> linkedBrokerParcelables = linkedBrokerManager.getLinkedBrokers();
//        assertThat("The number of linked brokers loaded from cache is correct", linkedBrokerParcelables.size() , is(linkedBrokers.size()));
//
//        TradeItLinkedBrokerParcelable linkedBrokerParcelable = linkedBrokerParcelables.get(0);
//        assertTrue("The error is set to session expired", linkedBrokerParcelable.isUnauthenticated());
//        assertThat("There number of linked broker accounts loaded from cache is correct", linkedBrokerParcelable.getAccounts().size() , is(accounts.size()));
//
//        TradeItLinkedBrokerAccountParcelable linkedBrokerAccountParcelable = linkedBrokerParcelable.getAccounts().get(0);
//        assertThat("The balance loaded from cache is correct", linkedBrokerAccountParcelable.getBalance() , is(account.getBalance()));
//        assertThat("The fx balance loaded from cache is correct", linkedBrokerAccountParcelable.getFxBalance() , is(account.getFxBalance()));
//    }

    @Test
    public void getOAuthLoginPopupUrlForMobile() throws InterruptedException {
        linkedBrokerManager.getOAuthLoginPopupUrl("Dummy", "myinternalappcallback", new TradeItCallback<String>() {

            @Override
            public void onSuccess(String oAuthUrl) {
                assertThat("oAuthUrl is not null", oAuthUrl , notNullValue());
                lock.countDown();
            }

            @Override
            public void onError(TradeItErrorResult error) {
                Log.e(this.getClass().getName(), error.toString());
                assertThat("fails to get the Oauth login popup url", error, nullValue());
                lock.countDown();
            }
        });

        boolean notExpired = lock.await(EXPIRED_TIME, TimeUnit.MILLISECONDS);
        assertThat("The call to getOAuthLoginPopupUrl is not expired", notExpired, is(true));
    }
}
