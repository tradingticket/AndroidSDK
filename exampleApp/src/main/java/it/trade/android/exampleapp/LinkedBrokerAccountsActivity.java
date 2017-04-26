package it.trade.android.exampleapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.List;

import it.trade.android.sdk.model.TradeItLinkedBrokerAccountParcelable;

import static it.trade.android.exampleapp.MainActivity.LINKED_BROKER_ACCOUNTS_PARAMETER;

public class LinkedBrokerAccountsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linked_broker_accounts);
        TextView textView = (TextView) this.findViewById(R.id.linked_broker_accounts_textview);

        Intent intent = getIntent();
        List<TradeItLinkedBrokerAccountParcelable> linkedBrokerAccounts = intent.getParcelableArrayListExtra(LINKED_BROKER_ACCOUNTS_PARAMETER);

        if (linkedBrokerAccounts.isEmpty()) {
            textView.setText("No linked broker accounts!");
        } else {
            textView.setText("# of linkedBroker accounts: " + linkedBrokerAccounts.size() + " : " + linkedBrokerAccounts.toString());
        }

    }
}
