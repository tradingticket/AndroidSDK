package it.trade.android.exampleapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import java.util.List;

import it.trade.android.sdk.model.TradeItLinkedBrokerAccountParcelable;
import it.trade.android.sdk.model.TradeItPositionParcelable;
import it.trade.model.TradeItErrorResult;
import it.trade.model.callback.TradeItCallback;

import static it.trade.android.exampleapp.MainActivity.POSITIONS_PARAMETER;


public class PositionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_positions);
        final TextView textView = (TextView) this.findViewById(R.id.positions_textview);
        textView.setMovementMethod(new ScrollingMovementMethod());
        Intent intent = getIntent();
        List<TradeItPositionParcelable> positions = intent.getParcelableArrayListExtra(POSITIONS_PARAMETER);
        textView.setText(positions.toString());
        TradeItLinkedBrokerAccountParcelable linkedBrokerAccount = intent.getParcelableExtra(MainActivity.PARCELED_ACCOUNT_PARAMETER);
        TradeItPositionParcelable position = null;
        for (TradeItPositionParcelable positionParcelable: positions) {
            if (positionParcelable.isProxyVoteEligible) {
                position = positionParcelable;
                break;
            }
        }
        linkedBrokerAccount.getProxyVoteUrl(position.symbol, new TradeItCallback<String>() {
            @Override
            public void onSuccess(String proxyVoteUrl) {
                textView.setText("Proxyvote url for last position: " + proxyVoteUrl);
            }

            @Override
            public void onError(TradeItErrorResult error) {
                textView.setText("Error getting Proxyvote url for last position: " + error);
            }
        });
    }
}
