package it.trade.android.exampleapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

import it.trade.android.sdk.model.TradeItLinkedBrokerAccountParcelable;
import it.trade.model.TradeItErrorResult;
import it.trade.model.callback.TradeItCallback;

import static it.trade.android.exampleapp.MainActivity.LINKED_BROKER_ACCOUNTS_PARAMETER;

public class LinkedBrokerAccountsActivity extends AppCompatActivity {
    TextView textView;
    List<TradeItLinkedBrokerAccountParcelable> linkedBrokerAccounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linked_broker_accounts);
        this.textView = (TextView) this.findViewById(R.id.linked_broker_accounts_textview);

        Intent intent = getIntent();
        this.linkedBrokerAccounts = intent.getParcelableArrayListExtra(LINKED_BROKER_ACCOUNTS_PARAMETER);

        if (linkedBrokerAccounts.isEmpty()) {
            this.textView.setText("No linked broker accounts!");
        } else {
            // Refresh balance on the first account just to make sure that an unparcelled account works properly
            textView.setText("Refreshing first account balance again just to test...");
            linkedBrokerAccounts.get(0).refreshBalance(new TradeItCallback<TradeItLinkedBrokerAccountParcelable>() {
                @Override
                public void onSuccess(TradeItLinkedBrokerAccountParcelable linkedBrokerAccountParcelable) {
                    textView.setText("Refreshed first account balance again just to test.\n# of linkedBroker accounts: " + linkedBrokerAccounts.size() + "\n\n" + linkedBrokerAccounts.toString());
                }

                @Override
                public void onError(TradeItErrorResult error) {
                    textView.setText("Refresh Balance ERROR:\n" + error);
                }
            });
        }

    }
}
