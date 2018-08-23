package it.trade.android.exampleapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import it.trade.android.sdk.model.TradeItLinkedBrokerAccountParcelable;
import it.trade.android.sdk.model.TradeItLinkedBrokerParcelable;
import it.trade.model.TradeItErrorResult;
import it.trade.model.TradeItSecurityQuestion;
import it.trade.model.callback.TradeItCallbackWithSecurityQuestionImpl;

import static it.trade.android.exampleapp.MainActivity.LINKED_BROKERS_PARAMETER;

public class LinkedBrokersActivity extends AppCompatActivity {
    List<TradeItLinkedBrokerParcelable> linkedBrokers;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linked_brokers);
        this.textView = (TextView) this.findViewById(R.id.linked_brokers_textview);
        this.textView.setMovementMethod(new ScrollingMovementMethod());

        Intent intent = getIntent();
        this.linkedBrokers = intent.getParcelableArrayListExtra(LINKED_BROKERS_PARAMETER);

        logBrokers();
    }

    public void authenticateFirstBroker(View view) {
        textView.setText("Authenticating...");

        this.linkedBrokers.get(0).authenticate(new TradeItCallbackWithSecurityQuestionImpl<List<TradeItLinkedBrokerAccountParcelable>>() {
            @Override
            public void onSecurityQuestion(TradeItSecurityQuestion securityQuestion) {
                textView.setText("Security Question:\n" + securityQuestion);
            }

            @Override
            public void onSuccess(List<TradeItLinkedBrokerAccountParcelable> type) {
                textView.setText("Authenticate SUCCESS!");
            }

            @Override
            public void onError(TradeItErrorResult error) {
                textView.setText("Authenticate ERROR:\n" + error);
            }
        });
    }

    private void logBrokers() {
        if (this.linkedBrokers.isEmpty()) {
            this.textView.setText("No linked brokers!");
        } else {
            String output = "";

            output += "=== " + this.linkedBrokers.size() + " PARCELED LINKED BROKERS ===\n\n";

            for (TradeItLinkedBrokerParcelable linkedBroker : this.linkedBrokers) {
                String json = "LINKED BROKER: " + linkedBroker;
                output += json + "\n\n===\n\n";
                Log.d("TEST", json);
            }

            this.textView.setText(output);
            Log.d("TEST", "==========");
        }
    }
}
