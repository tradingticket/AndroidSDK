package it.trade.android.sdk.model;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import it.trade.model.TradeItSecurityQuestion;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class TradeItSecurityQuestionParcelableTest {

    private TradeItSecurityQuestionParcelable tradeItSecurityQuestion;

    @Before
    public void createTradeItSecurityQuestion() {
        tradeItSecurityQuestion = new TradeItSecurityQuestionParcelable("My security question", Arrays.asList("option1", "option2", "option3"));
    }

    @Test
    public void securityQuestion_ParcelableWriteRead() {
        Parcel parcel = Parcel.obtain();
        tradeItSecurityQuestion.writeToParcel(parcel, tradeItSecurityQuestion.describeContents());

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0);

        // Read the data.
        TradeItSecurityQuestion createdFromParcel = TradeItSecurityQuestionParcelable.CREATOR.createFromParcel(parcel);
        String securityQuestion = createdFromParcel.getSecurityQuestion();
        List<String> securityQuestionOptions = createdFromParcel.getSecurityQuestionOptions();

        assertThat(securityQuestion, is(tradeItSecurityQuestion.getSecurityQuestion()));
        assertThat(securityQuestionOptions, is(tradeItSecurityQuestion.getSecurityQuestionOptions()));
    }
}
