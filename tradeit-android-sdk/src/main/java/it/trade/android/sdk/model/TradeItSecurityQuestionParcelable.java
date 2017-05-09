package it.trade.android.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import it.trade.model.TradeItSecurityQuestion;

public class TradeItSecurityQuestionParcelable extends TradeItSecurityQuestion implements Parcelable {

    public TradeItSecurityQuestionParcelable(String securityQuestion, List<String> securityQuestionOptions) {
        super(securityQuestion, securityQuestionOptions);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.securityQuestion);
        dest.writeStringList(this.securityQuestionOptions);
    }

    protected TradeItSecurityQuestionParcelable(Parcel in) {
        this.securityQuestion = in.readString();
        this.securityQuestionOptions = in.createStringArrayList();
    }

    public static final Parcelable.Creator<TradeItSecurityQuestionParcelable> CREATOR = new Parcelable.Creator<TradeItSecurityQuestionParcelable>() {
        @Override
        public TradeItSecurityQuestionParcelable createFromParcel(Parcel source) {
            return new TradeItSecurityQuestionParcelable(source);
        }

        @Override
        public TradeItSecurityQuestionParcelable[] newArray(int size) {
            return new TradeItSecurityQuestionParcelable[size];
        }
    };
}
