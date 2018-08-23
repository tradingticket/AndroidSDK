package it.trade.android.exampleapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.TextView
import it.trade.android.sdk.model.TradeItLinkedBrokerAccountParcelable
import it.trade.android.sdk.model.TradeItLinkedBrokerParcelable
import it.trade.model.TradeItErrorResult
import it.trade.model.TradeItSecurityQuestion
import it.trade.model.callback.TradeItCallbackWithSecurityQuestionImpl
import kotlinx.android.synthetic.main.activity_linked_brokers.*

class LinkedBrokersActivity : AppCompatActivity() {
    private lateinit var linkedBrokers: List<TradeItLinkedBrokerParcelable>
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_linked_brokers)
        textView = linked_brokers_textview
        textView.movementMethod = ScrollingMovementMethod()

        linkedBrokers = intent.getParcelableArrayListExtra(MainActivity.LINKED_BROKERS_PARAMETER)

        logBrokers()
    }

    fun authenticateFirstBroker(view: View) {
        textView.text = "Authenticating..."

        linkedBrokers[0].authenticate(object : TradeItCallbackWithSecurityQuestionImpl<List<TradeItLinkedBrokerAccountParcelable>>() {
            override fun onSecurityQuestion(securityQuestion: TradeItSecurityQuestion) {
                textView.text = "Security Question:\n$securityQuestion"
            }

            override fun onSuccess(type: List<TradeItLinkedBrokerAccountParcelable>) {
                textView.text = "Authenticate SUCCESS!"
            }

            override fun onError(error: TradeItErrorResult) {
                textView.text = "Authenticate ERROR:\n$error"
            }
        })
    }

    private fun logBrokers() {
        if (linkedBrokers.isEmpty()) {
            textView.text = "No linked brokers!"
        } else {
            val brokers = linkedBrokers.joinToString("\n\n==\n\n") { linkedBroker ->
                val json = "LINKED BROKER: $linkedBroker"
                Log.d("TEST", json)
                json
            }

            textView.text = "=== ${linkedBrokers.size} PARCELED LINKED BROKERS ===\n\n$brokers"
            Log.d("TEST", "==========")
        }
    }
}
