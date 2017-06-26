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
import it.trade.android.sdk.model.TradeItLinkedBrokerAccountParcelable;
import it.trade.android.sdk.model.TradeItLinkedBrokerParcelable;
import it.trade.android.sdk.model.TradeItOrderParcelable;
import it.trade.android.sdk.model.TradeItPositionParcelable;
import it.trade.model.TradeItErrorResult;
import it.trade.model.TradeItSecurityQuestion;
import it.trade.model.callback.TradeItCallback;
import it.trade.model.callback.TradeItCallbackWithSecurityQuestionImpl;
import it.trade.model.reponse.TradeItAvailableBrokersResponse;
import it.trade.model.reponse.TradeItPlaceStockOrEtfOrderResponse;
import it.trade.model.reponse.TradeItPreviewStockOrEtfOrderResponse;
import it.trade.model.reponse.TradeItResponse;
import it.trade.model.reponse.TradeItResponseStatus;
import it.trade.model.request.TradeItEnvironment;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static junit.framework.Assert.assertEquals;
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

    @Test
    public void linkBrokerOldMethodAndAuthenticationAndRefreshBalanceAndPositions() throws InterruptedException {
        linkedBrokerManager.linkBroker("My accountLabel 1", "Dummy", "dummy", "dummy",  new TradeItCallback<TradeItLinkedBrokerParcelable>() {
            @Override
            public void onSuccess(final TradeItLinkedBrokerParcelable linkedBroker) {
                assertThat("The linkedLogin userId is not null", linkedBroker.getLinkedLogin().userId , notNullValue());
                assertThat("The linkedLogin userToken is not null", linkedBroker.getLinkedLogin().userId , notNullValue());
                linkedBroker.authenticateIfNeeded(new TradeItCallbackWithSecurityQuestionImpl<List<TradeItLinkedBrokerAccountParcelable>>() {

                    @Override
                    public void onSuccess(final List<TradeItLinkedBrokerAccountParcelable> accounts) {
                        assertThat("The authentication is successful", !accounts.isEmpty(), is(true));
                        accounts.get(0).refreshBalance(new TradeItCallback<TradeItLinkedBrokerAccountParcelable>() {
                            @Override
                            public void onSuccess(TradeItLinkedBrokerAccountParcelable linkedBrokerAccountParcelable) {
                                assertThat("refreshBalance returns available cash", linkedBrokerAccountParcelable.getBalance().availableCash, notNullValue());
                                accounts.get(0).refreshPositions(new TradeItCallback<List<TradeItPositionParcelable>>() {
                                    @Override
                                    public void onSuccess(List<TradeItPositionParcelable> positions) {
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

        boolean notExpired = lock.await(EXPIRED_TIME, TimeUnit.MILLISECONDS);
        assertThat("The call to linkBroker is not expired", notExpired, is(true));
    }
    @Test
    public void linkBrokerOldMethodAndAuthenticationAndTrade() throws InterruptedException {
        linkedBrokerManager.linkBroker("My accountLabel 1", "Dummy", "dummy", "dummy",  new TradeItCallback<TradeItLinkedBrokerParcelable>() {
            @Override
            public void onSuccess(final TradeItLinkedBrokerParcelable linkedBroker) {
                assertThat("The linkedLogin userId is not null", linkedBroker.getLinkedLogin().userId , notNullValue());
                assertThat("The linkedLogin userToken is not null", linkedBroker.getLinkedLogin().userId , notNullValue());
                linkedBroker.authenticateIfNeeded(new TradeItCallbackWithSecurityQuestionImpl<List<TradeItLinkedBrokerAccountParcelable>>() {
                    @Override
                    public void onSuccess(List<TradeItLinkedBrokerAccountParcelable> accounts) {
                        assertThat("The authentication is successful",  !accounts.isEmpty(), is(true));

                        final TradeItOrderParcelable order = new TradeItOrderParcelable(accounts.get(0), "GE");
                        order.previewOrder(new TradeItCallback<TradeItPreviewStockOrEtfOrderResponse>() {
                            @Override
                            public void onSuccess(TradeItPreviewStockOrEtfOrderResponse response) {
                                assertThat("Preview order is successful",  response.orderId, notNullValue());
                                order.placeOrder(response.orderId, new TradeItCallback<TradeItPlaceStockOrEtfOrderResponse>() {
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
        boolean notExpired = lock.await(EXPIRED_TIME, TimeUnit.MILLISECONDS);
        assertThat("The call to linkBroker is not expired", notExpired, is(true));
    }

    @Test
    public void linkBrokerOldMethodAndSecurityQuestion() throws InterruptedException {
        linkedBrokerManager.linkBroker("My accountLabel 1", "Dummy", "dummySecurity", "dummy",  new TradeItCallback<TradeItLinkedBrokerParcelable>() {
            @Override
            public void onSuccess(final TradeItLinkedBrokerParcelable linkedBroker) {
                assertThat("The linkedLogin userId is not null", linkedBroker.getLinkedLogin().userId , notNullValue());
                assertThat("The linkedLogin userToken is not null", linkedBroker.getLinkedLogin().userToken , notNullValue());
                linkedBroker.authenticateIfNeeded(new TradeItCallbackWithSecurityQuestionImpl<List<TradeItLinkedBrokerAccountParcelable>>() {
                    @Override
                    public void onSuccess(List<TradeItLinkedBrokerAccountParcelable> accounts) {
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

        boolean notExpired = lock.await(EXPIRED_TIME, TimeUnit.MILLISECONDS);
        assertThat("The call to linkBrokerOldMethodAndSecurityQuestion is not expired", notExpired, is(true));
    }

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

    @Test
    public void linkAndUnlinkBrokers() throws InterruptedException {
        linkedBrokerManager.linkBroker("My accountLabel 1", "Dummy", "dummy", "dummy",  new TradeItCallback<TradeItLinkedBrokerParcelable>() {
            @Override
            public void onSuccess(final TradeItLinkedBrokerParcelable linkedBroker) {
                List<TradeItLinkedBrokerParcelable> linkedBrokers = linkedBrokerManager.getLinkedBrokers();
                assertThat("we linked one broker", linkedBrokers.size(), is(1));

                linkedBrokerManager.unlinkBroker(linkedBroker, new TradeItCallback<TradeItResponse>() {
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

        boolean notExpired = lock.await(EXPIRED_TIME, TimeUnit.MILLISECONDS);
        assertThat("The call to linkAndUnlinkBrokers is not expired", notExpired, is(true));
    }

    @Test
    public void linkBrokerAndGetOAuthLoginPopupForTokenUpdateUrl() throws InterruptedException {
        linkedBrokerManager.linkBroker("My accountLabel 1", "Dummy", "dummy", "dummy",  new TradeItCallback<TradeItLinkedBrokerParcelable>() {
            @Override
            public void onSuccess(final TradeItLinkedBrokerParcelable linkedBroker) {
                String userId = linkedBroker.getLinkedLogin().userId;
                assertThat("we linked one broker", userId, notNullValue());

                linkedBrokerManager.getOAuthLoginPopupForTokenUpdateUrl(linkedBroker, "myinternalappcallback", new TradeItCallback<String>() {

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

        boolean notExpired = lock.await(EXPIRED_TIME, TimeUnit.MILLISECONDS);
        assertThat("The call to linkBrokerAndGetOAuthLoginPopupForTokenUpdateUrl is not expired", notExpired, is(true));
    }
}
