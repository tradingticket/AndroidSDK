package it.trade.android.exampleapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import it.trade.android.exampleapp.adapters.BrokerAdapter;
import it.trade.android.sdk.TradeItSDK;
import it.trade.android.sdk.manager.TradeItLinkedBrokerManager;
import it.trade.android.sdk.model.TradeItCallBackImpl;
import it.trade.android.sdk.model.TradeItErrorResult;
import it.trade.android.sdk.model.TradeItLinkedBroker;
import it.trade.tradeitapi.model.TradeItAvailableBrokersResponse;
import it.trade.tradeitapi.model.TradeItAvailableBrokersResponse.Broker;

public class OauthLinkBrokerActivity extends AppCompatActivity {

    public final static String OAUTH_URL_PARAMETER = "it.trade.android.exampleapp.OAUTH_URL";
    TradeItLinkedBrokerManager linkedBrokerManager = TradeItSDK.getLinkedBrokerManager();
    TextView oAuthResultTextView;
    Spinner brokersSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth_link_broker);
        oAuthResultTextView = (TextView) this.findViewById(R.id.oAuthTextViewResult);
        oAuthResultTextView.setMovementMethod(new ScrollingMovementMethod());
        final Button linkBrokerButton = (Button) this.findViewById(R.id.button_test_oauth);
        brokersSpinner = (Spinner) this.findViewById(R.id.brokers_spinner);
        final OauthLinkBrokerActivity oauthLinkBrokerActivity = this;

        linkedBrokerManager.getAvailableBrokers(new TradeItCallBackImpl<List<TradeItAvailableBrokersResponse.Broker>>() {
            @Override
            public void onSuccess(List<TradeItAvailableBrokersResponse.Broker> brokersList) {
                linkBrokerButton.setEnabled(true);
                oAuthResultTextView.setText("Brokers available: " + brokersList + "\n");
                BrokerAdapter adapter = new BrokerAdapter(oauthLinkBrokerActivity, brokersList);
                brokersSpinner.setAdapter(adapter);
            }

            @Override
            public void onError(TradeItErrorResult error) {
                linkBrokerButton.setEnabled(false);
                oAuthResultTextView.setText("Error getting the brokers: " + error + "\n");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            String oAuthVerifier = intent.getData().getQueryParameter("oAuthVerifier");
            if (oAuthVerifier != null) {
                //TODO see if broker is returned in the response, label usefull ?
                linkedBrokerManager.linkBrokerWithOauthVerifier("MyAccountLabel", "Dummy", oAuthVerifier, new TradeItCallBackImpl<TradeItLinkedBroker>() {
                    @Override
                    public void onSuccess(TradeItLinkedBroker linkedBroker) {
                        oAuthResultTextView.setText("oAuthFlow Success: " + linkedBroker.toString() + "\n");
                    }

                    @Override
                    public void onError(TradeItErrorResult error) {
                        oAuthResultTextView.setText("linkBrokerWithOauthVerifier Error: " + error + "\n");
                    }
                });
            }
        }
    }

    public void processOauthFlow(View view) {
        final Context context = this;
        Broker brokerSelected = (Broker) brokersSpinner.getSelectedItem();
        linkedBrokerManager.getOAuthLoginPopupUrlForMobile(brokerSelected.shortName, "exampleapp://tradeit", new TradeItCallBackImpl<String>() {
            @Override
            public void onSuccess(String oAuthUrl) {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra(OAUTH_URL_PARAMETER, oAuthUrl);
                startActivity(intent);
            }

            @Override
            public void onError(TradeItErrorResult error) {
                oAuthResultTextView.setText("getOAuthLoginPopupUrlForMobile Error: " + error + "\n");
            }
        });
    }
}
