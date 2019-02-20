package it.trade.android.exampleapp

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import it.trade.android.sdk.TradeItConfigurationBuilder
import it.trade.android.sdk.TradeItSDK
import it.trade.android.sdk.enums.TradeItOrderAction
import it.trade.android.sdk.enums.TradeItOrderExpirationType
import it.trade.android.sdk.enums.TradeItOrderPriceType
import it.trade.android.sdk.enums.TradeItOrderQuantityType
import it.trade.android.sdk.exceptions.TradeItDeleteLinkedLoginException
import it.trade.android.sdk.exceptions.TradeItSaveLinkedLoginException
import it.trade.android.sdk.manager.TradeItLinkedBrokerManager
import it.trade.android.sdk.model.*
import it.trade.android.sdk.model.orderstatus.TradeItOrderStatusParcelable
import it.trade.model.TradeItErrorResult
import it.trade.model.TradeItSecurityQuestion
import it.trade.model.callback.TradeItCallback
import it.trade.model.callback.TradeItCallbackWithSecurityQuestionImpl
import it.trade.model.reponse.Instrument
import it.trade.model.reponse.TradeItResponse
import it.trade.model.request.TradeItEnvironment
import kotlinx.android.synthetic.main.activity_main.*
import java.math.BigDecimal
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var linkedBrokerManager: TradeItLinkedBrokerManager

    private var rowListener: View.OnClickListener = View.OnClickListener { view ->
        val action = MainActivityActions.values()[view.id]
        when (action) {
            MainActivity.MainActivityActions.GET_ALL_FEATURED_BROKERS,
            MainActivity.MainActivityActions.GET_ALL_NON_FEATURED_BROKERS,
            MainActivity.MainActivityActions.GET_FEATURED_EQUITY_BROKERS,
            MainActivity.MainActivityActions.GET_NON_FEATURED_EQUITY_BROKERS ->
                goToGetBrokersListActivity(action)
            MainActivity.MainActivityActions.OAUTH_LINKED_A_BROKER -> {
                Log.d(TAG, "Link a broker tapped!")
                val intentOauth = Intent(view.context, OauthLinkBrokerActivity::class.java)
                startActivity(intentOauth)
            }
            MainActivity.MainActivityActions.RELINK_FIRST_LINK__BROKER -> {
                Log.d(TAG, "Relink first linked broker was tapped!")
                relinkFirstLinkedBroker()
            }
            MainActivity.MainActivityActions.GET_LINKED_BROKERS -> {
                Log.d(TAG, "Get linked brokers tapped!")
                goToLinkedBrokersActivity()
            }
            MainActivity.MainActivityActions.DELETE_ALL_LINKED_BROKERS -> {
                Log.d(TAG, "Delete all linked brokers tapped!")
                deleteLinkedBrokers()
            }
            MainActivity.MainActivityActions.AUTHENTICATE_FIRST_LINKED_BROKER -> {
                Log.d(TAG, "Authenticate first linked broker tapped!")
                authenticateFirstLinkedBroker(0)
            }
            MainActivity.MainActivityActions.AUTHENTICATE_ALL_LINKED_BROKERS -> {
                Log.d(TAG, "Authenticate all linked brokers tapped!")
                authenticateAllLinkedBrokers()
            }
            MainActivity.MainActivityActions.REFRESH_ALL_BALANCES_FIRST_LINKED_BROKER -> {
                Log.d(TAG, "Refresh all balances for first linked broker was tapped!")
                refreshAllBalancesFirstLinkedBroker()
            }
            MainActivity.MainActivityActions.REFRESH_ALL_BALANCES_ALL_LINKED_BROKERS -> {
                Log.d(TAG, "refresh balances for all linked brokers was tapped!")
                refreshBalancesForAllLinkedBrokers()
            }
            MainActivity.MainActivityActions.PARCEL_FIRST_LINKED_BROKER_ACCOUNT -> {
                Log.d(TAG, "parcel first linked broker account was tapped!")
                parcelFirstLinkedBrokerAccount()
            }
            MainActivity.MainActivityActions.GET_POSITIONS_FIRST_LINKED_BROKER_ACCOUNT -> {
                getPositionsFirstLinkedBroker()
                Log.d(TAG, "get positions first linked broker was tapped!")
            }
            MainActivity.MainActivityActions.REFRESH_ORDERS_STATUS_FIRST_LINKED_BROKER_ACCOUNT -> {
                getOrdersStatusFirstLinkedBroker()
                Log.d(TAG, "refresh orders status first linked broker was tapped!")
            }
            MainActivity.MainActivityActions.PREVIEW_TRADE_FIRST_LINKED_BROKER_ACCOUNT -> {
                Log.d(TAG, "preview trade first linked broker account was tapped!")
                previewTradeFirstLinkedBroker()
            }
            MainActivity.MainActivityActions.PREVIEW_CRYPTO_TRADE_FIRST_CRYPTO_BROKER_ACCOUNT -> {
                Log.d(TAG, "preview trade first crypto broker account was tapped!")
                previewCryptoTradeFirstCryptoBrokerAccount()
            }
            MainActivity.MainActivityActions.GET_CRYPTO_QUOTE_FIRST_CRYPTO_BROKER_ACCOUNT -> {
                Log.d(TAG, "getCryptoQuote first crypto broker account was tapped!")
                getCryptoQuoteFirstCryptoBrokerAccount()
            }
            MainActivity.MainActivityActions.SYNC_LOCAL_LINKED_BROKERS -> {
                Log.d(TAG, "synch local linked brokers was tapped!")
                syncLocalLinkedBrokers()
            }
            else -> Log.e(TAG, "ERROR: no action found for id " + view.id)
        }
    }

    enum class MainActivityActions(val label: String) {
        GET_ALL_NON_FEATURED_BROKERS("Get All Non Featured Brokers"),
        GET_ALL_FEATURED_BROKERS("Get All Featured Brokers"),
        GET_FEATURED_EQUITY_BROKERS("Get Featured Equity  Brokers"),
        GET_NON_FEATURED_EQUITY_BROKERS("Get Non Featured Equity Brokers"),
        SYNC_LOCAL_LINKED_BROKERS("Sync Local Linked Brokers"),
        OAUTH_LINKED_A_BROKER("Link a broker via the oAuth flow"),
        RELINK_FIRST_LINK__BROKER("Relink first link broker via the oAuth flow"),
        GET_LINKED_BROKERS("Get current linked brokers"),
        DELETE_ALL_LINKED_BROKERS("Delete all linked brokers"),
        AUTHENTICATE_FIRST_LINKED_BROKER("Authenticate first linked broker"),
        AUTHENTICATE_ALL_LINKED_BROKERS("Authenticate all linked brokers"),
//        AUTHENTICATE_WITH_SECURITY_QUESTION_SIMPLE("Simple security question"),
//        AUTHENTICATE_WITH_SECURITY_QUESTION_OPTIONS("Security question with options"),
        REFRESH_ALL_BALANCES_ALL_LINKED_BROKERS("Refresh all balances for all linked brokers"),
        REFRESH_ALL_BALANCES_FIRST_LINKED_BROKER("Refresh all balances for first linked broker"),
        PARCEL_FIRST_LINKED_BROKER_ACCOUNT("Parcel first linked broker account"),
        GET_POSITIONS_FIRST_LINKED_BROKER_ACCOUNT("Get positions for first linked broker account"),
        REFRESH_ORDERS_STATUS_FIRST_LINKED_BROKER_ACCOUNT("Refresh orders status for first linked broker account"),
        PREVIEW_TRADE_FIRST_LINKED_BROKER_ACCOUNT("Preview trade for first linked broker account"),
        PREVIEW_CRYPTO_TRADE_FIRST_CRYPTO_BROKER_ACCOUNT("Preview crypto trade for first crypto broker account"),
        GET_CRYPTO_QUOTE_FIRST_CRYPTO_BROKER_ACCOUNT("Get crypto quote for first crypto broker account")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initTable()

        val configurationBuilder = TradeItConfigurationBuilder(
                this.applicationContext,
                "test-all-brokers",
                TradeItEnvironment.QA
        )

        TradeItSDK.configure(configurationBuilder)

        linkedBrokerManager = TradeItSDK.linkedBrokerManager
    }

    private fun initTable() {
        val tv = TextView(this).apply {
            val headerText = "TradeIt ExampleApp"
            layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
            )
            gravity = Gravity.CENTER
            textSize = 18f
            text = headerText
        }
        val rowHeader = TableRow(this).apply {
            setBackgroundColor(Color.parseColor("#c0c0c0"))
            layoutParams = TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT
            )
            addView(tv)
        }

        val tableLayout = tableLayoutId
        tableLayout.addView(rowHeader)

        for (action in MainActivityActions.values()) {
            addRow(tableLayout, action.label, action.ordinal)
        }
    }

    private fun addRow(tableLayout: TableLayout, label: String, id: Int) {
        val textView = TextView(this).apply {
            setPadding(10, 10, 10, 10)
            text = label
        }
        val row = TableRow(this).apply {
            this.id = id
            layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT
            )
            setBackgroundResource(R.drawable.row_border)
            addView(textView)
            isClickable = true
            setOnClickListener(rowListener)
        }
        tableLayout.addView(row)
    }

    private fun showAlert(title: String, message: String) {
        AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    // do nothing
                }
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
    }

    private fun showSecurityQuestion(
            securityQuestion: TradeItSecurityQuestion,
            editText: EditText,
            onSubmitListener: DialogInterface.OnClickListener,
            onCancelListener: DialogInterface.OnClickListener
    ) {
        val message = if (!securityQuestion.securityQuestionOptions.isEmpty())
            TextUtils.join("\n", securityQuestion.securityQuestionOptions)
        else
            ""

        AlertDialog.Builder(this)
                .setTitle(securityQuestion.securityQuestion)
                .setMessage(message)
                .setView(editText)
                .setPositiveButton(android.R.string.ok, onSubmitListener)
                .setNegativeButton(android.R.string.cancel, onCancelListener)
                .show()
    }

    private fun deleteLinkedBrokers() {
        val linkedBrokersToDelete = linkedBrokerManager.linkedBrokers
        if (linkedBrokersToDelete.isEmpty()) {
            val message = "No linked brokers to delete!: " + linkedBrokersToDelete.size
            showAlert("Delete linked brokers", message)
        } else {
            Log.d(TAG, "# of linkedBrokers before deletion: " + linkedBrokersToDelete.size)

            for (linkedBroker in ArrayList(linkedBrokersToDelete)) {
                linkedBrokerManager.unlinkBroker(linkedBroker, object : TradeItCallback<TradeItResponse> {
                    override fun onSuccess(type: TradeItResponse) {
                        Log.d(TAG, "# of linkedBrokers after deletion: " + linkedBrokerManager.linkedBrokers.size)
                    }

                    override fun onError(error: TradeItErrorResult) {
                        Log.e(TAG, "An error happened unlinking linkedBroker: $linkedBroker\nError: $error")
                    }
                })
            }
            //TODO: see how to wait for all calls to be finished before calling the alert
            showAlert("Delete linked brokers", "# of linkedBrokers after deletion: " + linkedBrokerManager.linkedBrokers.size)
        }
    }

    private fun authenticateAllLinkedBrokers() {
        val mainActivity = this
        linkedBrokerManager.authenticateAll(
                object : TradeItCallbackWithSecurityQuestionAndCompletion {
                    override fun onFinished() {
                        Log.d(TAG, "authenticateAllLinkedBrokers - onFinished")
                        goToLinkedBrokersActivity()
                    }

                    override fun onSecurityQuestion(
                            securityQuestion: TradeItSecurityQuestion,
                            callback: TradeItCallbackWithSecurityQuestionImpl<*>
                    ) {
                        val input = EditText(mainActivity)
                        showSecurityQuestion(
                                securityQuestion,
                                input,
                                DialogInterface.OnClickListener { _, _ -> callback.submitSecurityAnswer(input.text.toString()) },
                                DialogInterface.OnClickListener { _, _ -> callback.cancelSecurityQuestion() }
                        )
                    }
                }
        )
    }

    private fun authenticateFirstLinkedBroker(index: Int) {
        val linkedBrokers = linkedBrokerManager.linkedBrokers
        if (linkedBrokers.isEmpty() || linkedBrokers.size < index + 1) {
            val message = "No linked broker to authenticate! Index: " + index + ", linked broker count: " + linkedBrokers.size
            showAlert("Authenticate linked broker", message)
        } else {
            val linkedBroker = linkedBrokers[index]
            val mainActivity = this
            linkedBroker.authenticate(
                    object : TradeItCallbackWithSecurityQuestionImpl<List<TradeItLinkedBrokerAccountParcelable>>() {
                        override fun onSuccess(accounts: List<TradeItLinkedBrokerAccountParcelable>) {
                            goToLinkedBrokersActivity()
                        }

                        override fun onSecurityQuestion(securityQuestion: TradeItSecurityQuestion) {
                            val input = EditText(mainActivity)
                            val securityQuestionImpl = this
                            showSecurityQuestion(
                                    securityQuestion,
                                    input,
                                    DialogInterface.OnClickListener { _, _ -> securityQuestionImpl.submitSecurityAnswer(input.text.toString()) },
                                    DialogInterface.OnClickListener { _, _ -> securityQuestionImpl.cancelSecurityQuestion() }
                            )
                        }

                        override fun onError(error: TradeItErrorResult) {
                            showAlert("authenticateFirstLinkedBroker", "Error authenticating: $error")
                        }
                    }
            )
        }
    }

    private fun parcelFirstLinkedBrokerAccount() {
        val mainActivity = this
        val linkedBrokers = linkedBrokerManager.linkedBrokers

        when {
            linkedBrokers.isEmpty() -> showAlert("getBalancesFirstLinkedBroker", "No linked broker!")
            linkedBrokers[0].accounts.isEmpty() -> showAlert("getBalancesFirstLinkedBroker", "No linked broker accounts detected for first linked broker! Try authenticating.")
            else -> {
                val linkedBroker = linkedBrokers[0]
                val linkedBrokerAccountParcelable = linkedBroker.accounts[0]
                val intent = Intent(mainActivity, ParceledAccountActivity::class.java)
                intent.putExtra(PARCELED_ACCOUNT_PARAMETER, linkedBrokerAccountParcelable)
                startActivity(intent)
            }
        }
    }

    private fun refreshAllBalancesFirstLinkedBroker() {
        val linkedBrokers = linkedBrokerManager.linkedBrokers

        when {
            linkedBrokers.isEmpty() -> showAlert("refreshAllBalancesFirstLinkedBroker", "No linked broker!")
            linkedBrokers[0].accounts.isEmpty() -> showAlert("refreshAllBalancesFirstLinkedBroker", "No linked broker accounts detected for first linked broker! Try authenticating.")
            else -> {
                val linkedBroker = linkedBrokers[0]
                linkedBroker.refreshAccountBalances(object: TradeItCallBackCompletion {
                    override fun onFinished() {
                        goToLinkedBrokerAccountsActivity(linkedBroker.accounts)
                    }
                })
            }
        }
    }

    private fun refreshBalancesForAllLinkedBrokers() {
        val linkedBrokers = linkedBrokerManager.linkedBrokers

        when {
            linkedBrokers.isEmpty() -> showAlert("refreshAllBalancesFirstLinkedBroker", "No linked broker!")
            linkedBrokers[0].accounts.isEmpty() -> showAlert("refreshAllBalancesFirstLinkedBroker", "No linked broker accounts detected for first linked broker! Try authenticating.")
            else -> linkedBrokerManager.refreshAccountBalances(object: TradeItCallBackCompletion {
                override fun onFinished() {
                    goToLinkedBrokersActivity()
                }
            })
        }
    }

    private fun getPositionsFirstLinkedBroker() {
        val mainActivity = this
        val linkedBrokers = linkedBrokerManager.linkedBrokers
        when {
            linkedBrokers.isEmpty() -> showAlert("getPositionsFirstLinkedBroker", "No linked broker!")
            linkedBrokers[0].accounts.isEmpty() -> showAlert("getPositionsFirstLinkedBroker", "No linked broker accounts detected for first linked broker! Try authenticating.")
            else -> {
                val linkedBroker = linkedBrokers[0]
                for (linkedBrokerAccount in linkedBroker.accounts) {
                    linkedBrokerAccount.refreshPositions(
                            object : TradeItCallback<List<TradeItPositionParcelable>> {
                                override fun onSuccess(positions: List<TradeItPositionParcelable>) {
                                    val intent = Intent(mainActivity, PositionsActivity::class.java)
                                    intent.putExtra(PARCELED_ACCOUNT_PARAMETER, linkedBrokerAccount)
                                    intent.putParcelableArrayListExtra(POSITIONS_PARAMETER, positions as ArrayList<out Parcelable>)
                                    startActivity(intent)
                                }

                                override fun onError(error: TradeItErrorResult) {
                                    showAlert("getPositionsFirstLinkedBroker", "Error refreshing positions: $error")
                                }
                            }
                    )
                }
            }
        }
    }

    private fun getOrdersStatusFirstLinkedBroker() {
        val mainActivity = this
        val linkedBrokers = linkedBrokerManager.linkedBrokers
        when {
            linkedBrokers.isEmpty() -> showAlert("getOrdersStatusFirstLinkedBroker", "No linked broker!")
            linkedBrokers[0].accounts.isEmpty() -> showAlert("getOrdersStatusFirstLinkedBroker", "No linked broker accounts detected for first linked broker! Try authenticating.")
            else -> {
                val linkedBroker = linkedBrokers[0]
                for (linkedBrokerAccount in linkedBroker.accounts) {
                    linkedBrokerAccount.refreshOrdersStatus(
                            object : TradeItCallback<List<TradeItOrderStatusParcelable>> {
                                override fun onSuccess(orderStatusDetailsList: List<TradeItOrderStatusParcelable>) {
                                    val intent = Intent(mainActivity, OrdersStatusActivity::class.java)
                                    intent.putParcelableArrayListExtra(ORDERS_STATUS_PARAMETER, orderStatusDetailsList as ArrayList<out Parcelable>)
                                    startActivity(intent)
                                }

                                override fun onError(error: TradeItErrorResult) {
                                    showAlert("getOrdersStatusFirstLinkedBroker", "Error refreshing orders status: $error")
                                }
                            }
                    )
                }
            }
        }
    }

    private fun previewTradeFirstLinkedBroker() {
        val mainActivity = this
        val linkedBrokers = linkedBrokerManager.linkedBrokers
        when {
            linkedBrokers.isEmpty() -> showAlert("previewTradeFirstLinkedBroker", "No linked broker!")
            linkedBrokers[0].accounts.isEmpty() -> showAlert("previewTradeFirstLinkedBroker", "No linked broker accounts detected for first linked broker! Try authenticating.")
            else -> {
                val broker = linkedBrokers.first()
                val symbol = if (broker.brokerName == "kabu") "8703" else "GE"
                val order = TradeItOrderParcelable(linkedBrokers[0].accounts[0], symbol)
                order.limitPrice = 20.0
                order.expiration = TradeItOrderExpirationType.GOOD_FOR_DAY
                order.priceType = TradeItOrderPriceType.LIMIT
                val intent = Intent(mainActivity, PreviewOrderActivity::class.java)
                intent.putExtra(PREVIEW_ORDER_PARAMETER, order)
                startActivity(intent)
            }
        }
    }

    private fun previewCryptoTradeFirstCryptoBrokerAccount() {
        val mainActivity = this
        val linkedBrokers = linkedBrokerManager.linkedBrokers
        if (linkedBrokers.isEmpty()) {
            showAlert("previewTradeFirstLinkedBroker", "No linked broker!")
            return
        }
        val cryptoAccount = getFirstCryptoAccount(linkedBrokers)
        if (cryptoAccount == null) {
            showAlert("previewTradeFirstLinkedBroker", "No crypto account!")
            return
        }
        val cryptoOrderParcelable = TradeItCryptoOrderParcelable(
            cryptoAccount,
            SymbolPairParcelable("BTC", "USD"),
            TradeItOrderAction.BUY
        )
        cryptoOrderParcelable.priceType = TradeItOrderPriceType.LIMIT
        cryptoOrderParcelable.expiration = TradeItOrderExpirationType.GOOD_FOR_DAY
        cryptoOrderParcelable.limitPrice = BigDecimal(2000.0)
        cryptoOrderParcelable.orderQuantityType = TradeItOrderQuantityType.QUOTE_CURRENCY
        cryptoOrderParcelable.quantity = BigDecimal(1.0)
        val intent = Intent(mainActivity, PreviewCryptoOrderActivity::class.java)
        intent.putExtra(PREVIEW_ORDER_PARAMETER, cryptoOrderParcelable)
        startActivity(intent)
    }

    private fun getCryptoQuoteFirstCryptoBrokerAccount() {
        val mainActivity = this
        val linkedBrokers = linkedBrokerManager.linkedBrokers
        if (linkedBrokers.isEmpty()) {
            showAlert("getCryptoQuoteFirstCryptoBrokerAccount", "No linked broker!")
            return
        }
        val cryptoAccount = getFirstCryptoAccount(linkedBrokers)
        if (cryptoAccount == null) {
            showAlert("getCryptoQuoteFirstCryptoBrokerAccount", "No crypto account!")
            return
        }
        cryptoAccount.getCryptoQuote("BTC/USD", object : TradeItCallback<TradeItCryptoQuoteResponseParcelable> {
            override fun onSuccess(tradeItCryptoQuoteResponseParcelable: TradeItCryptoQuoteResponseParcelable) {
                val intent = Intent(mainActivity, GetCryptoQuoteActivity::class.java)
                intent.putExtra(GET_CRYPTO_QUOTE_PARAMETER, tradeItCryptoQuoteResponseParcelable)
                startActivity(intent)
            }

            override fun onError(tradeItErrorResult: TradeItErrorResult) {
                showAlert(tradeItErrorResult.shortMessage,
                    tradeItErrorResult.longMessages.joinToString()
                )
            }
        })
    }

    private fun getFirstCryptoAccount(
        linkedBrokers: List<TradeItLinkedBrokerParcelable>
    ): TradeItLinkedBrokerAccountParcelable? {
        return linkedBrokers.mapNotNull { linkedBroker ->
            linkedBroker.accounts.find {
                it.getOrderCapabilityForInstrument(Instrument.CRYPTO) != null
            }
        }.firstOrNull()
    }

    private fun goToLinkedBrokersActivity() {
        val intent = Intent(this.applicationContext, LinkedBrokersActivity::class.java)
        intent.putParcelableArrayListExtra(LINKED_BROKERS_PARAMETER, linkedBrokerManager.linkedBrokers as ArrayList<out Parcelable>)
        startActivity(intent)
    }

    private fun goToLinkedBrokerAccountsActivity(accounts: List<TradeItLinkedBrokerAccountParcelable>) {
        val intent = Intent(this.applicationContext, LinkedBrokerAccountsActivity::class.java)
        intent.putParcelableArrayListExtra(LINKED_BROKER_ACCOUNTS_PARAMETER, accounts as ArrayList<out Parcelable>)
        startActivity(intent)
    }

    private fun goToGetBrokersListActivity(action: MainActivity.MainActivityActions) {
        val intent = Intent(this.applicationContext, BrokersListActivity::class.java)
        intent.putExtra(GET_BROKERS_LIST_PARAMETER, action)
        startActivity(intent)
    }

    private fun syncLocalLinkedBrokers() {
        val linkedBrokerData1 = TradeItLinkedBrokerData(
                "dummy",
                "8fa14999720337719675",
                "XZZt9cfIz9APLljOPeKhFjOuz5mSa1E9Q5Un%2Fc1ARlaD4wQixu6S%2BUIQ6rOhiUDV1RJM0stg7EqVslOH5oxGYHBvdLrKqNoi%2BdRzGscDF3nNbzBR3QJMV5SxsgyEkaLrmFETBZUiaRcfKSR6kvLznA%3D%3D"
        ).withLinkActivationPending(true)
        linkedBrokerData1.injectAccount(TradeItLinkedBrokerAccountData("MyAccountName", "MyAccountNumber", "USD"))

        val linkedBrokerData2 = TradeItLinkedBrokerData(
                "dummyFx",
                "3741499971984583d2f1",
                "ecwzVqxPiTtgalvlgPQOofmaxc%2BVj1JWnl8UfTwnXlMS8lQgNJ8zevAWAR1hcflBkyJ0V%2FWCuxvQdCe1vowLOcX7Hj9vpADuQfuBppFo1faGCV7q9UEjr0J4F8OhlFhgL2SwRLRz0uD411DokfX86g%3D%3D"
        )

        try {
            linkedBrokerManager.syncLocalLinkedBrokers(Arrays.asList(linkedBrokerData1, linkedBrokerData2))
            goToLinkedBrokersActivity()
        } catch (e: TradeItSaveLinkedLoginException) {
            Log.e(TAG, e.message, e)
        } catch (e: TradeItDeleteLinkedLoginException) {
            Log.e(TAG, e.message, e)
        }

    }

    private fun relinkFirstLinkedBroker() {
        val linkedBrokers = linkedBrokerManager.linkedBrokers
        if (linkedBrokers.isEmpty()) {
            showAlert("relinkFirstLinkedBroker", "No linked broker!")
        } else {
            val intentRelinkOauth = Intent(this, OauthLinkBrokerActivity::class.java)
            intentRelinkOauth.putExtra(RELINK_OAUTH_PARAMETER, linkedBrokerManager.linkedBrokers[0].linkedLogin?.userId)
            startActivity(intentRelinkOauth)
        }
    }

    companion object {
        private val TAG = MainActivity::class.java.name

        const val GET_BROKERS_LIST_PARAMETER = "it.trade.android.exampleapp.AVAILABLE_BROKERS"
        const val LINKED_BROKERS_PARAMETER = "it.trade.android.exampleapp.LINKED_BROKERS"
        const val LINKED_BROKER_ACCOUNTS_PARAMETER = "it.trade.android.exampleapp.LINKED_BROKER_ACCOUNTS"
        const val PARCELED_ACCOUNT_PARAMETER = "it.trade.android.exampleapp.PARCELED_ACCOUNT"
        const val POSITIONS_PARAMETER = "it.trade.android.exampleapp.POSITIONS"
        const val ORDERS_STATUS_PARAMETER = "it.trade.android.exampleapp.ORDERS_STATUS"
        const val PREVIEW_ORDER_PARAMETER = "it.trade.android.exampleapp.PREVIEW_ORDER"
        const val RELINK_OAUTH_PARAMETER = "it.trade.android.exampleapp.RELINK_OAUTH"
        const val GET_CRYPTO_QUOTE_PARAMETER = "it.trade.android.exampleapp.GET_CRYPTO_QUOTE"
    }
}
