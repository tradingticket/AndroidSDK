package it.trade.android.exampleapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import it.trade.android.sdk.model.TradeItCryptoOrderParcelable;
import it.trade.android.sdk.model.orderstatus.TradeItOrderStatusParcelable;
import it.trade.model.TradeItErrorResult;
import it.trade.model.callback.TradeItCallback;
import it.trade.model.reponse.TradeItPreviewCryptoOrderResponse;

import static it.trade.android.exampleapp.MainActivity.PREVIEW_ORDER_PARAMETER;

public class PreviewCryptoOrderActivity extends AppCompatActivity {
    private static final String TAG = PreviewCryptoOrderActivity.class.getName();
    private String orderId;
    private TradeItCryptoOrderParcelable order;
    private TextView textView;
    private Button cancelOrderButton;
    String orderNumber = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_order);
        textView = (TextView) this.findViewById(R.id.preview_order_textview);
        textView.setMovementMethod(new ScrollingMovementMethod());
        Intent intent = getIntent();
        order = intent.getParcelableExtra(PREVIEW_ORDER_PARAMETER);
        final Button placeOrderButton = (Button) this.findViewById(R.id.place_trade_button);
        cancelOrderButton = (Button) this.findViewById(R.id.cancel_order_button);
        order.previewCryptoOrder(new TradeItCallback<TradeItPreviewCryptoOrderResponse>() {
            @Override
            public void onSuccess(TradeItPreviewCryptoOrderResponse response) {
                textView.setText(response.toString());
                orderId = response.orderId;
                placeOrderButton.setEnabled(true);
            }

            @Override
            public void onError(TradeItErrorResult error) {
                Log.e(TAG, "ERROR previewCryptoOrder: " + error);
                textView.setText("ERROR previewCryptoOrder: " + error);
            }
        });
    }

//    public void placeTrade(View view) {
//        order.placeOrder(orderId, new TradeItCallback<TradeItPlaceStockOrEtfOrderResponseParcelable>() {
//            @Override
//            public void onSuccess(TradeItPlaceStockOrEtfOrderResponseParcelable placeOrderResponse) {
//                textView.append(placeOrderResponse.toString());
//                cancelOrderButton.setEnabled(true);
//                orderNumber = placeOrderResponse.getOrderNumber();
//            }
//
//            @Override
//            public void onError(TradeItErrorResult error) {
//                Log.e(TAG, "ERROR placeOrder: " + error.toString());
//                textView.setText("ERROR placeOrder: " + error);
//            }
//        });
//    }

    public void cancelOrder(View view) {
        order.getLinkedBrokerAccount().cancelOrder(orderNumber, new TradeItCallback<TradeItOrderStatusParcelable>() {
            @Override
            public void onSuccess(TradeItOrderStatusParcelable orderStatusParcelable) {
                textView.append(orderStatusParcelable.toString());
            }

            @Override
            public void onError(TradeItErrorResult error) {
                Log.e(TAG, "ERROR cancelOrder: " + error.toString());
                textView.setText("ERROR cancelOrder: " + error);
            }
        });
    }
}
