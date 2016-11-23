package trade.it.android.sdk.model;


import java.util.Arrays;
import java.util.List;

import it.trade.tradeitapi.model.TradeItErrorCode;

public class TradeItErrorResult {
    private TradeItErrorCode errorCode = null;
    private String shortMessage = null;
    private List<String> longMessages = Arrays.asList("Trading is temporarily unavailable. Please try again in a few minutes.");
    private String systemMessage = "Unknown response sent from the server.";
    private int httpCode = 200;

     public TradeItErrorResult(TradeItErrorCode errorCode, String shortMessage, List<String> longMessages) {
        this.errorCode = errorCode;
        this.shortMessage = shortMessage;
        this.longMessages = longMessages;
        this.systemMessage = null;
    }

    public TradeItErrorResult(int httpCode) {
        this.httpCode = httpCode;
        this.systemMessage = "error sending request to ems server";
    }

    public TradeItErrorResult(String shortMessage) {
        this.shortMessage = shortMessage;
    }
}
