package it.trade.android.exampleapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import it.trade.android.exampleapp.adapters.BrokerAdapter;
import it.trade.android.exampleapp.customtabs.CustomTabActivityHelper;
import it.trade.android.sdk.TradeItSDK;
import it.trade.android.sdk.manager.TradeItLinkedBrokerManager;
import it.trade.android.sdk.model.TradeItLinkedBrokerParcelable;
import it.trade.model.TradeItErrorResult;
import it.trade.model.callback.TradeItCallback;
import it.trade.model.reponse.TradeItAvailableBrokersResponse.Broker;

import static android.content.Intent.FLAG_ACTIVITY_NO_HISTORY;

public class OauthLinkBrokerActivity extends AppCompatActivity implements CustomTabActivityHelper.ConnectionCallback{

    public static final String APP_DEEP_LINK = "exampleapp://tradeit";
    TradeItLinkedBrokerManager linkedBrokerManager = TradeItSDK.getLinkedBrokerManager();
    TextView oAuthResultTextView;
    Spinner brokersSpinner;
    private CustomTabActivityHelper customTabActivityHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth_link_broker);

        customTabActivityHelper = new CustomTabActivityHelper();
        customTabActivityHelper.setConnectionCallback(this);

        oAuthResultTextView = (TextView) this.findViewById(R.id.oAuthTextViewResult);
        oAuthResultTextView.setMovementMethod(new ScrollingMovementMethod());
        final Button linkBrokerButton = (Button) this.findViewById(R.id.button_test_oauth);
        brokersSpinner = (Spinner) this.findViewById(R.id.brokers_spinner);
        final OauthLinkBrokerActivity oauthLinkBrokerActivity = this;

        Intent intent = getIntent();
        final String userId = intent.getStringExtra(MainActivity.RELINK_OAUTH_PARAMETER);

        linkedBrokerManager.getAvailableBrokers(new TradeItCallback<List<Broker>>() {
            @Override
            public void onSuccess(List<Broker> brokersList) {
                linkBrokerButton.setEnabled(true);
                oAuthResultTextView.setText("Brokers available: " + brokersList + "\n");
                BrokerAdapter adapter = new BrokerAdapter(oauthLinkBrokerActivity, brokersList);
                brokersSpinner.setAdapter(adapter);
                if (userId != null) {
                    relinkOauthFlow(userId);
                }

            }

            @Override
            public void onError(TradeItErrorResult error) {
                linkBrokerButton.setEnabled(false);
                oAuthResultTextView.setText("Error getting the brokers: " + error + "\n");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        customTabActivityHelper.bindCustomTabsService(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        customTabActivityHelper.unbindCustomTabsService(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
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

    public void processOauthFlow(View view) {
        Broker brokerSelected = (Broker) brokersSpinner.getSelectedItem();
        if (brokerSelected == null) {
            return;
        }
        linkedBrokerManager.getOAuthLoginPopupUrl(brokerSelected.shortName, APP_DEEP_LINK, new TradeItCallback<String>() {
            @Override
            public void onSuccess(String oAuthUrl) {
                launchCustomTab(oAuthUrl);
            }

            @Override
            public void onError(TradeItErrorResult error) {
                oAuthResultTextView.setText("getOAuthLoginPopupUrl Error: " + error + "\n");
            }
        });
    }

    public void relinkOauthFlow(String userId) {
        linkedBrokerManager.getOAuthLoginPopupForTokenUpdateUrlByUserId(userId, APP_DEEP_LINK, new TradeItCallback<String>() {
            @Override
            public void onSuccess(String oAuthUrl) {
                launchCustomTab(oAuthUrl);
            }

            @Override
            public void onError(TradeItErrorResult error) {
                oAuthResultTextView.setText("getOAuthLoginPopupForTokenUpdateUrlByUserId Error: " + error + "\n");
            }
        });
    }

    private void launchCustomTab(String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(getResources().getColor(R.color.colorPrimary));

        CustomTabsIntent customTabsIntent = builder.build();
        Uri uri = Uri.parse(url);
        customTabsIntent.intent.addFlags(FLAG_ACTIVITY_NO_HISTORY);
        CustomTabActivityHelper.openCustomTab(this, customTabsIntent, uri,
                //fallback if custom Tabs is not available
                new CustomTabActivityHelper.CustomTabFallback() {
                    @Override
                    public void openUri(Activity activity, Uri uri) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        activity.startActivity(intent);
                    }
                }
        );
    }

    @Override
    public void onCustomTabsConnected() {
    }

    @Override
    public void onCustomTabsDisconnected() {
    }
}