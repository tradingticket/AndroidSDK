package trade.it.android.sdk.model;

import java.util.Arrays;
import java.util.List;

public class TradeItSecurityQuestion {

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


}
