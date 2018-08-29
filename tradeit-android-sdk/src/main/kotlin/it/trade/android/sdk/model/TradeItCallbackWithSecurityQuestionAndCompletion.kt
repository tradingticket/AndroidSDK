package it.trade.android.sdk.model

import it.trade.model.TradeItSecurityQuestion
import it.trade.model.callback.TradeItCallbackWithSecurityQuestionImpl


interface TradeItCallbackWithSecurityQuestionAndCompletion : TradeItCallBackCompletion {
    fun onSecurityQuestion(securityQuestion: TradeItSecurityQuestion, callback: TradeItCallbackWithSecurityQuestionImpl<*>)
}
