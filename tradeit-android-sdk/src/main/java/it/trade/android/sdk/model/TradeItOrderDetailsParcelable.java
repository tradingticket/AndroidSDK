package it.trade.android.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import it.trade.model.reponse.OrderDetails;
import it.trade.model.reponse.Warning;

public class TradeItOrderDetailsParcelable implements Parcelable {
    protected String orderSymbol;
    protected String orderAction;
    protected Double orderQuantity;
    protected String orderExpiration;
    protected String orderPrice;
    protected String orderValueLabel;
    protected String orderCommissionLabel;
    protected String orderMessage;
    protected String lastPrice;
    protected String bidPrice;
    protected String askPrice;
    protected String timestamp;
    protected Double buyingPower;
    protected Double availableCash;
    protected Double estimatedOrderCommission;
    protected Double longHoldings;
    protected Double shortHoldings;
    protected Double estimatedOrderValue;
    protected Double estimatedTotalValue;
    protected List<TradeItWarningParcelable> warnings = new ArrayList<>();

    TradeItOrderDetailsParcelable() {
    }

    @Override
    public String toString() {
        final String strDelimiter = "', ";
        final String numberDelimiter = ", ";
        StringBuilder builder = new StringBuilder();
        builder.append("OrderDetails{");
        builder.append("orderSymbol='").append(orderSymbol).append(strDelimiter);
        builder.append("orderAction='").append(orderAction).append(strDelimiter);
        builder.append("orderQuantity=").append(orderQuantity).append(numberDelimiter);
        builder.append("orderExpiration='").append(orderExpiration).append(strDelimiter);
        builder.append("orderPrice='").append(orderPrice).append(strDelimiter);
        builder.append("orderValueLabel='").append(orderValueLabel).append(strDelimiter);
        builder.append("orderCommissionLabel='").append(orderCommissionLabel).append(strDelimiter);
        builder.append("orderMessage='").append(orderMessage).append(strDelimiter);
        builder.append("lastPrice='").append(lastPrice).append(strDelimiter);
        builder.append("bidPrice='").append(bidPrice).append(strDelimiter);
        builder.append("askPrice='").append(askPrice).append(strDelimiter);
        builder.append("timestamp='").append(timestamp).append(strDelimiter);
        builder.append("buyingPower=").append(buyingPower).append(numberDelimiter);
        builder.append("availableCash=").append(availableCash).append(numberDelimiter);
        builder.append("estimatedOrderCommission=").append(estimatedOrderCommission).append(numberDelimiter);
        builder.append("longHoldings=").append(longHoldings).append(numberDelimiter);
        builder.append("shortHoldings=").append(shortHoldings).append(numberDelimiter);
        builder.append("estimatedOrderValue=").append(estimatedOrderValue).append(numberDelimiter);
        builder.append("estimatedTotalValue=").append(estimatedTotalValue).append(numberDelimiter);
        builder.append("warnings=[");
        for (TradeItWarningParcelable warning: warnings) {
            builder.append(warning.toString()).append(numberDelimiter);
        }
        builder.append("]");

        builder.append("}");

        return builder.toString();
    }

    TradeItOrderDetailsParcelable(OrderDetails orderDetails) {
        this.askPrice = orderDetails.askPrice;
        this.availableCash = orderDetails.availableCash;
        this.bidPrice = orderDetails.bidPrice;
        this.buyingPower = orderDetails.buyingPower;
        this.estimatedOrderCommission = orderDetails.estimatedOrderCommission;
        this.estimatedOrderValue = orderDetails.estimatedOrderValue;
        this.estimatedTotalValue = orderDetails.estimatedTotalValue;
        this.lastPrice = orderDetails.lastPrice;
        this.orderAction = orderDetails.orderAction;
        this.orderSymbol = orderDetails.orderSymbol;
        this.orderExpiration = orderDetails.orderExpiration;
        this.orderPrice = orderDetails.orderPrice;
        this.orderQuantity = orderDetails.orderQuantity;
        this.orderMessage = orderDetails.orderMessage;
        this.orderValueLabel = orderDetails.orderValueLabel;
        this.orderCommissionLabel = orderDetails.orderCommissionLabel;
        this.timestamp = orderDetails.timestamp;
        this.longHoldings = orderDetails.longHoldings;
        this.shortHoldings = orderDetails.shortHoldings;
        this.warnings = mapWarnings(orderDetails.warnings);
    }

    private static List<TradeItWarningParcelable> mapWarnings(List<Warning> warnings) {
        List<TradeItWarningParcelable> mappedValues = new ArrayList<>();
        if (warnings != null) {
            for (Warning warning: warnings) {
                mappedValues.add(new TradeItWarningParcelable(warning));
            }
        }
        return mappedValues;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.orderSymbol);
        dest.writeString(this.orderAction);
        dest.writeValue(this.orderQuantity);
        dest.writeString(this.orderExpiration);
        dest.writeString(this.orderPrice);
        dest.writeString(this.orderValueLabel);
        dest.writeString(this.orderCommissionLabel);
        dest.writeString(this.orderMessage);
        dest.writeString(this.lastPrice);
        dest.writeString(this.bidPrice);
        dest.writeString(this.askPrice);
        dest.writeString(this.timestamp);
        dest.writeValue(this.buyingPower);
        dest.writeValue(this.availableCash);
        dest.writeValue(this.estimatedOrderCommission);
        dest.writeValue(this.longHoldings);
        dest.writeValue(this.shortHoldings);
        dest.writeValue(this.estimatedOrderValue);
        dest.writeValue(this.estimatedTotalValue);
        dest.writeTypedList(this.warnings);
    }

    protected TradeItOrderDetailsParcelable(Parcel in) {
        this.orderSymbol = in.readString();
        this.orderAction = in.readString();
        this.orderQuantity = (Double) in.readValue(Double.class.getClassLoader());
        this.orderExpiration = in.readString();
        this.orderPrice = in.readString();
        this.orderValueLabel = in.readString();
        this.orderCommissionLabel = in.readString();
        this.orderMessage = in.readString();
        this.lastPrice = in.readString();
        this.bidPrice = in.readString();
        this.askPrice = in.readString();
        this.timestamp = in.readString();
        this.buyingPower = (Double) in.readValue(Double.class.getClassLoader());
        this.availableCash = (Double) in.readValue(Double.class.getClassLoader());
        this.estimatedOrderCommission = (Double) in.readValue(Double.class.getClassLoader());
        this.longHoldings = (Double) in.readValue(Double.class.getClassLoader());
        this.shortHoldings = (Double) in.readValue(Double.class.getClassLoader());
        this.estimatedOrderValue = (Double) in.readValue(Double.class.getClassLoader());
        this.estimatedTotalValue = (Double) in.readValue(Double.class.getClassLoader());
        this.warnings = in.createTypedArrayList(TradeItWarningParcelable.CREATOR);
    }

    public static final Parcelable.Creator<TradeItOrderDetailsParcelable> CREATOR = new Parcelable.Creator<TradeItOrderDetailsParcelable>() {
        @Override
        public TradeItOrderDetailsParcelable createFromParcel(Parcel source) {
            return new TradeItOrderDetailsParcelable(source);
        }

        @Override
        public TradeItOrderDetailsParcelable[] newArray(int size) {
            return new TradeItOrderDetailsParcelable[size];
        }
    };

    public String getOrderSymbol() {
        return orderSymbol;
    }

    public String getOrderAction() {
        return orderAction;
    }

    public Double getOrderQuantity() {
        return orderQuantity;
    }

    public String getOrderExpiration() {
        return orderExpiration;
    }

    public String getOrderPrice() {
        return orderPrice;
    }

    public String getOrderValueLabel() {
        return orderValueLabel;
    }

    public String getOrderCommissionLabel() {
        return orderCommissionLabel;
    }

    public String getOrderMessage() {
        return orderMessage;
    }

    public String getLastPrice() {
        return lastPrice;
    }

    public String getBidPrice() {
        return bidPrice;
    }

    public String getAskPrice() {
        return askPrice;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public Double getBuyingPower() {
        return buyingPower;
    }

    public Double getAvailableCash() {
        return availableCash;
    }

    public Double getEstimatedOrderCommission() {
        return estimatedOrderCommission;
    }

    public Double getLongHoldings() {
        return longHoldings;
    }

    public Double getShortHoldings() {
        return shortHoldings;
    }

    public Double getEstimatedOrderValue() {
        return estimatedOrderValue;
    }

    public Double getEstimatedTotalValue() {
        return estimatedTotalValue;
    }

    public List<TradeItWarningParcelable> getWarnings() {
        return warnings;
    }
}
