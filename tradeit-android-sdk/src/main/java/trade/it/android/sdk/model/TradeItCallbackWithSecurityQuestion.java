package trade.it.android.sdk.model;

import it.trade.tradeitapi.API.TradeItApiClient;
import trade.it.android.sdk.internal.AuthenticationCallbackWithErrorHandling;

public interface TradeItCallbackWithSecurityQuestion<T> extends TradeItCallback<T>  {
    void onSecurityQuestion(TradeItSecurityQuestion securityQuestion);
    void submitSecurityAnswer(String answer);
    void setApiClient(TradeItApiClient apiClient);
    void setAuthenticationHandler(AuthenticationCallbackWithErrorHandling authenticationHandler);

}
