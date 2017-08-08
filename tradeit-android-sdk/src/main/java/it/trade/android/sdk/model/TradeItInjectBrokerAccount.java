package it.trade.android.sdk.model;

public class TradeItInjectBrokerAccount {
    public String accountName;
    public String accountNumber;
    public String accountBaseCurrency;

    public TradeItInjectBrokerAccount(String accountName, String accountNumber, String accountBaseCurrency) {
        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.accountBaseCurrency = accountBaseCurrency;
    }
}
