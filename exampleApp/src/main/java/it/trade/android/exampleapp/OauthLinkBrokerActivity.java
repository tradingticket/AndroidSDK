package it.trade.android.exampleapp;

import android.app.Activity;
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
import it.trade.android.sdk.model.TradeItLinkedBrokerParcelable;
import it.trade.model.TradeItErrorResult;
import it.trade.model.callback.TradeItCallback;
import it.trade.model.reponse.TradeItAvailableBrokersResponse.Broker;

public class OauthLinkBrokerActivity extends AppCompatActivity {

    public final static String OAUTH_URL_PARAMETER = "it.trade.android.exampleapp.OAUTH_URL";
    TradeItLinkedBrokerManager linkedBrokerManager = TradeItSDK.getLinkedBrokerManager();
    TextView oAuthResultTextView;
    Spinner brokersSpinner;
    private static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth_link_broker);
        oAuthResultTextView = (TextView) this.findViewById(R.id.oAuthTextViewResult);
        oAuthResultTextView.setMovementMethod(new ScrollingMovementMethod());
        final Button linkBrokerButton = (Button) this.findViewById(R.id.button_test_oauth);
        brokersSpinner = (Spinner) this.findViewById(R.id.brokers_spinner);
        final OauthLinkBrokerActivity oauthLinkBrokerActivity = this;

        linkedBrokerManager.getAvailableBrokers(new TradeItCallback<List<Broker>>() {
            @Override
            public void onSuccess(List<Broker> brokersList) {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK){
                String oAuthVerifier = intent.getData().getQueryParameter("oAuthVerifier");
                if (oAuthVerifier != null) {
                    linkedBrokerManager.linkBrokerWithOauthVerifier("MyAccountLabel", oAuthVerifier, new TradeItCallback<TradeItLinkedBrokerParcelable>() {
                        @Override
                        public void onSuccess(TradeItLinkedBrokerParcelable linkedBroker) {
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
    }

    public void processOauthFlow(View view) {
        final Context context = this.getApplicationContext();
        Broker brokerSelected = (Broker) brokersSpinner.getSelectedItem();
        if (brokerSelected == null) {
            return;
        }
        linkedBrokerManager.getOAuthLoginPopupUrl(brokerSelected.shortName, "exampleapp://tradeit", new TradeItCallback<String>() {
            @Override
            public void onSuccess(String oAuthUrl) {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra(OAUTH_URL_PARAMETER, oAuthUrl);
                startActivityForResult(intent, REQUEST_CODE);
            }

                @Override
                public void onError(TradeItErrorResult error) {
                    oAuthResultTextView.setText("getOAuthLoginPopupUrl Error: " + error + "\n");
                }
            });
        }
    }
}
