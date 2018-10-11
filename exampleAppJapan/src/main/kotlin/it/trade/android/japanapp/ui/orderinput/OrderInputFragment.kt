package it.trade.android.japanapp.ui.orderinput

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import it.trade.android.japanapp.R
import kotlinx.android.synthetic.main.order_input_fragment.*

private const val TAG = "OrderInputFragment"

class OrderInputFragment : Fragment() {

    companion object {
        fun newInstance(symbol: String): OrderInputFragment {
            val args = Bundle()
            args.putString("symbol", symbol)
            val fragment = OrderInputFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var viewModel: OrderInputViewModel
    private lateinit var symbol: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        symbol = arguments?.getString("symbol") ?: "8703"
        return inflater.inflate(R.layout.order_input_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!, OrderInputViewModelFactory(symbol)).get(OrderInputViewModel::class.java)
        viewModel.getOrderModel().observe(this, Observer { orderForm ->
            orderForm?.run {
                tvSymbolName.text = symbol.name
                tvSymbol.text = "${symbol.symbol} ${symbol.exchange}"
                tvCurrentTime.text = "13:00"

                tvPrice.text = String.format("%,.0f", symbol.price)
                val change = String.format("%+,.0f", priceChange)
                val percentage = String.format("%+.2f", priceChangePercentage * 100)
                tvPriceChange.text = "$change ($percentage%)"

                tvBuyingPower.text = String.format("%,.0f", buyingPower.availableCash)
                tvNisaLimit.text = String.format("(NISA) %,.0f", buyingPower.availableNisaLimit)

                etQuantity.setText(String.format("%d", orderInfo.quantity))
                etPrice.setText(String.format("%.0f", orderInfo.limitPrice))
                btMarket.isChecked = orderInfo.type == OrderType.MARKET
                btLimit.isChecked = orderInfo.type == OrderType.LIMIT

                val lower = String.format("%,.0f", symbol.priceLowerLimit)
                val upper = String.format("%,.0f", symbol.priceUpperLimit)
                tvPriceLimit.text = "(値幅制限 $lower-$upper)"
                val estimated = String.format("%,.0f", estimatedValue)
                tvEstimatedValue.text = "$estimated 円"

                Log.d(TAG, "current expiry is ${orderInfo.expiry}")
                btDay.isChecked = orderInfo.expiry == OrderExpiry.DAY
                btWeek.isChecked = orderInfo.expiry == OrderExpiry.WEEK
                btWeek.isEnabled = orderInfo.type == OrderType.LIMIT
                btTillDate.isChecked = orderInfo.expiry == OrderExpiry.TILL_DATE
                btTillDate.isEnabled = orderInfo.type == OrderType.LIMIT
                btSession.isChecked = orderInfo.expiry in listOf(OrderExpiry.OPENING, OrderExpiry.CLOSING, OrderExpiry.FUNARI)
            }
        })
        btQuantityPlus.setOnClickListener {
            viewModel.increaseQuantity()
        }
        btQuantityMinus.setOnClickListener {
            viewModel.decreaseQuantity()
        }
        btPricePlus.setOnClickListener {
            viewModel.increasePrice()
        }
        btPriceMinus.setOnClickListener {
            viewModel.decreasePrice()
        }
        btMarket.setOnClickListener {
            viewModel.setMarketOrder()
            togglePriceType()
        }
        btLimit.setOnClickListener {
            viewModel.setLimitOrder()
            togglePriceType()
        }
        etPrice.onChange { price ->
            val selection = etPrice.selectionStart
            val result = viewModel.setLimitPrice(price)
            etPrice.error = null
            if (!result) {
                etPrice.error = getString(R.string.invalid_price)
            } else if (selection >= 0) {
                etPrice.setSelection(selection)
            }
        }
        etQuantity.onChange { quantity ->
            val selection = etQuantity.selectionStart
            val result = viewModel.setQuantity(quantity)
            etQuantity.error = null
            if (!result) {
                etQuantity.error = getString(R.string.invalid_quantity)
            } else if (selection >= 0) {
                etQuantity.setSelection(selection)
            }
        }
        btDay.setOnClickListener {
            viewModel.setExpiry(OrderExpiry.DAY)
        }
        btWeek.setOnClickListener {
            viewModel.setExpiry(OrderExpiry.WEEK)
        }
        btTillDate.setOnClickListener {
            viewModel.setExpiry(OrderExpiry.TILL_DATE)
        }
        btSession.setOnClickListener {
            // the button status should only updated by the viewModel instead of the click event
            btSession.isChecked = !btSession.isChecked
            val popup = PopupMenu(activity!!, it)
            popup.inflate(R.menu.session_popup)
            if (btMarket.isChecked) {
                popup.menu.findItem(R.id.funari).isVisible = false
            }
            popup.setOnMenuItemClickListener { item ->
                when (item?.itemId) {
                    R.id.opening -> {
                        viewModel.setExpiry(OrderExpiry.OPENING)
                        true
                    }
                    R.id.closing -> {
                        viewModel.setExpiry(OrderExpiry.CLOSING)
                        true
                    }
                    R.id.funari -> {
                        viewModel.setExpiry(OrderExpiry.FUNARI)
                        true
                    }
                    else -> false
                }
            }
            popup.setOnDismissListener { _ -> viewModel.dummyUpdate()}
            popup.show()
        }

    }

    private fun togglePriceType() {
        if (btLimit.isChecked) {
            priceInput.visibility = View.VISIBLE
            tvPriceLimit.visibility = View.VISIBLE
        } else {
            priceInput.visibility = View.GONE
            tvPriceLimit.visibility = View.GONE
        }
    }

    private fun EditText.onChange(cb: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                cb(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

}
