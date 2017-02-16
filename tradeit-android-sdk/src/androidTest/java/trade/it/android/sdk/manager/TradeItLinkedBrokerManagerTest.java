package trade.it.android.sdk.manager;

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

import it.trade.tradeitapi.API.TradeItAccountLinker;
import it.trade.tradeitapi.exception.TradeItKeystoreServiceCreateKeyException;
import it.trade.tradeitapi.exception.TradeItRetrieveLinkedAccountException;
import it.trade.tradeitapi.model.TradeItAvailableBrokersResponse;
import it.trade.tradeitapi.model.TradeItEnvironment;
import it.trade.tradeitapi.model.TradeItGetAccountOverviewResponse;
import it.trade.tradeitapi.model.TradeItGetPositionsResponse;
import it.trade.tradeitapi.model.TradeItPlaceStockOrEtfOrderResponse;
import it.trade.tradeitapi.model.TradeItPreviewStockOrEtfOrderResponse;
import it.trade.tradeitapi.model.TradeItResponse;
import it.trade.tradeitapi.model.TradeItResponseStatus;
import trade.it.android.sdk.model.TradeItCallBackImpl;
import trade.it.android.sdk.model.TradeItCallbackWithSecurityQuestionImpl;
import trade.it.android.sdk.model.TradeItErrorResult;
import trade.it.android.sdk.model.TradeItLinkedBroker;
import trade.it.android.sdk.model.TradeItLinkedBrokerAccount;
import trade.it.android.sdk.model.TradeItOrder;
import trade.it.android.sdk.model.TradeItSecurityQuestion;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TradeItLinkedBrokerManagerTest {

    private CountDownLatch lock = new CountDownLatch(1);
    private TradeItLinkedBrokerManager linkedBrokerManager;
    private Context instrumentationCtx;


    @Before
    public void createTradeItLinkedBrokerManager() throws TradeItKeystoreServiceCreateKeyException, TradeItRetrieveLinkedAccountException {
        instrumentationCtx = InstrumentationRegistry.getTargetContext();
        linkedBrokerManager = new TradeItLinkedBrokerManager(instrumentationCtx.getApplicationContext(), new TradeItAccountLinker("tradeit-test-api-key", TradeItEnvironment.QA));
    }

    @Before
    public void cleanSharedPrefs() {
        SharedPreferences sharedPreferences =
                getInstrumentation().getTargetContext().getSharedPreferences(TradeItAccountLinker.TRADE_IT_SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
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
    public void linkBrokerOldMethodAndAuthenticationAndRefreshBalanceAndPositions() throws InterruptedException {
        linkedBrokerManager.linkBroker("My accountLabel 1", "Dummy", "dummy", "dummy",  new TradeItCallBackImpl<TradeItLinkedBroker>() {
            @Override
            public void onSuccess(final TradeItLinkedBroker linkedBroker) {
                assertThat("The linkedAccount userId is not null", linkedBroker.getLinkedAccount().userId , notNullValue());
                assertThat("The linkedAccount userToken is not null", linkedBroker.getLinkedAccount().userId , notNullValue());
                linkedBroker.authenticate(new TradeItCallbackWithSecurityQuestionImpl<List<TradeItLinkedBrokerAccount>>() {

                    @Override
                    public void onSuccess(final List<TradeItLinkedBrokerAccount> accounts) {
                        assertThat("The authentication is successful", !accounts.isEmpty(), is(true));
                        accounts.get(0).refreshBalance(new TradeItCallBackImpl<TradeItGetAccountOverviewResponse>() {
                            @Override
                            public void onSuccess(TradeItGetAccountOverviewResponse balance) {
                                assertThat("refreshBalance returns available cash", balance.availableCash, notNullValue());
                                accounts.get(0).refreshPositions(new TradeItCallBackImpl<List<TradeItGetPositionsResponse.Position>>() {
                                    @Override
                                    public void onSuccess(List<TradeItGetPositionsResponse.Position> positions) {
                                        assertThat("refresh positions is successful", !positions.isEmpty(), is(true));
                                        lock.countDown();
                                    }

                                    @Override
                                    public void onError(TradeItErrorResult error) {
                                        Log.e(this.getClass().getName(), error.toString());
                                        assertThat("fails to refresh positions", error, nullValue());
                                        lock.countDown();
                                    }
                                });
                            }

                            @Override
                            public void onError(TradeItErrorResult error) {
                                Log.e(this.getClass().getName(), error.toString());
                                assertThat("fails to refresh balances", error, nullValue());
                                lock.countDown();
                            }
                        });
                    }

                    @Override
                    public void onSecurityQuestion(TradeItSecurityQuestion securityQuestion) {
                        Log.e(this.getClass().getName(), securityQuestion.toString());
                        assertThat("fails to authenticate",  securityQuestion, nullValue());
                        lock.countDown();
                    }

                    @Override
                    public void onError(TradeItErrorResult error) {
                        Log.e(this.getClass().getName(), error.toString());
                        assertThat("fails to authenticate",  error, nullValue());
                        lock.countDown();
                    }
                });
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
    public void linkBrokerOldMethodAndAuthenticationAndTrade() throws InterruptedException {
        linkedBrokerManager.linkBroker("My accountLabel 1", "Dummy", "dummy", "dummy",  new TradeItCallBackImpl<TradeItLinkedBroker>() {
            @Override
            public void onSuccess(final TradeItLinkedBroker linkedBroker) {
                assertThat("The linkedAccount userId is not null", linkedBroker.getLinkedAccount().userId , notNullValue());
                assertThat("The linkedAccount userToken is not null", linkedBroker.getLinkedAccount().userId , notNullValue());
                linkedBroker.authenticate(new TradeItCallbackWithSecurityQuestionImpl<List<TradeItLinkedBrokerAccount>>() {
                    @Override
                    public void onSuccess(List<TradeItLinkedBrokerAccount> accounts) {
                        assertThat("The authentication is successful",  !accounts.isEmpty(), is(true));

                        final TradeItOrder order = new TradeItOrder(accounts.get(0), "GE");
                        order.previewOrder(new TradeItCallBackImpl<TradeItPreviewStockOrEtfOrderResponse>() {
                            @Override
                            public void onSuccess(TradeItPreviewStockOrEtfOrderResponse response) {
                                assertThat("Preview order is successful",  response.orderId, notNullValue());
                                order.placeOrder(response.orderId, new TradeItCallBackImpl<TradeItPlaceStockOrEtfOrderResponse>() {
                                    @Override
                                    public void onSuccess(TradeItPlaceStockOrEtfOrderResponse placeOrderResponse) {
                                        assertEquals("Place order is successful",  placeOrderResponse.status, TradeItResponseStatus.SUCCESS);
                                        lock.countDown();
                                    }

                                    @Override
                                    public void onError(TradeItErrorResult error) {
                                        assertThat("fails to place order", error, nullValue());
                                        lock.countDown();
                                    }
                                });
                            }

                            @Override
                            public void onError(TradeItErrorResult error) {
                                Log.e(this.getClass().getName(), error.toString());
                                assertThat("fails to call preview order",  error, nullValue());
                                lock.countDown();
                            }
                        });
                    }

                    @Override
                    public void onSecurityQuestion(TradeItSecurityQuestion securityQuestion) {
                        Log.e(this.getClass().getName(), securityQuestion.toString());
                        assertThat("fails to authenticate",  securityQuestion, nullValue());
                        lock.countDown();
                    }

                    @Override
                    public void onError(TradeItErrorResult error) {
                        Log.e(this.getClass().getName(), error.toString());
                        assertThat("fails to authenticate",  error, nullValue());
                        lock.countDown();
                    }
                });
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
    public void linkBrokerOldMethodAndSecurityQuestion() throws InterruptedException {
        linkedBrokerManager.linkBroker("My accountLabel 1", "Dummy", "dummySecurity", "dummy",  new TradeItCallBackImpl<TradeItLinkedBroker>() {
            @Override
            public void onSuccess(final TradeItLinkedBroker linkedBroker) {
                assertThat("The linkedAccount userId is not null", linkedBroker.getLinkedAccount().userId , notNullValue());
                assertThat("The linkedAccount userToken is not null", linkedBroker.getLinkedAccount().userToken , notNullValue());
                linkedBroker.authenticate(new TradeItCallbackWithSecurityQuestionImpl<List<TradeItLinkedBrokerAccount>>() {
                    @Override
                    public void onSuccess(List<TradeItLinkedBrokerAccount> accounts) {
                        assertThat("successful authentication after answering security question security question",  accounts, notNullValue());
                        lock.countDown();
                    }

                    @Override
                    public void onSecurityQuestion(TradeItSecurityQuestion securityQuestion) {
                        assertThat("security question is not null",  securityQuestion, notNullValue());
                        this.submitSecurityAnswer("tradingticket");
                    }

                    @Override
                    public void onError(TradeItErrorResult error) {
                        assertThat("fails to get security question",  error, nullValue());
                        lock.countDown();
                    }
                });
            }

            @Override
            public void onError(TradeItErrorResult error) {
                Log.e(this.getClass().getName(), error.toString());
                assertThat("fails to linkBroker", error, nullValue());
                lock.countDown();
            }
        });

        boolean notExpired = lock.await(5000, TimeUnit.MILLISECONDS);
        assertThat("The call to linkBrokerOldMethodAndSecurityQuestion is not expired", notExpired, is(true));
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

    @Test
    public void linkAndUnlinkBrokers() throws InterruptedException {
        linkedBrokerManager.linkBroker("My accountLabel 1", "Dummy", "dummy", "dummy",  new TradeItCallBackImpl<TradeItLinkedBroker>() {
            @Override
            public void onSuccess(final TradeItLinkedBroker linkedBroker) {
                List<TradeItLinkedBroker> linkedBrokers = linkedBrokerManager.getLinkedBrokers();
                assertThat("we linked one broker", linkedBrokers.size(), is(1));

                linkedBrokerManager.unlinkBroker(linkedBroker, new TradeItCallBackImpl<TradeItResponse>() {
                    @Override
                    public void onSuccess(TradeItResponse response) {
                        assertThat("unlink broker successfully", response, notNullValue());
                        if (linkedBrokerManager.getLinkedBrokers().size() == 0) {
                            lock.countDown();
                        }
                    }
                    @Override
                    public void onError(TradeItErrorResult error) {
                        Log.e(this.getClass().getName(), error.toString());
                        assertThat("fails to unlink broker", error, nullValue());
                        lock.countDown();
                    }
                });
            }

            @Override
            public void onError(TradeItErrorResult error) {
                Log.e(this.getClass().getName(), error.toString());
                assertThat("fails to link broker", error, nullValue());
                lock.countDown();
            }
        });

        boolean notExpired = lock.await(10000, TimeUnit.MILLISECONDS);
        assertThat("The call to linkAndUnlinkBrokers is not expired", notExpired, is(true));
    }

    @Test
    public void linkBrokerAndGetOAuthLoginPopupForTokenUpdateUrl() throws InterruptedException {
        linkedBrokerManager.linkBroker("My accountLabel 1", "Dummy", "dummy", "dummy",  new TradeItCallBackImpl<TradeItLinkedBroker>() {
            @Override
            public void onSuccess(final TradeItLinkedBroker linkedBroker) {
                String userId = linkedBroker.getLinkedAccount().userId;
                assertThat("we linked one broker", userId, notNullValue());

                linkedBrokerManager.getOAuthLoginPopupForTokenUpdateUrl("Dummy", userId, "myinternalappcallback", new TradeItCallBackImpl<String>() {

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
            }

            @Override
            public void onError(TradeItErrorResult error) {
                Log.e(this.getClass().getName(), error.toString());
                assertThat("fails to link broker", error, nullValue());
                lock.countDown();
            }
        });

        boolean notExpired = lock.await(5000, TimeUnit.MILLISECONDS);
        assertThat("The call to linkBrokerAndGetOAuthLoginPopupForTokenUpdateUrl is not expired", notExpired, is(true));
    }
}
