package it.trade.android.exampleapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import it.trade.android.sdk.model.TradeItBalanceParcelable;

import static it.trade.android.exampleapp.MainActivity.BALANCES_PARAMETER;

public class BalancesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balances);
        TextView textView = (TextView) this.findViewById(R.id.account_overview_textview);
        textView.setMovementMethod(new ScrollingMovementMethod());
        Intent intent = getIntent();
        TradeItBalanceParcelable balance = intent.getParcelableExtra(BALANCES_PARAMETER);
        textView.setText(balance.toString());
    }
}
