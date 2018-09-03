package it.trade.android.exampleapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.ScrollingMovementMethod
import it.trade.android.exampleapp.adapters.BrokerAdapter
import it.trade.android.sdk.TradeItSDK
import it.trade.model.TradeItErrorResult
import it.trade.model.callback.TradeItCallback
import it.trade.model.reponse.Instrument
import it.trade.model.reponse.TradeItAvailableBrokersResponse.Broker
import kotlinx.android.synthetic.main.activity_brokers_list.*

class BrokersListActivity : AppCompatActivity() {
    private var linkedBrokerManager = TradeItSDK.linkedBrokerManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_brokers_list)
        val brokersSpinner = brokers_spinner
        val textView = brokers_textview
        textView!!.movementMethod = ScrollingMovementMethod()
        val intent = intent
        val action = intent.extras!!.get(MainActivity.GET_BROKERS_LIST_PARAMETER) as MainActivity.MainActivityActions
        val brokersListActivity = this
        when (action) {
            MainActivity.MainActivityActions.GET_ALL_FEATURED_BROKERS -> linkedBrokerManager?.getAllFeaturedBrokers(object : TradeItCallback<List<Broker>> {
                override fun onSuccess(brokersList: List<Broker>) {
                    brokersSpinner!!.adapter = BrokerAdapter(brokersListActivity, brokersList)
                    textView.text = brokersList.toString()
                }

                override fun onError(error: TradeItErrorResult) {
                    textView.text = error.toString()
                }
            })
            MainActivity.MainActivityActions.GET_ALL_NON_FEATURED_BROKERS -> linkedBrokerManager?.getAllNonFeaturedBrokers(object : TradeItCallback<List<Broker>> {
                override fun onSuccess(brokersList: List<Broker>) {
                    brokersSpinner!!.adapter = BrokerAdapter(brokersListActivity, brokersList)
                    textView.text = brokersList.toString()
                }

                override fun onError(error: TradeItErrorResult) {
                    textView.text = error.toString()
                }
            })
            MainActivity.MainActivityActions.GET_FEATURED_EQUITY_BROKERS -> linkedBrokerManager.getFeaturedBrokersForInstrumentType(Instrument.EQUITIES, object : TradeItCallback<List<Broker>> {
                override fun onSuccess(brokersList: List<Broker>) {
                    brokersSpinner!!.adapter = BrokerAdapter(brokersListActivity, brokersList)
                    textView.text = brokersList.toString()
                }

                override fun onError(error: TradeItErrorResult) {
                    textView.text = error.toString()
                }
            })
            MainActivity.MainActivityActions.GET_NON_FEATURED_EQUITY_BROKERS -> linkedBrokerManager.getNonFeaturedBrokersForInstrumentType(Instrument.EQUITIES, object : TradeItCallback<List<Broker>> {
                override fun onSuccess(brokersList: List<Broker>) {
                    brokersSpinner!!.adapter = BrokerAdapter(brokersListActivity, brokersList)
                    textView.text = brokersList.toString()
                }

                override fun onError(error: TradeItErrorResult) {
                    textView.text = error.toString()
                }
            })
            else -> textView.text = "this action is not handled: ($action)"
        }
    }
}
