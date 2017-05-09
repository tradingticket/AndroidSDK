package it.trade.android.exampleapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import java.util.List;

import it.trade.android.sdk.model.TradeItPositionParcelable;

import static it.trade.android.exampleapp.MainActivity.POSITIONS_PARAMETER;


public class PositionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_positions);
        TextView textView = (TextView) this.findViewById(R.id.positions_textview);
        textView.setMovementMethod(new ScrollingMovementMethod());
        Intent intent = getIntent();
        List<TradeItPositionParcelable> positions = intent.getParcelableArrayListExtra(POSITIONS_PARAMETER);
        textView.setText(positions.toString());
    }
}
