package it.trade.android.exampleapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import it.trade.android.exampleapp.adapters.BrokerAdapter;
import it.trade.android.sdk.TradeItSDK;
import it.trade.android.sdk.manager.TradeItLinkedBrokerManager;
import it.trade.model.TradeItErrorResult;
import it.trade.model.callback.TradeItCallback;
import it.trade.model.reponse.Instrument;
import it.trade.model.reponse.TradeItAvailableBrokersResponse.Broker;

import static it.trade.android.exampleapp.MainActivity.GET_BROKERS_LIST_PARAMETER;
public class BrokersListActivity extends AppCompatActivity {
    TradeItLinkedBrokerManager linkedBrokerManager = TradeItSDK.getLinkedBrokerManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brokers_list);
        final Spinner brokersSpinner = (Spinner) this.findViewById(R.id.brokers_spinner);
        final TextView textView = (TextView) this.findViewById(R.id.brokers_textview);
        textView.setMovementMethod(new ScrollingMovementMethod());
        Intent intent = getIntent();
        MainActivity.MainActivityActions action = (MainActivity.MainActivityActions) intent.getExtras().get(GET_BROKERS_LIST_PARAMETER);
        final BrokersListActivity brokersListActivity = this;
        switch (action) {
            case GET_ALL_FEATURED_BROKERS:
                linkedBrokerManager.getAllFeaturedBrokers(new TradeItCallback<List<Broker>>() {
                    @Override
                    public void onSuccess(List<Broker> brokersList) {
                        brokersSpinner.setAdapter(new BrokerAdapter(brokersListActivity, brokersList));
                        textView.setText(brokersList.toString());
                    }

                    @Override
                    public void onError(TradeItErrorResult error) {
                        textView.setText(error.toString());
                    }
                });
                break;
            case GET_ALL_NON_FEATURED_BROKERS:
                linkedBrokerManager.getAllNonFeaturedBrokers(new TradeItCallback<List<Broker>>() {
                    @Override
                    public void onSuccess(List<Broker> brokersList) {
                        brokersSpinner.setAdapter(new BrokerAdapter(brokersListActivity, brokersList));
                        textView.setText(brokersList.toString());
                    }

                    @Override
                    public void onError(TradeItErrorResult error) {
                        textView.setText(error.toString());
                    }
                });
                break;
            case GET_FEATURED_EQUITY_BROKERS:
                linkedBrokerManager.getFeaturedBrokersForInstrumentType(Instrument.EQUITIES, new TradeItCallback<List<Broker>>() {
                    @Override
                    public void onSuccess(List<Broker> brokersList) {
                        brokersSpinner.setAdapter(new BrokerAdapter(brokersListActivity, brokersList));
                        textView.setText(brokersList.toString());
                    }

                    @Override
                    public void onError(TradeItErrorResult error) {
                        textView.setText(error.toString());
                    }
                });
                break;
            case GET_NON_FEATURED_EQUITY_BROKERS:
                linkedBrokerManager.getNonFeaturedBrokersForInstrumentType(Instrument.EQUITIES, new TradeItCallback<List<Broker>>() {
                    @Override
                    public void onSuccess(List<Broker> brokersList) {
                        brokersSpinner.setAdapter(new BrokerAdapter(brokersListActivity, brokersList));
                        textView.setText(brokersList.toString());
                    }

                    @Override
                    public void onError(TradeItErrorResult error) {
                        textView.setText(error.toString());
                    }
                });
                break;
            default:
                textView.setText("this action is not handled: (" + action + ")");
                break;
        }
    }
}
