package it.trade.android.sdk.exceptions

class TradeItSDKConfigurationException : RuntimeException {

    constructor(detailMessage: String) : super(detailMessage) {}

    constructor(detailMessage: String, throwable: Throwable) : super(detailMessage, throwable) {}
}
