package it.trade.android.exampleapp

import android.app.Activity
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NO_HISTORY
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v7.app.AppCompatActivity
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import it.trade.android.exampleapp.adapters.BrokerAdapter
import it.trade.android.exampleapp.customtabs.CustomTabActivityHelper
import it.trade.android.sdk.TradeItSDK
import it.trade.android.sdk.model.TradeItLinkedBrokerParcelable
import it.trade.model.TradeItErrorResult
import it.trade.model.callback.TradeItCallback
import it.trade.model.reponse.TradeItAvailableBrokersResponse.Broker

class OauthLinkBrokerActivity : AppCompatActivity(), CustomTabActivityHelper.ConnectionCallback {
    private var linkedBrokerManager = TradeItSDK.getLinkedBrokerManager()
    private lateinit var oAuthResultTextView: TextView
    private lateinit var brokersSpinner: Spinner
    private lateinit var customTabActivityHelper: CustomTabActivityHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_oauth_link_broker)

        customTabActivityHelper = CustomTabActivityHelper()
        customTabActivityHelper.setConnectionCallback(this)

        oAuthResultTextView = this.findViewById(R.id.oAuthTextViewResult) as TextView
        oAuthResultTextView.movementMethod = ScrollingMovementMethod()
        val linkBrokerButton = this.findViewById(R.id.button_test_oauth) as Button?
        brokersSpinner = this.findViewById(R.id.brokers_spinner) as Spinner
        val oauthLinkBrokerActivity = this

        val intent = intent
        val userId = intent.getStringExtra(MainActivity.RELINK_OAUTH_PARAMETER)

        linkedBrokerManager.getAvailableBrokers(object : TradeItCallback<List<Broker>> {
            override fun onSuccess(brokersList: List<Broker>) {
                linkBrokerButton!!.isEnabled = true
                oAuthResultTextView.text = "Brokers available: $brokersList\n"
                val adapter = BrokerAdapter(oauthLinkBrokerActivity, brokersList)
                brokersSpinner.adapter = adapter
                if (userId != null) {
                    relinkOauthFlow(userId)
                }

            }

            override fun onError(error: TradeItErrorResult) {
                linkBrokerButton!!.isEnabled = false
                oAuthResultTextView.text = "Error getting the brokers: $error\n"
            }
        })
    }

    override fun onStart() {
        super.onStart()
        customTabActivityHelper!!.bindCustomTabsService(this)
    }

    override fun onStop() {
        super.onStop()
        customTabActivityHelper!!.unbindCustomTabsService(this)
    }

    override fun onResume() {
        super.onResume()
        val intent = intent
        if (intent != null && intent.data != null) {
            val oAuthVerifier = intent.data!!.getQueryParameter("oAuthVerifier")
            if (oAuthVerifier != null) {
                linkedBrokerManager.linkBrokerWithOauthVerifier("MyAccountLabel", oAuthVerifier, object : TradeItCallback<TradeItLinkedBrokerParcelable> {
                    override fun onSuccess(linkedBroker: TradeItLinkedBrokerParcelable) {
                        oAuthResultTextView.text = "oAuthFlow Success: " + linkedBroker.toString() + "\n"
                    }

                    override fun onError(error: TradeItErrorResult) {
                        oAuthResultTextView.text = "linkBrokerWithOauthVerifier Error: $error\n"
                    }
                })
            }
        }
    }

    fun processOauthFlow(view: View) {
        val brokerSelected = brokersSpinner.selectedItem as Broker ?: return
        linkedBrokerManager.getOAuthLoginPopupUrl(brokerSelected.shortName, APP_DEEP_LINK, object : TradeItCallback<String> {
            override fun onSuccess(oAuthUrl: String) {
                launchCustomTab(oAuthUrl)
            }

            override fun onError(error: TradeItErrorResult) {
                oAuthResultTextView.text = "getOAuthLoginPopupUrl Error: $error\n"
            }
        })
    }

    fun relinkOauthFlow(userId: String?) {
        linkedBrokerManager.getOAuthLoginPopupForTokenUpdateUrlByUserId(userId, APP_DEEP_LINK, object : TradeItCallback<String> {
            override fun onSuccess(oAuthUrl: String) {
                launchCustomTab(oAuthUrl)
            }

            override fun onError(error: TradeItErrorResult) {
                oAuthResultTextView.text = "getOAuthLoginPopupForTokenUpdateUrlByUserId Error: $error\n"
            }
        })
    }

    private fun launchCustomTab(url: String) {
        val builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(resources.getColor(R.color.colorPrimary))

        val customTabsIntent = builder.build()
        val uri = Uri.parse(url)
        customTabsIntent.intent.addFlags(FLAG_ACTIVITY_NO_HISTORY)
        CustomTabActivityHelper.openCustomTab(this, customTabsIntent, uri, object : CustomTabActivityHelper.CustomTabFallback {
            //fallback if custom Tabs is not available
            override fun openUri(activity: Activity, uri: Uri) {
                val intent = Intent(Intent.ACTION_VIEW, uri)
                activity.startActivity(intent)
            }

        })
    }

    override fun onCustomTabsConnected() {}

    override fun onCustomTabsDisconnected() {}

    companion object {

        val APP_DEEP_LINK = "exampleapp://tradeit"
    }
}