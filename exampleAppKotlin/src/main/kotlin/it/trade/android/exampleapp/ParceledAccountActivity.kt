package it.trade.android.exampleapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.TextView
import it.trade.android.sdk.TradeItSDK
import it.trade.android.sdk.model.TradeItLinkedBrokerAccountParcelable
import it.trade.android.sdk.model.TradeItPositionParcelable
import it.trade.model.TradeItErrorResult
import it.trade.model.TradeItSecurityQuestion
import it.trade.model.callback.TradeItCallback
import it.trade.model.callback.TradeItCallbackWithSecurityQuestionImpl

class ParceledAccountActivity : AppCompatActivity() {
    private lateinit var textView: TextView
    private lateinit var linkedBrokerAccount: TradeItLinkedBrokerAccountParcelable
    private var originalLinkedBrokerAccount: TradeItLinkedBrokerAccountParcelable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parceled_account)
        this.textView = this.findViewById(R.id.output_textview) as TextView
        this.textView.movementMethod = ScrollingMovementMethod()
        val intent = intent
        linkedBrokerAccount = intent.getParcelableExtra(MainActivity.PARCELED_ACCOUNT_PARAMETER)
        originalLinkedBrokerAccount = TradeItSDK
                .linkedBrokerManager
                .getLinkedBrokerByUserId(linkedBrokerAccount.linkedBroker?.linkedLogin?.userId!!)!!
                .getLinkedBrokerAccount(linkedBrokerAccount.accountNumber)

        val message = ("Parceled LinkedBrokerAccount(@"
                + System.identityHashCode(linkedBrokerAccount)
                + "):\n"
                + linkedBrokerAccount.toString()
                + "\n\nLinkedBrokerManager->LinkedBrokerAccount(@"
                + System.identityHashCode(originalLinkedBrokerAccount)
                + "):\n"
                + originalLinkedBrokerAccount!!.toString())
        textView.text = message
    }

    fun getBalanceTapped(view: View) {
        this.textView.text = "Fetching balances..."

        linkedBrokerAccount.refreshBalance(object : TradeItCallback<TradeItLinkedBrokerAccountParcelable> {
            override fun onSuccess(linkedBrokerAccountParcelable: TradeItLinkedBrokerAccountParcelable) {
                val message = ("Parceled LinkedBrokerAccount(@"
                        + System.identityHashCode(linkedBrokerAccountParcelable)
                        + "):\n"
                        + linkedBrokerAccountParcelable.toString()
                        + "\n\n==========\n\n"
                        + "LinkedBrokerManager->LinkedBrokerAccount(@"
                        + System.identityHashCode(originalLinkedBrokerAccount)
                        + "):\n"
                        + originalLinkedBrokerAccount!!.toString())
                textView.text = message
            }

            override fun onError(error: TradeItErrorResult) {
                textView.text = "Error refreshing balances: $error"
            }
        })
    }

    fun getPortfolioTapped(view: View) {
        this.textView.text = "Fetching portfolio..."

        linkedBrokerAccount.refreshPositions(object : TradeItCallback<List<TradeItPositionParcelable>> {
            override fun onSuccess(positions: List<TradeItPositionParcelable>) {
                val message = ("Parceled LinkedBrokerAccount.positions(@"
                        + System.identityHashCode(linkedBrokerAccount)
                        + "):\n"
                        + linkedBrokerAccount.positions.toString()
                        + "\n\n==========\n\n"
                        + "LinkedBrokerManager->LinkedBrokerAccount.positions(@"
                        + System.identityHashCode(originalLinkedBrokerAccount)
                        + "):\n"
                        + originalLinkedBrokerAccount?.positions.toString())
                textView.text = message
            }

            override fun onError(error: TradeItErrorResult) {
                textView.text = "Error refreshing portfolio: $error"
            }
        })
    }

    fun authenticate(view: View) {
        linkedBrokerAccount.linkedBroker?.authenticate(object : TradeItCallbackWithSecurityQuestionImpl<List<TradeItLinkedBrokerAccountParcelable>>() {
            override fun onSecurityQuestion(securityQuestion: TradeItSecurityQuestion) {

            }

            override fun onSuccess(accounts: List<TradeItLinkedBrokerAccountParcelable>) {
                val message = ("Parceled LinkedBrokerAccount(@"
                        + System.identityHashCode(linkedBrokerAccount)
                        + "):\n"
                        + linkedBrokerAccount.toString()
                        + "\n\n==========\n\n"
                        + "LinkedBrokerManager->LinkedBrokerAccount(@"
                        + System.identityHashCode(accounts[0])
                        + "):\n"
                        + accounts[0].toString())
                textView.text = message
            }

            override fun onError(error: TradeItErrorResult) {
                textView.text = "Error authenticating: $error"
            }
        })
    }
}
