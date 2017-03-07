package it.trade.android.exampleapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.List;

import it.trade.android.sdk.model.TradeItCallbackWithSecurityQuestionImpl;
import it.trade.android.sdk.model.TradeItErrorResult;
import it.trade.android.sdk.model.TradeItLinkedBroker;
import it.trade.android.sdk.model.TradeItLinkedBrokerAccount;
import it.trade.android.sdk.model.TradeItSecurityQuestion;

public class LinkedBrokerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linked_broker);
        final TextView linkedBrokerTextView = (TextView) this.findViewById(R.id.linkedBrokerTextView);
        final TradeItLinkedBroker linkedBroker = (TradeItLinkedBroker) getIntent().getParcelableExtra("linkedBroker");
        if (linkedBroker != null) {
            linkedBroker.authenticate(new TradeItCallbackWithSecurityQuestionImpl<List<TradeItLinkedBrokerAccount>>() {
                @Override
                public void onSecurityQuestion(TradeItSecurityQuestion securityQuestion) {
                    linkedBrokerTextView.setText("SecurityQuestion: " + securityQuestion.getSecurityQuestionOptions());
                }

                @Override
                public void onSuccess(List<TradeItLinkedBrokerAccount> accounts) {
                    linkedBrokerTextView.setText("linkedBroker accounts: " + linkedBroker.getAccounts().toString());
                }

                @Override
                public void onError(TradeItErrorResult error) {
                    linkedBrokerTextView.setText("Error: " + error.toString());
                }
            });
        }
    }
}
