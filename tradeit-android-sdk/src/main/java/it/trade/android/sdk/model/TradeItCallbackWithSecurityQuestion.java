package it.trade.android.sdk.model;

import it.trade.tradeitapi.API.TradeItApiClient;
import it.trade.android.sdk.internal.AuthenticationCallback;

public interface TradeItCallbackWithSecurityQuestion<T> extends TradeItCallback<T>  {
    void onSecurityQuestion(TradeItSecurityQuestion securityQuestion);
    void submitSecurityAnswer(String answer);
    void setApiClient(TradeItApiClient apiClient);
    void setAuthenticationHandler(AuthenticationCallback authenticationHandler);

}
