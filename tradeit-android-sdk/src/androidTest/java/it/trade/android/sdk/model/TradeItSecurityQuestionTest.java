package it.trade.android.sdk.model;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class TradeItSecurityQuestionTest {

    private TradeItSecurityQuestion securityQuestion;

    @Before
    public void createTradeItSecurityQuestion() {
        securityQuestion = new TradeItSecurityQuestion("My security question", null);
    }

    @Test
    public void securityQuestion_ParcelableWriteRead() {
        Parcel parcel = Parcel.obtain();
        securityQuestion.writeToParcel(parcel, securityQuestion.describeContents());

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0);

        // Read the data.
        TradeItSecurityQuestion createdFromParcel = TradeItSecurityQuestion.CREATOR.createFromParcel(parcel);
        List<String> securityQuestionOptions = createdFromParcel.getSecurityQuestionOptions();

        assertThat(securityQuestionOptions, is(securityQuestion.getSecurityQuestionOptions()));
    }
}
