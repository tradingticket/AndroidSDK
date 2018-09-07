package it.trade.android.sdk.model

import android.os.Parcel
import android.support.test.filters.SmallTest
import android.support.test.runner.AndroidJUnit4
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
@SmallTest
class TradeItSecurityQuestionParcelableTest {

    private var tradeItSecurityQuestion: TradeItSecurityQuestionParcelable? = null

    @Before
    fun createTradeItSecurityQuestion() {
        tradeItSecurityQuestion = TradeItSecurityQuestionParcelable("My security question", Arrays.asList("option1", "option2", "option3"))
    }

    @Test
    fun securityQuestion_ParcelableWriteRead() {
        val parcel = Parcel.obtain()
        tradeItSecurityQuestion!!.writeToParcel(parcel, tradeItSecurityQuestion!!.describeContents())

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0)

        // Read the data.
        val createdFromParcel = TradeItSecurityQuestionParcelable.CREATOR.createFromParcel(parcel)
        val securityQuestion = createdFromParcel.securityQuestion
        val securityQuestionOptions = createdFromParcel.securityQuestionOptions

        assertThat(securityQuestion, `is`(tradeItSecurityQuestion!!.securityQuestion))
        assertThat(securityQuestionOptions, `is`(tradeItSecurityQuestion!!.securityQuestionOptions))
    }
}
