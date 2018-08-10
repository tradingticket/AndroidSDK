package it.trade.android.exampleapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import java.util.List;

import it.trade.android.sdk.model.orderstatus.TradeItOrderStatusParcelable;

import static it.trade.android.exampleapp.MainActivity.ORDERS_STATUS_PARAMETER;

public class OrdersStatusActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_status);
        TextView textView = (TextView) this.findViewById(R.id.orders_status_textview);
        textView.setMovementMethod(new ScrollingMovementMethod());
        Intent intent = getIntent();
        List<TradeItOrderStatusParcelable> ordersStatusDetailsList = intent.getParcelableArrayListExtra(MainActivity.ORDERS_STATUS_PARAMETER);
        textView.setText(ordersStatusDetailsList.toString());

    }
}
