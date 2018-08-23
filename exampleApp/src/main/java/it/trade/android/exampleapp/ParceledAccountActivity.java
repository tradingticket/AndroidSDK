package it.trade.android.exampleapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import it.trade.android.sdk.TradeItSDK;
import it.trade.android.sdk.model.TradeItLinkedBrokerAccountParcelable;
import it.trade.android.sdk.model.TradeItPositionParcelable;
import it.trade.model.TradeItErrorResult;
import it.trade.model.TradeItSecurityQuestion;
import it.trade.model.callback.TradeItCallback;
import it.trade.model.callback.TradeItCallbackWithSecurityQuestionImpl;

import static it.trade.android.exampleapp.MainActivity.PARCELED_ACCOUNT_PARAMETER;

public class ParceledAccountActivity extends AppCompatActivity {
    TextView textView;
    TradeItLinkedBrokerAccountParcelable linkedBrokerAccount;
    TradeItLinkedBrokerAccountParcelable originalLinkedBrokerAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parceled_account);
        this.textView = (TextView) this.findViewById(R.id.output_textview);
        this.textView.setMovementMethod(new ScrollingMovementMethod());
        Intent intent = getIntent();
        linkedBrokerAccount = intent.getParcelableExtra(PARCELED_ACCOUNT_PARAMETER);
        originalLinkedBrokerAccount = TradeItSDK
                .getLinkedBrokerManager()
                .getLinkedBrokerByUserId(linkedBrokerAccount.linkedBroker.getLinkedLogin().userId)
                .getLinkedBrokerAccount(linkedBrokerAccount.getAccountNumber());

        String message = "Parceled LinkedBrokerAccount(@"
                + System.identityHashCode(linkedBrokerAccount)
                + "):\n"
                + linkedBrokerAccount.toString()
                + "\n\nLinkedBrokerManager->LinkedBrokerAccount(@"
                + System.identityHashCode(originalLinkedBrokerAccount)
                + "):\n"
                + originalLinkedBrokerAccount.toString();
        textView.setText(message);
    }

    public void getBalanceTapped(View view) {
        this.textView.setText("Fetching balances...");

        linkedBrokerAccount.refreshBalance(new TradeItCallback<TradeItLinkedBrokerAccountParcelable>() {
            @Override
            public void onSuccess(TradeItLinkedBrokerAccountParcelable linkedBrokerAccountParcelable) {
                String message = "Parceled LinkedBrokerAccount(@"
                        + System.identityHashCode(linkedBrokerAccountParcelable)
                        + "):\n"
                        + linkedBrokerAccountParcelable.toString()
                        + "\n\n==========\n\n"
                        + "LinkedBrokerManager->LinkedBrokerAccount(@"
                        + System.identityHashCode(originalLinkedBrokerAccount)
                        + "):\n"
                        + originalLinkedBrokerAccount.toString();
                textView.setText(message);
            }

            @Override
            public void onError(TradeItErrorResult error) {
                textView.setText("Error refreshing balances: " + error);
            }
        });
    }

    public void getPortfolioTapped(View view) {
        this.textView.setText("Fetching portfolio...");

        linkedBrokerAccount.refreshPositions(new TradeItCallback<List<TradeItPositionParcelable>>() {
            @Override
            public void onSuccess(List<TradeItPositionParcelable> positions) {
                String message = "Parceled LinkedBrokerAccount.positions(@"
                        + System.identityHashCode(linkedBrokerAccount)
                        + "):\n"
                        + linkedBrokerAccount.getPositions().toString()
                        + "\n\n==========\n\n"
                        + "LinkedBrokerManager->LinkedBrokerAccount.positions(@"
                        + System.identityHashCode(originalLinkedBrokerAccount)
                        + "):\n"
                        + originalLinkedBrokerAccount.getPositions().toString();
                textView.setText(message);
            }

            @Override
            public void onError(TradeItErrorResult error) {
                textView.setText("Error refreshing portfolio: " + error);
            }
        });
    }

    public void authenticate(View view) {
        linkedBrokerAccount.linkedBroker.authenticate(new TradeItCallbackWithSecurityQuestionImpl<List<TradeItLinkedBrokerAccountParcelable>>() {
            @Override
            public void onSecurityQuestion(TradeItSecurityQuestion securityQuestion) {

            }

            @Override
            public void onSuccess(List<TradeItLinkedBrokerAccountParcelable> accounts) {
                String message = "Parceled LinkedBrokerAccount(@"
                        + System.identityHashCode(linkedBrokerAccount)
                        + "):\n"
                        + linkedBrokerAccount.toString()
                        + "\n\n==========\n\n"
                        + "LinkedBrokerManager->LinkedBrokerAccount(@"
                        + System.identityHashCode(accounts.get(0))
                        + "):\n"
                        + accounts.get(0).toString();
                textView.setText(message);
            }

            @Override
            public void onError(TradeItErrorResult error) {
                textView.setText("Error authenticating: " + error);
            }
        });
    }
}
