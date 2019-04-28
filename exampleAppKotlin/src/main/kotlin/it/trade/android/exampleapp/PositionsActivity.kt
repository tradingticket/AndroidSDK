package it.trade.android.exampleapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.ScrollingMovementMethod
import android.widget.TextView
import it.trade.android.sdk.model.TradeItLinkedBrokerAccountParcelable
import it.trade.android.sdk.model.TradeItPositionParcelable
import it.trade.model.TradeItErrorResult
import it.trade.model.callback.TradeItCallback


class PositionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_positions)
        val textView = this.findViewById(R.id.positions_textview) as TextView?
        textView!!.movementMethod = ScrollingMovementMethod()
        val intent = intent
        val positions = intent.getParcelableArrayListExtra<TradeItPositionParcelable>(MainActivity.POSITIONS_PARAMETER)
        val linkedBrokerAccount = intent.getParcelableExtra<TradeItLinkedBrokerAccountParcelable>(MainActivity.PARCELED_ACCOUNT_PARAMETER)
        textView.text = positions.toString()
        val position = positions.find { it.isProxyVoteEligible }
        position?.let {
            linkedBrokerAccount.getProxyVoteUrl(it.symbol, object : TradeItCallback<String> {
                override fun onSuccess(proxyVoteUrl: String) {
                    textView.text = "Proxyvote url for last position: " + proxyVoteUrl
                }

                override fun onError(error: TradeItErrorResult?) {
                    textView.text = "Error getting Proxyvote url for last position: " + error
                }

            })
        }
    }
}
