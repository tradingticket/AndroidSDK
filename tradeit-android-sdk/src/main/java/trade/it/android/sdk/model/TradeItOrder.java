package trade.it.android.sdk.model;

import it.trade.tradeitapi.model.TradeItPlaceStockOrEtfOrderRequest;
import it.trade.tradeitapi.model.TradeItPlaceStockOrEtfOrderResponse;
import it.trade.tradeitapi.model.TradeItPreviewStockOrEtfOrderRequest;
import it.trade.tradeitapi.model.TradeItPreviewStockOrEtfOrderResponse;
import retrofit2.Response;
import trade.it.android.sdk.enums.TradeItOrderAction;
import trade.it.android.sdk.enums.TradeItOrderExpiration;
import trade.it.android.sdk.enums.TradeItOrderPriceType;
import trade.it.android.sdk.internal.DefaultCallbackWithErrorHandling;
import trade.it.android.sdk.internal.PreviewTradeCallbackWithErrorHandling;

public class TradeItOrder {

    private TradeItLinkedBrokerAccount linkedBrokerAccount;
    private String symbol;
    private int quantity = 1;
    private Double limitPrice;
    private Double stopPrice;
    private Double quoteLastPrice;
    private TradeItOrderAction action = TradeItOrderAction.BUY;
    private TradeItOrderPriceType priceType = TradeItOrderPriceType.MARKET;
    private TradeItOrderExpiration expiration = TradeItOrderExpiration.GOOD_FOR_DAY;

    public TradeItOrder(TradeItLinkedBrokerAccount linkedBrokerAccount, String symbol) {
        this.linkedBrokerAccount = linkedBrokerAccount;
        this.symbol = symbol;
    }

    public void previewOrder(final TradeItCallback<TradeItPreviewStockOrEtfOrderResponse> callback) {
        TradeItPreviewStockOrEtfOrderRequest previewRequest = new TradeItPreviewStockOrEtfOrderRequest(this.linkedBrokerAccount.getAccountNumber(),
                this.action.getActionValue(),
                String.valueOf(this.quantity),
                this.symbol,
                this.priceType.getPriceTypeValue(),
                (this.limitPrice != null ? this.limitPrice.toString() : null),
                (this.stopPrice != null ? this.stopPrice.toString() : null),
                this.expiration.getExpirationValue());

        this.linkedBrokerAccount.getTradeItApiClient().previewStockOrEtfOrder(previewRequest, new PreviewTradeCallbackWithErrorHandling<TradeItPreviewStockOrEtfOrderResponse, TradeItPreviewStockOrEtfOrderResponse>(callback) {

            @Override
            public void onSuccessResponse(Response<TradeItPreviewStockOrEtfOrderResponse> response) {
                callback.onSuccess(response.body());
            }

        });
    }

    public void placeOrder(String orderId, final TradeItCallback<TradeItPlaceStockOrEtfOrderResponse> callback) {
        TradeItPlaceStockOrEtfOrderRequest placeStockOrEtfOrderRequest = new TradeItPlaceStockOrEtfOrderRequest(orderId);
        this.linkedBrokerAccount.getTradeItApiClient().placeStockOrEtfOrder(placeStockOrEtfOrderRequest, new DefaultCallbackWithErrorHandling<TradeItPlaceStockOrEtfOrderResponse, TradeItPlaceStockOrEtfOrderResponse>(callback) {

            @Override
            public void onSuccessResponse(Response<TradeItPlaceStockOrEtfOrderResponse> response) {
                callback.onSuccess(response.body());
            }
        });
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Double getLimitPrice() {
        return limitPrice;
    }

    public void setLimitPrice(Double limitPrice) {
        this.limitPrice = limitPrice;
    }

    public Double getStopPrice() {
        return stopPrice;
    }

    public void setStopPrice(Double stopPrice) {
        this.stopPrice = stopPrice;
    }

    public Double getQuoteLastPrice() {
        return quoteLastPrice;
    }

    public void setQuoteLastPrice(Double quoteLastPrice) {
        this.quoteLastPrice = quoteLastPrice;
    }

    public TradeItOrderAction getAction() {
        return action;
    }

    public void setAction(TradeItOrderAction action) {
        this.action = action;
    }

    public TradeItOrderPriceType getPriceType() {
        return priceType;
    }

    public void setPriceType(TradeItOrderPriceType priceType) {
        this.priceType = priceType;
    }

    public TradeItOrderExpiration getExpiration() {
        return expiration;
    }

    public void setExpiration(TradeItOrderExpiration expiration) {
        this.expiration = expiration;
    }
}
