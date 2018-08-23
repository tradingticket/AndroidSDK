package it.trade.android.sdk.internal

import java.util.ArrayList

import it.trade.android.sdk.model.TradeItLinkedBrokerParcelable


class LinkedBrokersParcelableList(linkedBrokers: List<TradeItLinkedBrokerParcelable>) : ArrayList<TradeItLinkedBrokerParcelable>() {

    init {
        this.addAll(linkedBrokers)
    }

    fun containsSameAccounts(linkedBrokerParcelable: TradeItLinkedBrokerParcelable): Boolean {
        for (o in this) {
            if (o != null && o == linkedBrokerParcelable && o.equalsAccounts(linkedBrokerParcelable)) {
                return true
            }
        }
        return false
    }
}
