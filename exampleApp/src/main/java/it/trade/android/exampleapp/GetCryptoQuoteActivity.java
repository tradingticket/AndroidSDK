package it.trade.android.exampleapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import it.trade.android.sdk.model.TradeItCryptoQuoteResponseParcelable;

import static it.trade.android.exampleapp.MainActivity.GET_CRYPTO_QUOTE_PARAMETER;

public class GetCryptoQuoteActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_crypto_quote);

        textView = (TextView) this.findViewById(R.id.get_crypto_quote_textview);
        textView.setMovementMethod(new ScrollingMovementMethod());
        Intent intent = getIntent();
        TradeItCryptoQuoteResponseParcelable cryptoQuoteResponseParcelable = intent
                .getParcelableExtra(GET_CRYPTO_QUOTE_PARAMETER);
        textView.setText(cryptoQuoteResponseParcelable.toString());
    }

}
