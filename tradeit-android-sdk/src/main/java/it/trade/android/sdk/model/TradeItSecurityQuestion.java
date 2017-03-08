package it.trade.android.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.List;

public class TradeItSecurityQuestion implements Parcelable {

    private List<String>  securityQuestionOptions = null;

    public TradeItSecurityQuestion(String securityQuestion, List<String> securityQuestionOptions) {
        if (securityQuestion != null) {
            securityQuestionOptions = Arrays.asList(securityQuestion);
        }
        this.securityQuestionOptions = securityQuestionOptions;
    }

    public List<String> getSecurityQuestionOptions() {
        return securityQuestionOptions;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.securityQuestionOptions);
    }

    protected TradeItSecurityQuestion(Parcel in) {
        this.securityQuestionOptions = in.createStringArrayList();
    }

    public static final Parcelable.Creator<TradeItSecurityQuestion> CREATOR = new Parcelable.Creator<TradeItSecurityQuestion>() {
        @Override
        public TradeItSecurityQuestion createFromParcel(Parcel source) {
            return new TradeItSecurityQuestion(source);
        }

        @Override
        public TradeItSecurityQuestion[] newArray(int size) {
            return new TradeItSecurityQuestion[size];
        }
    };
}
