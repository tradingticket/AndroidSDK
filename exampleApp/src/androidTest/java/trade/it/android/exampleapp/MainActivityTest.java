package trade.it.android.exampleapp;


import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.web.webdriver.DriverAtoms;
import android.support.test.espresso.web.webdriver.Locator;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.web.sugar.Web.onWebView;
import static android.support.test.espresso.web.webdriver.DriverAtoms.clearElement;
import static android.support.test.espresso.web.webdriver.DriverAtoms.findElement;
import static android.support.test.espresso.web.webdriver.DriverAtoms.webClick;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

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
    public void tapOAuthFlowButtonTest() throws InterruptedException {
        ViewInteraction appCompatButton = onView(
        allOf(withId(R.id.button_test_oauth), withText("Test oAuth flow"), isDisplayed()));
        appCompatButton.perform(click());

        Thread.sleep(2000l); //TODO there should be a better way for wating

        onWebView()
                .withElement(findElement(Locator.NAME, "id"))
                .perform(clearElement())
                .perform(DriverAtoms.webKeys("dummy"))
                .withElement(findElement(Locator.NAME, "password"))
                .perform(clearElement())
                .perform(DriverAtoms.webKeys("dummy"))
                .withElement(findElement(Locator.TAG_NAME, "button"))
                .perform(webClick());

        Thread.sleep(3000l); //TODO there should be a better way for wating

        onView(withId(R.id.textViewResult)).check(matches(withText(containsString("oAuthFlow Success:"))));

    }
}
