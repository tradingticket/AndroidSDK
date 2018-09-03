package it.trade.android.exampleapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import it.trade.android.sdk.model.TradeItLinkedBrokerAccountParcelable
import it.trade.model.TradeItErrorResult
import it.trade.model.callback.TradeItCallback
import kotlinx.android.synthetic.main.activity_linked_broker_accounts.*

class LinkedBrokerAccountsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_linked_broker_accounts)
        val textView = linked_broker_accounts_textview

        val intent = intent
        val linkedBrokerAccounts = intent.getParcelableArrayListExtra<TradeItLinkedBrokerAccountParcelable>(MainActivity.LINKED_BROKER_ACCOUNTS_PARAMETER)

        if (linkedBrokerAccounts.isEmpty()) {
            textView!!.text = "No linked broker accounts!"
        } else {
            // Refresh balance on the first account just to make sure that an unparcelled account works properly
            textView!!.text = "Refreshing first account balance again just to test..."
            linkedBrokerAccounts[0].refreshBalance(object : TradeItCallback<TradeItLinkedBrokerAccountParcelable> {
                override fun onSuccess(linkedBrokerAccountParcelable: TradeItLinkedBrokerAccountParcelable) {
                    textView.text = "Refreshed first account balance again just to test.\n# of linkedBroker accounts: " + linkedBrokerAccounts.size + "\n\n" + linkedBrokerAccounts.toString()
                }

                override fun onError(error: TradeItErrorResult) {
                    textView.text = "Refresh Balance ERROR:\n$error"
                }
            })
        }

    }
}
