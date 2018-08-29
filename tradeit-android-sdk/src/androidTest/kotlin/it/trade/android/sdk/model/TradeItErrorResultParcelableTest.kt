package it.trade.android.sdk.model

import android.os.Parcel
import android.support.test.runner.AndroidJUnit4
import android.test.suitebuilder.annotation.SmallTest

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import java.util.Arrays

import it.trade.model.reponse.TradeItErrorCode

import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertThat

@RunWith(AndroidJUnit4::class)
@SmallTest
class TradeItErrorResultParcelableTest {

    private var errorResult: TradeItErrorResultParcelable? = null

    @Before
    fun createTradeItErrorResult() {
        errorResult = TradeItErrorResultParcelable()
    }

    @Test
    fun errorResult_ParcelableWriteRead() {
        // Set up the Parcelable object to send and receive.
        errorResult!!.errorCode = TradeItErrorCode.BROKER_ACCOUNT_ERROR
        errorResult!!.httpCode = 400
        errorResult!!.longMessages = Arrays.asList("Error1", "Error2")
        errorResult!!.shortMessage = "MyShortMessage"
        errorResult!!.systemMessage = "MySystemMessage"

        // Write the data.
        val parcel = Parcel.obtain()
        errorResult!!.writeToParcel(parcel, errorResult!!.describeContents())

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0)

        // Read the data.
        val createdFromParcel = TradeItErrorResultParcelable.CREATOR.createFromParcel(parcel)
        val errorCode = createdFromParcel.errorCode
        val httpCode = createdFromParcel.httpCode
        val longMessages = createdFromParcel.longMessages
        val shortMessage = createdFromParcel.shortMessage
        val systemMessage = createdFromParcel.systemMessage

        // Verify that the received data is correct.
        assertThat(errorCode, `is`(errorResult!!.errorCode))
        assertThat(httpCode, `is`(errorResult!!.httpCode))
        assertThat(longMessages, `is`(errorResult!!.longMessages))
        assertThat(shortMessage, `is`(errorResult!!.shortMessage))
        assertThat(systemMessage, `is`(errorResult!!.systemMessage))
    }
}
