package it.trade.android.sdk.model;

import java.util.ArrayList;
import java.util.List;

public class TradeItInjectBroker {

    public String broker;
    public String userId;
    public String userToken;
    public boolean isLinkActivationPending = false;
    public List<TradeItInjectBrokerAccount> injectAccounts = new ArrayList<>();

    public TradeItInjectBroker(String broker, String userId, String userToken) {
        this.broker = broker;
        this.userId = userId;
        this.userToken = userToken;
    }

    public TradeItInjectBroker withLinkActivationPending(boolean isLinkActivationPending) {
        this.isLinkActivationPending = isLinkActivationPending;
        return this;
    }

    public void injectAccount(TradeItInjectBrokerAccount injectBrokerAccount) {
        this.injectAccounts.add(injectBrokerAccount);
    }

    public TradeItInjectBroker(TradeItLinkedLoginParcelable linkedLoginParcelable) {
        this.broker = linkedLoginParcelable.broker;
        this.userId = linkedLoginParcelable.userId;
        this.userToken = linkedLoginParcelable.userToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TradeItInjectBroker that = (TradeItInjectBroker) o;

        return (userId.equals(that.userId) && userToken.equals(that.userToken));
    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (userToken != null ? userToken.hashCode() : 0);
        return result;
    }
}
