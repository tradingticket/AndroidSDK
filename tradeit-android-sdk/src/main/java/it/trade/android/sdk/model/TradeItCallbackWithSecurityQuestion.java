package it.trade.android.sdk.model;

import it.trade.tradeitapi.API.TradeItApiClient;
import it.trade.android.sdk.internal.AuthenticationCallbackWithErrorHandling;

public interface TradeItCallbackWithSecurityQuestion<T> extends TradeItCallback<T>  {
    void onSecurityQuestion(TradeItSecurityQuestion securityQuestion);
    void submitSecurityAnswer(String answer);
    void setApiClient(TradeItApiClient apiClient);
    void setAuthenticationHandler(AuthenticationCallbackWithErrorHandling authenticationHandler);

}
