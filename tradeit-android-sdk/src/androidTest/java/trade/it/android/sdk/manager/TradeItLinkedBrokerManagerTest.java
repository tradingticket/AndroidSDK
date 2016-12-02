package trade.it.android.sdk.manager;

import android.content.Context;
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

import it.trade.tradeitapi.model.TradeItAvailableBrokersResponse;
import it.trade.tradeitapi.model.TradeItEnvironment;
import it.trade.tradeitapi.model.TradeItLinkedAccount;
import trade.it.android.sdk.model.TradeItCallBackImpl;
import trade.it.android.sdk.model.TradeItErrorResult;

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

    @Before
    public void createTradeItLinkedBrokerManager() {
        linkedBrokerManager = new TradeItLinkedBrokerManager("tradeit-test-api-key", TradeItEnvironment.QA);
        instrumentationCtx = InstrumentationRegistry.getContext();
    }

    @Test
    public void getAvailableBrokers() throws InterruptedException {
        linkedBrokerManager.getAvailableBrokers(new TradeItCallBackImpl<List<TradeItAvailableBrokersResponse.Broker>>() {
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
        boolean notExpired = lock.await(5000, TimeUnit.MILLISECONDS);
        assertThat("The call to getAvailableBrokers is not expired", notExpired, is(true));
    }

    @Test
    public void linkBrokerOldMethod() throws InterruptedException {
        linkedBrokerManager.linkBroker(instrumentationCtx, "My accountLabel 1", "Dummy", "dummy", "dummy",  new TradeItCallBackImpl<TradeItLinkedAccount>() {
            @Override
            public void onSuccess(TradeItLinkedAccount linkedAccount) {
                assertThat("The linkedAccount userId is not null", linkedAccount.userId , notNullValue());
                assertThat("The linkedAccount userToken is not null", linkedAccount.userId , notNullValue());
                lock.countDown();
            }

            @Override
            public void onError(TradeItErrorResult error) {
                Log.e(this.getClass().getName(), error.toString());
                assertThat("fails to link broker", error, nullValue());
                lock.countDown();
            }
        });
        boolean notExpired = lock.await(5000, TimeUnit.MILLISECONDS);
        assertThat("The call to linkBroker is not expired", notExpired, is(true));
    }

    @Test
    public void getOAuthLoginPopupUrlForMobile() throws InterruptedException {
        linkedBrokerManager.getOAuthLoginPopupUrlForMobile("Dummy", "myinternalappcallback", new TradeItCallBackImpl<String>() {

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

        boolean notExpired = lock.await(5000, TimeUnit.MILLISECONDS);
        assertThat("The call to getOAuthLoginPopupUrlForMobile is not expired", notExpired, is(true));
    }
}
