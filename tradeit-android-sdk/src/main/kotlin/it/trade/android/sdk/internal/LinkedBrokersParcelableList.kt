package it.trade.android.sdk.internal

import it.trade.android.sdk.model.TradeItLinkedBrokerParcelable
import java.util.*


class LinkedBrokersParcelableList(linkedBrokers: List<TradeItLinkedBrokerParcelable>) : ArrayList<TradeItLinkedBrokerParcelable>() {

    init {
        this.addAll(linkedBrokers)
    }

    fun containsSameAccounts(linkedBrokerParcelable: TradeItLinkedBrokerParcelable): Boolean {
        return this.any {
            it == linkedBrokerParcelable && it.equalsAccounts(linkedBrokerParcelable)
        }
    }
}
