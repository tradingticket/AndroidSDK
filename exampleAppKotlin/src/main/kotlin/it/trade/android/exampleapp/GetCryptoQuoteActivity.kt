package it.trade.android.exampleapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.ScrollingMovementMethod
import android.widget.TextView
import it.trade.android.sdk.model.TradeItCryptoQuoteResponseParcelable

class GetCryptoQuoteActivity : AppCompatActivity() {

    private var textView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_crypto_quote)

        textView = this.findViewById(R.id.get_crypto_quote_textview) as TextView
        textView!!.movementMethod = ScrollingMovementMethod()
        val intent = intent
        val cryptoQuoteResponseParcelable = intent
            .getParcelableExtra(MainActivity.GET_CRYPTO_QUOTE_PARAMETER) as TradeItCryptoQuoteResponseParcelable
        textView!!.setText(cryptoQuoteResponseParcelable.toString())
    }

}
