package it.trade.android.sdk.model;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import it.trade.model.TradeItErrorResult;
import it.trade.model.reponse.TradeItErrorCode;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class TradeItErrorResultParcelableTest {

    private TradeItErrorResultParcelable errorResult;

    @Before
    public void createTradeItErrorResult() {
        errorResult = new TradeItErrorResultParcelable();
    }

    @Test
    public void errorResult_ParcelableWriteRead() {
        // Set up the Parcelable object to send and receive.
        errorResult.setErrorCode(TradeItErrorCode.BROKER_ACCOUNT_ERROR);
        errorResult.setHttpCode(400);
        errorResult.setLongMessages(Arrays.asList("Error1", "Error2"));
        errorResult.setShortMessage("MyShortMessage");
        errorResult.setSystemMessage("MySystemMessage");

        // Write the data.
        Parcel parcel = Parcel.obtain();
        errorResult.writeToParcel(parcel, errorResult.describeContents());

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0);

        // Read the data.
        TradeItErrorResult createdFromParcel = TradeItErrorResultParcelable.CREATOR.createFromParcel(parcel);
        TradeItErrorCode errorCode = createdFromParcel.getErrorCode();
        int httpCode = createdFromParcel.getHttpCode();
        List<String> longMessages = createdFromParcel.getLongMessages();
        String shortMessage = createdFromParcel.getShortMessage();
        String systemMessage = createdFromParcel.getSystemMessage();

        // Verify that the received data is correct.
        assertThat(errorCode, is(errorResult.getErrorCode()));
        assertThat(httpCode, is(errorResult.getHttpCode()));
        assertThat(longMessages, is(errorResult.getLongMessages()));
        assertThat(shortMessage, is(errorResult.getShortMessage()));
        assertThat(systemMessage, is(errorResult.getSystemMessage()));
    }
}
