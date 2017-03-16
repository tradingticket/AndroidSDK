package it.trade.android.exampleapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import it.trade.android.sdk.enums.TradeItOrderAction;
import it.trade.android.sdk.enums.TradeItOrderExpiration;
import it.trade.android.sdk.enums.TradeItOrderPriceType;
import it.trade.android.sdk.model.TradeItCallBackImpl;
import it.trade.android.sdk.model.TradeItErrorResult;
import it.trade.android.sdk.model.TradeItLinkedBrokerAccount;
import it.trade.android.sdk.model.TradeItOrder;
import it.trade.tradeitapi.model.TradeItPlaceStockOrEtfOrderResponse;
import it.trade.tradeitapi.model.TradeItPreviewStockOrEtfOrderResponse;

import static it.trade.android.exampleapp.MainActivity.OAUTH_URL_PARAMETER;


public class WebViewActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        Intent intent = getIntent();
        String urlParam = intent.getStringExtra(OAUTH_URL_PARAMETER);
        TradeItLinkedBrokerAccount account = intent.getParcelableExtra("it.trade.android.exampleapp.LINKED_BROKER_ACCOUNT");

        if (account == null) {
            WebView myWebView = (WebView) findViewById(R.id.webview);
            WebSettings webSettings = myWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            myWebView.loadUrl(urlParam);
        } else {
            // Testing trading
            final TradeItOrder order = new TradeItOrder(account, "CMG");
            order.setAction(TradeItOrderAction.BUY);
            order.setQuantity(1);
            order.setPriceType(TradeItOrderPriceType.LIMIT);
            order.setLimitPrice(1.00);
            order.setExpiration(TradeItOrderExpiration.GOOD_FOR_DAY);

            order.previewOrder(new TradeItCallBackImpl<TradeItPreviewStockOrEtfOrderResponse>() {
                @Override
                public void onSuccess(TradeItPreviewStockOrEtfOrderResponse previewResponse) {
                    Log.d("ASDF", "PREVIEW SUCCESS: " + previewResponse.shortMessage + " - " + previewResponse.orderDetails);

                    String orderId = previewResponse.orderId;

                    order.placeOrder(orderId, new TradeItCallBackImpl<TradeItPlaceStockOrEtfOrderResponse>() {
                        @Override
                        public void onSuccess(TradeItPlaceStockOrEtfOrderResponse placeOrderResponse) {
                            Log.d("ASDF", "PLACE TRADE SUCCESS: " + placeOrderResponse.shortMessage + " - " + placeOrderResponse.longMessages.get(0));
                        }

                        @Override
                        public void onError(TradeItErrorResult error) {
                            Log.d("ASDF", "PLACE TRADE FAIL: " + error.getShortMessage() + " - " + error.getLongMessages().get(0));
                        }
                    });

                }

                @Override
                public void onError(TradeItErrorResult error) {
                    Log.d("ASDF", "PREVIEW FAIL: " + error.getShortMessage() + " - " + error.getLongMessages().get(0));
                }
            });
        }
    }
}
