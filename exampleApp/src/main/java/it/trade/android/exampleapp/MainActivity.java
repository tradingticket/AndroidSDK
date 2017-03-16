package it.trade.android.exampleapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import it.trade.android.sdk.TradeItSDK;
import it.trade.android.sdk.manager.TradeItLinkedBrokerManager;
import it.trade.android.sdk.model.TradeItCallBackImpl;
import it.trade.android.sdk.model.TradeItCallbackWithSecurityQuestionImpl;
import it.trade.android.sdk.model.TradeItErrorResult;
import it.trade.android.sdk.model.TradeItLinkedBroker;
import it.trade.android.sdk.model.TradeItLinkedBrokerAccount;
import it.trade.android.sdk.model.TradeItSecurityQuestion;
import it.trade.tradeitapi.model.TradeItEnvironment;

public class MainActivity extends AppCompatActivity {

    public final static String OAUTH_URL_PARAMETER = "it.trade.android.exampleapp.OAUTH_URL";
    TradeItLinkedBrokerManager linkedBrokerManager;
    TextView textViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.textViewResult = (TextView) findViewById(R.id.textViewResult);

        TradeItSDK.configure(this.getApplicationContext(), "tradeit-test-api-key", TradeItEnvironment.QA);
        linkedBrokerManager = TradeItSDK.getLinkedBrokerManager();


    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            String oAuthVerifier = intent.getData().getQueryParameter("oAuthVerifier");
            if (oAuthVerifier != null) {
                final MainActivity mainActivity = this;
                linkedBrokerManager.linkBrokerWithOauthVerifier("MyAccountLabel", "Dummy", oAuthVerifier, new TradeItCallBackImpl<TradeItLinkedBroker>() {
                    @Override
                    public void onSuccess(TradeItLinkedBroker linkedBroker) {
                        textViewResult.append("oAuthFlow Success: " + linkedBroker.toString());

                        // TEST TRADING
                        linkedBroker.authenticate(new TradeItCallbackWithSecurityQuestionImpl<List<TradeItLinkedBrokerAccount>>() {
                            @Override
                            public void onSuccess(final List<TradeItLinkedBrokerAccount> accounts) {
                                Log.d("ASDF", "AUTH SUCCESS: " + accounts.size() + " accounts");

                                Intent intent = new Intent(mainActivity, WebViewActivity.class);
                                intent.putExtra("it.trade.android.exampleapp.LINKED_BROKER_ACCOUNT", accounts.get(0));
                                startActivity(intent);
                            }

                            @Override
                            public void onSecurityQuestion(TradeItSecurityQuestion securityQuestion) {
                                Log.d("ASDF", "AUTH SECURITY QUESTION!: " + securityQuestion.getSecurityQuestion());

                                // this.submitSecurityAnswer("my answer");
                            }

                            @Override
                            public void onError(TradeItErrorResult error) {
                                Log.d("ASDF", "AUTH FAIL: " + error.getShortMessage() + " - " + error.getLongMessages().get(0));
                            }
                        });
                    }

                    @Override
                    public void onError(TradeItErrorResult error) {
                        textViewResult.append("oAuthFlow Error: " + error.toString());
                    }
                });
            }
        }
    }

    public void processOauthFlow(View view) {
        final Context context = this;
        linkedBrokerManager.getOAuthLoginPopupUrlForMobile("Dummy", "exampleapp://tradeit", new TradeItCallBackImpl<String>() {
            @Override
            public void onSuccess(String oAuthUrl) {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra(OAUTH_URL_PARAMETER, oAuthUrl);
                startActivity(intent);
            }

            @Override
            public void onError(TradeItErrorResult error) {
                textViewResult.append("oAuthFlow Error: " + error.toString());
            }
        });
    }
}
