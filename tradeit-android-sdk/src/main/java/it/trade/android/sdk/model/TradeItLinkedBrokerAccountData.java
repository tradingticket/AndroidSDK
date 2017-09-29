package it.trade.android.sdk.model;

public class TradeItLinkedBrokerAccountData {
    public String accountName;
    public String accountNumber;
    public String accountBaseCurrency;

    public TradeItLinkedBrokerAccountData(String accountName, String accountNumber, String accountBaseCurrency) {
        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.accountBaseCurrency = accountBaseCurrency;
    }
}
