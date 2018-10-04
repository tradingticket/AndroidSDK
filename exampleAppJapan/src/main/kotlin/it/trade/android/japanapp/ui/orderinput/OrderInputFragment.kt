package it.trade.android.japanapp.ui.orderinput

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.opengl.Visibility
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.trade.android.japanapp.R
import kotlinx.android.synthetic.main.order_input_fragment.*
import kotlinx.android.synthetic.main.order_input_fragment.view.*

class OrderInputFragment : Fragment() {

    companion object {
        fun newInstance() = OrderInputFragment()
        fun newInstance(symbol: String): OrderInputFragment {
            val args = Bundle()
            args.putString("symbol", symbol)
            val fragment = OrderInputFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var viewModel: OrderInputViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.order_input_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(OrderInputViewModel::class.java)
        arguments?.getString("symbol")?.let {
            viewModel.init(it)
        }
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

                val lower = String.format("%,.0f", symbol.priceLowerLimit)
                val upper = String.format("%,.0f", symbol.priceUpperLimit)
                tvPriceLimit.text = "(値幅制限 $lower-$upper)"
                val estimated = String.format("%,.0f", estimatedValue)
                tvEstimatedValue.text = "$estimated 円"
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
        btLimit.isChecked = true
        btMarket.isChecked = false
        btMarket.setOnClickListener {
            btLimit.isChecked = false
            btMarket.isChecked = true
            togglePriceType()
            viewModel.resetPrice()
        }
        btLimit.setOnClickListener {
            btLimit.isChecked = true
            btMarket.isChecked = false
            togglePriceType()
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

}
