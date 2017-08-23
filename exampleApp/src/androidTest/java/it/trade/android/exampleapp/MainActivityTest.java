package it.trade.android.exampleapp;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.web.webdriver.DriverAtoms;
import android.support.test.espresso.web.webdriver.Locator;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import it.trade.android.sdk.internal.TradeItKeystoreService;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.web.sugar.Web.onWebView;
import static android.support.test.espresso.web.webdriver.DriverAtoms.clearElement;
import static android.support.test.espresso.web.webdriver.DriverAtoms.findElement;
import static android.support.test.espresso.web.webdriver.DriverAtoms.webClick;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;


@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            clearSharedPrefs(InstrumentationRegistry.getTargetContext(), TradeItKeystoreService.TRADE_IT_SHARED_PREFS_KEY);
            super.beforeActivityLaunched();
        }
    };

    private void clearSharedPrefs(Context context, String name) {
        SharedPreferences prefs =
                context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }


    @Rule
    public ActivityTestRule<WebViewActivity> mActivityRule = new ActivityTestRule<WebViewActivity>(
            WebViewActivity.class, false, false) {
        @Override
        protected void afterActivityLaunched() {
            // Technically we do not need to do this - WebViewActivity has javascript turned on.
            // Other WebViews in your app may have javascript turned off, however since the only way
            // to automate WebViews is through javascript, it must be enabled.
            onWebView().forceJavascriptEnabled();
        }
    };


    @Test
    public void oAuthFlowToTradeTest() throws InterruptedException {
        testOauthFlow("dummy");
        testGetLinkedBrokers(1);
        testAuthenticateFirstLinkedBroker();
        testGetBalancesFirstLinkedBroker();
        testPositionsFirstLinkedBrokerAccount();
        testPreviewAndPlaceTradeFirstLinkedBrokerAccount();
        testDeleteAllLinkedBrokers();
    }

    @Test
    public void oAuthFlowMultipleLinkedBrokerTest() throws InterruptedException {
        testOauthFlow("dummyMultiple");
        testOauthFlow("dummy");
        testGetLinkedBrokers(2);
        testAuthenticateAllLinkedBroker(2);
        testRefreshAllBalanceForAllLinkedBroker();
        testDeleteAllLinkedBrokers();
    }

    @Test
    public void testDummySecurity() throws InterruptedException {
        tapOnText(MainActivity.MainActivityActions.AUTHENTICATE_WITH_SECURITY_QUESTION_SIMPLE.getLabel());

        Thread.sleep(1600l); //TODO there should be a better way for waiting

        checkFieldContainsText(R.id.alertTitle, "What is your mother's maiden name");

        ViewInteraction editText = onView(
                allOf(withClassName(is("android.widget.EditText")),
                        withParent(allOf(withId(R.id.custom),
                                withParent(withId(R.id.customPanel)))),
                        isDisplayed()));
        editText.perform(replaceText("tradingticket"), closeSoftKeyboard());

        tapOnText("OK");

        Thread.sleep(500l); //TODO there should be a better way for waiting

        checkFieldContainsText(android.R.id.message, "Successfully Authenticate dummySecurity");

        tapOnText("OK");
    }

    @Test
    public void testDummyMultiple() throws InterruptedException {
        ViewInteraction textView = onView(
                allOf(withText(MainActivity.MainActivityActions.AUTHENTICATE_WITH_SECURITY_QUESTION_OPTIONS.getLabel()), isDisplayed()));
        textView.perform(click());

        Thread.sleep(1600l); //TODO there should be a better way for waiting

        checkFieldContainsText(R.id.alertTitle, "Select an option from the following");

        checkFieldContainsText(android.R.id.message, "option 1\noption 2\noption 3\noption 4\noption 5\noption 6\noption 7\noption 8\noption 9\noption 10");

        ViewInteraction editText = onView(
                allOf(withClassName(is("android.widget.EditText")),
                        withParent(allOf(withId(R.id.custom),
                                withParent(withId(R.id.customPanel)))),
                        isDisplayed()));
        editText.perform(replaceText("option 1"), closeSoftKeyboard());

        tapOnText("OK");

        Thread.sleep(500l); //TODO there should be a better way for waiting

        checkFieldContainsText(android.R.id.message, "Successfully Authenticate dummyOption");

        tapOnText("OK");
    }

    private void testOauthFlow(String dummyLogin) throws InterruptedException {
        tapOnText(MainActivity.MainActivityActions.OAUTH_LINKED_A_BROKER.getLabel());

        Thread.sleep(500l); //TODO there should be a better way for waiting

        checkFieldContainsText(R.id.oAuthTextViewResult, "Brokers available:");

        //select dummy broker in the spinner
        selectBroker("Dummy Broker");

        tapOnText("Link broker");

        Thread.sleep(1500l); //TODO there should be a better way for waiting

        onWebView()
                .withElement(findElement(Locator.NAME, "id"))
                .perform(clearElement())
                .perform(DriverAtoms.webKeys(dummyLogin))
                .withElement(findElement(Locator.NAME, "password"))
                .perform(clearElement())
                .perform(DriverAtoms.webKeys("dummy"))
                .withElement(findElement(Locator.TAG_NAME, "button"))
                .perform(webClick());

        Thread.sleep(2000l); //TODO there should be a better way for waiting

        checkFieldContainsText(R.id.oAuthTextViewResult, "oAuthFlow Success:");

        navigateUp();
    }

    private void selectBroker(String brokerLongName) {
        ViewInteraction appCompatSpinner = onView(
                allOf(withId(R.id.brokers_spinner),
                        withParent(allOf(withId(R.id.activity_oauth_link_broker),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        appCompatSpinner.perform(click());

        ViewInteraction appCompatCheckedTextView = onView(
                allOf(withId(android.R.id.text1), withText(brokerLongName), isDisplayed()));

        appCompatCheckedTextView.perform(click());
    }
    private void testGetLinkedBrokers(int number) throws InterruptedException {
        tapOnText(MainActivity.MainActivityActions.GET_LINKED_BROKERS.getLabel());

        Thread.sleep(1000l); //TODO there should be a better way for waiting

        checkFieldContainsText(R.id.linked_brokers_textview, number + " PARCELED LINKED BROKERS");

        navigateUp();
    }

    private void testAuthenticateFirstLinkedBroker() throws InterruptedException {
        tapOnText(MainActivity.MainActivityActions.AUTHENTICATE_FIRST_LINKED_BROKER.getLabel());

        Thread.sleep(1000l); //TODO there should be a better way for waiting

        checkFieldContainsText(R.id.linked_broker_accounts_textview, "# of linkedBroker accounts: 1 : [TradeItLinkedBrokerAccountParcelable{accountBaseCurrency='USD', accountName='Individual Account', accountNumber='SINGLE-ACCT-0001', balance='null'}]");

        navigateUp();
    }

    private void testAuthenticateAllLinkedBroker(int number) throws InterruptedException {
        tapOnText(MainActivity.MainActivityActions.AUTHENTICATE_ALL_LINKED_BROKERS.getLabel());

        Thread.sleep(1000l); //TODO there should be a better way for waiting

        checkFieldContainsText(R.id.linked_brokers_textview, number + " PARCELED LINKED BROKERS");

        navigateUp();
    }

    private void testRefreshAllBalanceForAllLinkedBroker() throws InterruptedException {
        tapOnText(MainActivity.MainActivityActions.REFRESH_ALL_BALANCES_FIRST_LINKED_BROKER.getLabel());

        Thread.sleep(1000l); //TODO there should be a better way for waiting

        checkFieldContainsText(R.id.linked_broker_accounts_textview, "balance='TradeItBalanceParcelable");

        navigateUp();
    }

    private void testGetBalancesFirstLinkedBroker() throws InterruptedException {
        tapOnText("Get balances for first linked broker account");

        Thread.sleep(1000l); //TODO there should be a better way for waiting

        checkFieldContainsText(R.id.account_overview_textview, "TradeItBalanceParcelable{availableCash=1204.06, buyingPower=2408.12, dayAbsoluteReturn=78.42, dayPercentReturn=3.25, totalAbsoluteReturn=14486.67, totalPercentReturn=22.84, totalValue=76489.23}");

        navigateUp();
    }

    private void testPositionsFirstLinkedBrokerAccount() throws InterruptedException {
        tapOnText(MainActivity.MainActivityActions.GET_POSITIONS_FIRST_LINKED_BROKER_ACCOUNT.getLabel());

        Thread.sleep(1000l); //TODO there should be a better way for waiting

        checkFieldContainsText(R.id.positions_textview, "[TradeItPosition{costbasis=103.34, holdingType='LONG', lastPrice=112.34, quantity=1.0, symbol='AAPL', symbolClass='EQUITY_OR_ETF'");

        navigateUp();
    }

    private void testPreviewAndPlaceTradeFirstLinkedBrokerAccount() throws InterruptedException {
        tapOnText(MainActivity.MainActivityActions.PREVIEW_TRADE_FIRST_LINKED_BROKER_ACCOUNT.getLabel());

        Thread.sleep(1000l); //TODO there should be a better way for waiting

        checkFieldContainsText(R.id.preview_order_textview, "TradeItPreviewStockOrEtfOrderResponseParcelable{orderId='1', ackWarningsList=[], warningsList=[], orderDetails=OrderDetails{orderSymbol='GE', orderAction='buy', orderQuantity=1.0, orderExpiration='day', orderPrice='$20.00', orderValueLabel='Estimated Cost', orderMessage='You are about to place a limit order to buy GE', lastPrice='null', bidPrice='null', askPrice='null'");

        //place trade
        tapOnText("Place trade");

        Thread.sleep(1500l); //TODO there should be a better way for waiting

        checkFieldContainsText(R.id.preview_order_textview,"TradeItPlaceStockOrEtfOrderResponseParcelable{broker='Dummy', confirmationMessage='Your order message");

        navigateUp();
    }

    private void testDeleteAllLinkedBrokers() {
        tapOnText(MainActivity.MainActivityActions.DELETE_ALL_LINKED_BROKERS.getLabel());

        checkFieldContainsText(android.R.id.message, "# of linkedBrokers after deletion: 0");

        tapOnText("OK");
    }

    private void checkFieldContainsText(int fieldId, String text) {
        onView(withId(fieldId)).check(matches(withText(containsString(text))));
    }

    private void tapOnText(String text) {
        ViewInteraction textView = onView(
                allOf(withText(text), isDisplayed()));
        textView.perform(click());
    }

    private void navigateUp() {
        ViewInteraction imageButton = onView(
                allOf(withContentDescription("Navigate up"),
                        withParent(allOf(withId(R.id.action_bar),
                                withParent(withId(R.id.action_bar_container)))),
                        isDisplayed()));
        imageButton.perform(click());
    }
}
