package it.trade.android.sdk.model

import java.util.ArrayList

class TradeItLinkedBrokerData {

    var broker: String
    var userId: String? = null
    var userToken: String? = null
    var isLinkActivationPending = false
    var linkedBrokerAccounts: MutableList<TradeItLinkedBrokerAccountData> = ArrayList()

    constructor(broker: String, userId: String, userToken: String) {
        this.broker = broker
        this.userId = userId
        this.userToken = userToken
    }

    fun withLinkActivationPending(isLinkActivationPending: Boolean): TradeItLinkedBrokerData {
        this.isLinkActivationPending = isLinkActivationPending
        return this
    }

    fun injectAccount(linkedBrokerAccountData: TradeItLinkedBrokerAccountData) {
        this.linkedBrokerAccounts.add(linkedBrokerAccountData)
    }

    fun injectAccounts(linkedBrokerAccounts: MutableList<TradeItLinkedBrokerAccountData>) {
        this.linkedBrokerAccounts = linkedBrokerAccounts
    }

    constructor(linkedLoginParcelable: TradeItLinkedLoginParcelable) {
        this.broker = linkedLoginParcelable.broker
        this.userId = linkedLoginParcelable.userId
        this.userToken = linkedLoginParcelable.userToken
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val that = o as TradeItLinkedBrokerData?

        return userId == that!!.userId && userToken == that.userToken
    }

    override fun hashCode(): Int {
        var result = if (userId != null) userId!!.hashCode() else 0
        result = 31 * result + if (userToken != null) userToken!!.hashCode() else 0
        return result
    }
}
