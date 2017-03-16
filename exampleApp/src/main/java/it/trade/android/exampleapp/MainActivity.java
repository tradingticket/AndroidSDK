package it.trade.android.exampleapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.trade.android.sdk.TradeItSDK;
import it.trade.android.sdk.manager.TradeItLinkedBrokerManager;
import it.trade.android.sdk.model.TradeItCallBackImpl;
import it.trade.android.sdk.model.TradeItCallbackWithSecurityQuestionImpl;
import it.trade.android.sdk.model.TradeItErrorResult;
import it.trade.android.sdk.model.TradeItLinkedBroker;
import it.trade.android.sdk.model.TradeItLinkedBrokerAccount;
import it.trade.android.sdk.model.TradeItSecurityQuestion;
import it.trade.tradeitapi.model.TradeItEnvironment;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    public final static String LINKED_BROKERS_PARAMETER = "it.trade.android.exampleapp.LINKED_BROKERS";
    public final static String LINKED_BROKER_ACCOUNTS_PARAMETERS = "it.trade.android.exampleapp.LINKED_BROKER_ACCOUNTS";

    TradeItLinkedBrokerManager linkedBrokerManager;

    private static final int OAUTH_LINKED_A_BROKER_ID = 0;
    private static final int GET_LINKED_BROKERS_ID = 1;
    private static final int DELETE_ALL_LINKED_BROKERS_ID = 2;
    private static final int AUTHENTICATE_FIRST_LINKED_BROKER = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initTable();

        TradeItSDK.configure(this.getApplicationContext(), "tradeit-test-api-key", TradeItEnvironment.QA);
        linkedBrokerManager = TradeItSDK.getLinkedBrokerManager();


    }

    private void initTable() {
        TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayoutId);
        TableRow rowHeader = new TableRow(this);
        rowHeader.setBackgroundColor(Color.parseColor("#c0c0c0"));
        rowHeader.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        String headerText = "TradeIt ExampleApp";

        TextView tv = new TextView(this);
        tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(18);
        tv.setText(headerText);
        rowHeader.addView(tv);
        tableLayout.addView(rowHeader);

        addRow(tableLayout, "Link a broker via the oAuth flow", OAUTH_LINKED_A_BROKER_ID);
        addRow(tableLayout, "getLinkedBrokers", GET_LINKED_BROKERS_ID);
        addRow(tableLayout, "deleteAllLinkedBrokers", DELETE_ALL_LINKED_BROKERS_ID);

    }

    private void addRow(TableLayout tableLayout, String label, int id) {
        TableRow row= new TableRow(this);
        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        row.setBackgroundResource(R.drawable.row_border);
        TextView textView = new TextView(this);
        textView.setPadding(10, 10, 10, 10);
        textView.setText(label);
        row.addView(textView);
        row.setClickable(true);
        row.setId(id);
        row.setOnClickListener(rowListener);
        tableLayout.addView(row);
    }

    View.OnClickListener rowListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case OAUTH_LINKED_A_BROKER_ID:
                    Log.d(TAG, "Link a broker tapped!");
                    Intent intentOauth = new Intent(view.getContext(), OauthLinkBrokerActivity.class);
                    startActivity(intentOauth);
                    break;
                case GET_LINKED_BROKERS_ID:
                    Log.d(TAG, "Get linked brokers tapped!");
                    List<TradeItLinkedBroker> linkedBrokers = linkedBrokerManager.getLinkedBrokers();
                    Intent intent = new Intent(view.getContext(), LinkedBrokersActivity.class);
                    intent.putParcelableArrayListExtra(LINKED_BROKERS_PARAMETER, (ArrayList<? extends Parcelable>) linkedBrokers);
                    startActivity(intent);
                    break;
                case DELETE_ALL_LINKED_BROKERS_ID:
                    Log.d(TAG, "Delete all linked brokers tapped!");
                    deleteLinkedBrokers();
                    break;
                case AUTHENTICATE_FIRST_LINKED_BROKER:
                    Log.d(TAG, "Authenticate first linked broker tapped!");
                    authenticateFirstLinkedBroker();
                    break;
                default:
                    Log.e(TAG, "ERROR: no action found for id " + view.getId());
            }
        }
    };

    private void showAlert(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteLinkedBrokers() {
        List<TradeItLinkedBroker> linkedBrokersToDelete = linkedBrokerManager.getLinkedBrokers();
        if (linkedBrokersToDelete.isEmpty()) {
            String message = "No linked brokers to delete!: " + linkedBrokersToDelete.size();
            showAlert("Delete linked brokers", message);
        } else {
            Log.d(TAG, "# of linkedBrokers before deletion: " + linkedBrokersToDelete.size());

            for (final TradeItLinkedBroker linkedBroker: linkedBrokersToDelete) {
                linkedBrokerManager.unlinkBroker(linkedBroker, new TradeItCallBackImpl() {
                    @Override
                    public void onSuccess(Object type) {
                        Log.d(TAG, "# of linkedBrokers after deletion: " + linkedBrokerManager.getLinkedBrokers().size());
                    }

                    @Override
                    public void onError(TradeItErrorResult error) {
                        Log.e(TAG, "An error happened unlinking linkedBroker: " + linkedBroker + "\nError: " + error);
                    }
                });
            }
            //TODO see how to wait for all calls to be finished before calling the alert
            showAlert("Delete linked brokers", "# of linkedBrokers after deletion: " + linkedBrokerManager.getLinkedBrokers().size());
        }
    }

    private void authenticateFirstLinkedBroker() {
        List<TradeItLinkedBroker> linkedBrokers = linkedBrokerManager.getLinkedBrokers();
        if (linkedBrokers.isEmpty()) {
            String message = "No linked brokers to authenticate!: " + linkedBrokers.size();
            showAlert("Authenticate linked broker", message);
        } else {
            TradeItLinkedBroker linkedBroker = linkedBrokers.get(0);
            final MainActivity mainActivity = this;
            linkedBroker.authenticate(new TradeItCallbackWithSecurityQuestionImpl<List<TradeItLinkedBrokerAccount>>() {
                @Override
                public void onSuccess(final List<TradeItLinkedBrokerAccount> accounts) {
                    Intent intent = new Intent(mainActivity, LinkedBrokerAccountsActivity.class);
                    intent.putParcelableArrayListExtra(LINKED_BROKER_ACCOUNTS_PARAMETERS, (ArrayList<? extends Parcelable>)accounts);
                    startActivity(intent);
                }

                @Override
                public void onSecurityQuestion(TradeItSecurityQuestion securityQuestion) {
                    //TODO handle security question
                    //Log.d("ASDF", "AUTH SECURITY QUESTION!: " + securityQuestion.getSecurityQuestion());
                    // this.submitSecurityAnswer("my answer");
                }

                @Override
                public void onError(TradeItErrorResult error) {
                    showAlert("authenticateFirstLinkedBroker", "Error authenticating: " + error);
                }
            });
        }
    }
}
