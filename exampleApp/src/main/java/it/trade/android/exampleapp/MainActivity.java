package it.trade.android.exampleapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.trade.android.sdk.TradeItSDK;
import it.trade.android.sdk.enums.TradeItOrderExpiration;
import it.trade.android.sdk.enums.TradeItOrderPriceType;
import it.trade.android.sdk.manager.TradeItLinkedBrokerManager;
import it.trade.android.sdk.model.TradeItCallBackImpl;
import it.trade.android.sdk.model.TradeItCallbackWithSecurityQuestionImpl;
import it.trade.android.sdk.model.TradeItErrorResult;
import it.trade.android.sdk.model.TradeItLinkedBroker;
import it.trade.android.sdk.model.TradeItLinkedBrokerAccount;
import it.trade.android.sdk.model.TradeItOrder;
import it.trade.android.sdk.model.TradeItSecurityQuestion;
import it.trade.tradeitapi.model.TradeItEnvironment;
import it.trade.tradeitapi.model.TradeItGetAccountOverviewResponse;
import it.trade.tradeitapi.model.TradeItPosition;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    public final static String LINKED_BROKERS_PARAMETER = "it.trade.android.exampleapp.LINKED_BROKERS";
    public final static String LINKED_BROKER_ACCOUNTS_PARAMETER = "it.trade.android.exampleapp.LINKED_BROKER_ACCOUNTS";
    public final static String BALANCES_PARAMETER = "it.trade.android.exampleapp.BALANCES";
    public final static String POSITIONS_PARAMETER = "it.trade.android.exampleapp.POSITIONS";
    public final static String PREVIEW_ORDER_PARAMETER = "it.trade.android.exampleapp.PREVIEW_ORDER";

    private TradeItLinkedBrokerManager linkedBrokerManager;

    private static final int OAUTH_LINKED_A_BROKER_ID = 0;
    private static final int GET_LINKED_BROKERS_ID = 1;
    private static final int DELETE_ALL_LINKED_BROKERS_ID = 2;
    private static final int AUTHENTICATE_FIRST_LINKED_BROKER = 3;
    private static final int AUTHENTICATE_WITH_SECURITY_QUESTION_SIMPLE = 4;
    private static final int AUTHENTICATE_WITH_SECURITY_QUESTION_OPTIONS = 5;
    private static final int GET_BALANCES_FIRST_LINKED_BROKER_ACCOUNT = 6;
    private static final int GET_POSITIONS_FIRST_LINKED_BROKER_ACCOUNT = 7;
    private static final int PREVIEW_TRADE_FIRST_LINKED_BROKER_ACCOUNT = 8;

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
        addRow(tableLayout, "authenticateFirstLinkedBroker", AUTHENTICATE_FIRST_LINKED_BROKER);
        addRow(tableLayout, "SimpleSecurityQuestion", AUTHENTICATE_WITH_SECURITY_QUESTION_SIMPLE);
        addRow(tableLayout, "SecurityQuestionWithOptions", AUTHENTICATE_WITH_SECURITY_QUESTION_OPTIONS);
        addRow(tableLayout, "GetBalancesFirstLinkedBroker", GET_BALANCES_FIRST_LINKED_BROKER_ACCOUNT);
        addRow(tableLayout, "GetPositionsFirstLinkedBroker", GET_POSITIONS_FIRST_LINKED_BROKER_ACCOUNT);
        addRow(tableLayout, "PreviewAndPlaceTradeFirstLinkedBrokerAccount", PREVIEW_TRADE_FIRST_LINKED_BROKER_ACCOUNT);

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
                case AUTHENTICATE_WITH_SECURITY_QUESTION_SIMPLE:
                    Log.d(TAG, "Simple security question was tapped!");
                    authenticateWithSimpleSecurityQuestion();
                    break;
                case AUTHENTICATE_WITH_SECURITY_QUESTION_OPTIONS:
                    Log.d(TAG, "Security question with options was tapped!");
                    authenticateWithSecurityQuestionOptions();
                    break;
                case GET_BALANCES_FIRST_LINKED_BROKER_ACCOUNT:
                    Log.d(TAG, "get balances first linked broker account was tapped!");
                    getBalancesFirstLinkedBrokerAccount();
                    break;
                case GET_POSITIONS_FIRST_LINKED_BROKER_ACCOUNT:
                    Log.d(TAG, "get positions first linked broker account was tapped!");
                    getPositionsFirstLinkedBroker();
                    break;
                case PREVIEW_TRADE_FIRST_LINKED_BROKER_ACCOUNT:
                    Log.d(TAG, "preview trade first linked broker account was tapped!");
                    previewTradeFirstLinkedBroker();
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

    private void showSecurityQuestion(TradeItSecurityQuestion securityQuestion, EditText editText, DialogInterface.OnClickListener onSubmitListener) {
        final EditText input = editText;
        String message = !securityQuestion.getSecurityQuestionOptions().isEmpty() ? TextUtils.join("\n", securityQuestion.getSecurityQuestionOptions()) : "";
        new AlertDialog.Builder(this)
                .setTitle(securityQuestion.getSecurityQuestion())
                .setMessage(message)
                .setView(input)
                .setPositiveButton(android.R.string.ok, onSubmitListener)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
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
                    intent.putParcelableArrayListExtra(LINKED_BROKER_ACCOUNTS_PARAMETER, (ArrayList<? extends Parcelable>)accounts);
                    startActivity(intent);
                }

                @Override
                public void onSecurityQuestion(TradeItSecurityQuestion securityQuestion) {
                    final EditText input = new EditText(mainActivity);
                    final TradeItCallbackWithSecurityQuestionImpl securityQuestionImpl = this;
                    showSecurityQuestion(securityQuestion, input, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            securityQuestionImpl.submitSecurityAnswer(input.getText().toString());
                        }
                    });
                }

                @Override
                public void onError(TradeItErrorResult error) {
                    showAlert("authenticateFirstLinkedBroker", "Error authenticating: " + error);
                }
            });
        }
    }

    private void authenticateWithSimpleSecurityQuestion() {
        authenticateWithSecurityQuestion("dummySecurity");
    }

    private void authenticateWithSecurityQuestionOptions() {
        authenticateWithSecurityQuestion("dummyOption");
    }

    private void authenticateWithSecurityQuestion(String dummySecurityQuestionLogin) {
        final MainActivity mainActivity = this;
        final String dummyLogin = dummySecurityQuestionLogin;
        linkedBrokerManager.linkBroker(dummyLogin, "Dummy", dummyLogin, "dummy", new TradeItCallBackImpl<TradeItLinkedBroker>() {
            @Override
            public void onSuccess(final TradeItLinkedBroker linkedBroker) {
                linkedBroker.authenticate(new TradeItCallbackWithSecurityQuestionImpl<List<TradeItLinkedBrokerAccount>>() {
                    @Override
                    public void onSuccess(final List<TradeItLinkedBrokerAccount> accounts) {
                        showAlert("Dummy security", "Successfully Authenticate " + dummyLogin);
                        linkedBrokerManager.unlinkBroker(linkedBroker, new TradeItCallBackImpl() {
                            @Override
                            public void onSuccess(Object type) {
                                Log.d(TAG, "unlinking successfully " + dummyLogin);
                            }

                            @Override
                            public void onError(TradeItErrorResult error) {
                                Log.e(TAG, "ERROR unlinking " + dummyLogin);
                            }
                        });
                    }

                    @Override
                    public void onSecurityQuestion(TradeItSecurityQuestion securityQuestion) {
                        final EditText input = new EditText(mainActivity);
                        final TradeItCallbackWithSecurityQuestionImpl securityQuestionImpl = this;
                        showSecurityQuestion(securityQuestion, input, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                securityQuestionImpl.submitSecurityAnswer(input.getText().toString());
                            }
                        });
                    }

                    @Override
                    public void onError(TradeItErrorResult error) {
                        showAlert("authenticate " + dummyLogin, "Error authenticating: " + error);
                    }
                });
            }
            @Override
            public void onError(TradeItErrorResult error) {
                showAlert("authenticate " + dummyLogin, "Error linking "+ dummyLogin +": " + error);
            }
        });
    }

    private void getBalancesFirstLinkedBrokerAccount() {
        final MainActivity mainActivity = this;
        List<TradeItLinkedBroker> linkedBrokers = linkedBrokerManager.getLinkedBrokers();
        if (linkedBrokers.isEmpty()) {
            showAlert("getBalancesFirstLinkedBroker", "No linked broker!");
        } else if (linkedBrokers.get(0).getAccounts().isEmpty()) {
            showAlert("getBalancesFirstLinkedBroker", "No linked broker accounts! Did you authenticate before ?");
        } else {
            TradeItLinkedBroker linkedBroker = linkedBrokers.get(0);
            TradeItLinkedBrokerAccount linkedBrokerAccount = linkedBroker.getAccounts().get(0);
            linkedBrokerAccount.refreshBalance(new TradeItCallBackImpl<TradeItGetAccountOverviewResponse>() {
                @Override
                public void onSuccess(TradeItGetAccountOverviewResponse accountOverviewResponse) {
                    Intent intent = new Intent(mainActivity, BalancesActivity.class);
                    intent.putExtra(BALANCES_PARAMETER, accountOverviewResponse);
                    startActivity(intent);
                }

                @Override
                public void onError(TradeItErrorResult error) {
                    showAlert("getBalancesFirstLinkedBroker", "Error refreshing balances: " + error);
                }
            });
        }
    }

    private void getPositionsFirstLinkedBroker() {
        final MainActivity mainActivity = this;
        List<TradeItLinkedBroker> linkedBrokers = linkedBrokerManager.getLinkedBrokers();
        if (linkedBrokers.isEmpty()) {
            showAlert("getPositionsFirstLinkedBroker", "No linked broker!");
        } else if (linkedBrokers.get(0).getAccounts().isEmpty()) {
            showAlert("getPositionsFirstLinkedBroker", "No linked broker accounts! Did you authenticate before ?");
        } else {
            TradeItLinkedBroker linkedBroker = linkedBrokers.get(0);
            TradeItLinkedBrokerAccount linkedBrokerAccount = linkedBroker.getAccounts().get(0);
            linkedBrokerAccount.refreshPositions(new TradeItCallBackImpl<List<TradeItPosition>>() {
                @Override
                public void onSuccess(List<TradeItPosition> positions) {
                    Intent intent = new Intent(mainActivity, PositionsActivity.class);
                    intent.putParcelableArrayListExtra(POSITIONS_PARAMETER, (ArrayList<? extends Parcelable>) positions);
                    startActivity(intent);
                }

                @Override
                public void onError(TradeItErrorResult error) {
                    showAlert("getBalancesFirstLinkedBroker", "Error refreshing balances: " + error);
                }
            });
        }
    }

    private void previewTradeFirstLinkedBroker() {
        final MainActivity mainActivity = this;
        List<TradeItLinkedBroker> linkedBrokers = linkedBrokerManager.getLinkedBrokers();
        if (linkedBrokers.isEmpty()) {
            showAlert("previewTradeFirstLinkedBroker", "No linked broker!");
        } else if (linkedBrokers.get(0).getAccounts().isEmpty()) {
            showAlert("previewTradeFirstLinkedBroker", "No linked broker accounts! Did you authenticate before ?");
        } else {
            final TradeItOrder order = new TradeItOrder(linkedBrokers.get(0).getAccounts().get(0), "GE");
            order.setLimitPrice(20.0);
            order.setExpiration(TradeItOrderExpiration.GOOD_FOR_DAY);
            order.setPriceType(TradeItOrderPriceType.LIMIT);
            Intent intent = new Intent(mainActivity, PreviewOrderActivity.class);
            intent.putExtra(PREVIEW_ORDER_PARAMETER, order);
            startActivity(intent);
        }
    }
}
