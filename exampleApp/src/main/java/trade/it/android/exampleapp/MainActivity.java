package trade.it.android.exampleapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import it.trade.tradeitapi.API.TradeItBrokerLinker;
import it.trade.tradeitapi.exception.TradeItKeystoreServiceCreateKeyException;
import it.trade.tradeitapi.exception.TradeItRetrieveLinkedLoginException;
import it.trade.tradeitapi.model.TradeItEnvironment;
import trade.it.android.sdk.manager.TradeItLinkedBrokerManager;
import trade.it.android.sdk.model.TradeItCallBackImpl;
import trade.it.android.sdk.model.TradeItErrorResult;
import trade.it.android.sdk.model.TradeItLinkedBroker;

public class MainActivity extends AppCompatActivity {

    public final static String OAUTH_URL_PARAMETER = "it.trade.android.exampleapp.OAUTH_URL";
    TradeItLinkedBrokerManager linkedBrokerManager;
    TextView textViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.textViewResult = (TextView) findViewById(R.id.textViewResult);

        try {
            linkedBrokerManager = new TradeItLinkedBrokerManager(this.getApplicationContext(), new TradeItBrokerLinker("tradeit-test-api-key", TradeItEnvironment.QA));
        } catch (TradeItKeystoreServiceCreateKeyException e) {
            this.textViewResult.append("Error initializing linkedBrokerManager: " + e.getMessage());
        } catch (TradeItRetrieveLinkedLoginException e) {
            this.textViewResult.append("Error retreiving linked accounts: " + e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            String oAuthVerifier = intent.getData().getQueryParameter("oAuthVerifier");
            if (oAuthVerifier != null) {
                linkedBrokerManager.linkBrokerWithOauthVerifier("MyAccountLabel", "Dummy", oAuthVerifier, new TradeItCallBackImpl<TradeItLinkedBroker>() {
                    @Override
                    public void onSuccess(TradeItLinkedBroker linkedBroker) {
                        textViewResult.append("oAuthFlow Success: " + linkedBroker.toString());
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
