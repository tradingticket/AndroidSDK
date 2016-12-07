package trade.it.android.sdk.model;


import it.trade.tradeitapi.model.TradeItAuthenticateResponse.Account;

public class TradeItLinkedBrokerAccount {

    private String accountName;
    private String accountNumber;
    private String accountBaseCurrency;
    private TradeItLinkedBroker linkedBroker;

    public TradeItLinkedBrokerAccount(TradeItLinkedBroker linkedBroker, Account account) {
        this.linkedBroker = linkedBroker;
        this.accountName = account.name;
        this.accountNumber = account.accountNumber;
        this.accountBaseCurrency = account.accountBaseCurrency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TradeItLinkedBrokerAccount that = (TradeItLinkedBrokerAccount) o;

        if (!accountName.equals(that.accountName)) return false;
        return accountNumber.equals(that.accountNumber);

    }

    @Override
    public int hashCode() {
        int result = accountName.hashCode();
        result = 31 * result + accountNumber.hashCode();
        return result;
    }
}
