package it.trade.android.sdk.model;

public class TradeItInjectBroker {

    public String broker;
    public String userId;
    public String userToken;
    public boolean isLinkActivationPending;

    public TradeItInjectBroker(String broker, String userId, String userToken, boolean isLinkActivationPending) {
        this.broker = broker;
        this.userId = userId;
        this.userToken = userToken;
        this.isLinkActivationPending = isLinkActivationPending;
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
