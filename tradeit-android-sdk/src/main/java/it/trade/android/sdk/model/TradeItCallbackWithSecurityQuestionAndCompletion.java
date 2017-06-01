package it.trade.android.sdk.model;

import it.trade.model.TradeItSecurityQuestion;
import it.trade.model.callback.TradeItCallbackWithSecurityQuestionImpl;


public interface TradeItCallbackWithSecurityQuestionAndCompletion extends TradeItCallBackCompletion{
    void onSecurityQuestion(TradeItSecurityQuestion securityQuestion, TradeItCallbackWithSecurityQuestionImpl callback);
}
