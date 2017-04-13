package it.trade.android.exampleapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import it.trade.android.sdk.model.TradeItLinkedBroker;

import static it.trade.android.exampleapp.MainActivity.LINKED_BROKERS_PARAMETER;

public class LinkedBrokersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linked_brokers);
        final TextView textView = (TextView) this.findViewById(R.id.linked_brokers_textview);
        textView.setMovementMethod(new ScrollingMovementMethod());
        Intent intent = getIntent();
        List<TradeItLinkedBroker> linkedBrokers = intent.getParcelableArrayListExtra(LINKED_BROKERS_PARAMETER);

        if (linkedBrokers.isEmpty()) {
            textView.setText("No linked brokers!");
        } else {
            String output = "";

            output += "=== " + linkedBrokers.size() + " PARCELED LINKED BROKERS ===\n\n";

            Gson gson = new Gson();
            for (TradeItLinkedBroker linkedBroker : linkedBrokers) {
                String json = "LINKED LOGIN: " + gson.toJson(linkedBroker.getLinkedLogin());
                json += "\nLINKED BROKER: " + gson.toJson(linkedBroker);
                output += json + "\n\n===\n\n";
                Log.d("TEST", json);
            }
            textView.setText(output);
            Log.d("TEST", "==========");
        }
    }
}
