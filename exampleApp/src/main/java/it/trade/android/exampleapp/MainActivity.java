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
import it.trade.android.sdk.model.TradeItBalanceParcelable;
import it.trade.android.sdk.model.TradeItCallBackCompletion;
import it.trade.android.sdk.model.TradeItCallbackWithSecurityQuestionAndCompletion;
import it.trade.android.sdk.model.TradeItLinkedBrokerAccountParcelable;
import it.trade.android.sdk.model.TradeItLinkedBrokerParcelable;
import it.trade.android.sdk.model.TradeItOrderParcelable;
import it.trade.android.sdk.model.TradeItPositionParcelable;
import it.trade.model.TradeItErrorResult;
import it.trade.model.TradeItSecurityQuestion;
import it.trade.model.callback.TradeItCallback;
import it.trade.model.callback.TradeItCallbackWithSecurityQuestionImpl;
import it.trade.model.request.TradeItEnvironment;

import static it.trade.android.exampleapp.MainActivity.MainActivityActions.AUTHENTICATE_ALL_LINKED_BROKERS;
import static it.trade.android.exampleapp.MainActivity.MainActivityActions.AUTHENTICATE_FIRST_LINKED_BROKER;
import static it.trade.android.exampleapp.MainActivity.MainActivityActions.AUTHENTICATE_WITH_SECURITY_QUESTION_OPTIONS;
import static it.trade.android.exampleapp.MainActivity.MainActivityActions.AUTHENTICATE_WITH_SECURITY_QUESTION_SIMPLE;
import static it.trade.android.exampleapp.MainActivity.MainActivityActions.DELETE_ALL_LINKED_BROKERS;
import static it.trade.android.exampleapp.MainActivity.MainActivityActions.GET_BALANCES_FIRST_LINKED_BROKER_ACCOUNT;
import static it.trade.android.exampleapp.MainActivity.MainActivityActions.GET_LINKED_BROKERS;
import static it.trade.android.exampleapp.MainActivity.MainActivityActions.GET_POSITIONS_FIRST_LINKED_BROKER_ACCOUNT;
import static it.trade.android.exampleapp.MainActivity.MainActivityActions.OAUTH_LINKED_A_BROKER;
import static it.trade.android.exampleapp.MainActivity.MainActivityActions.PREVIEW_TRADE_FIRST_LINKED_BROKER_ACCOUNT;
import static it.trade.android.exampleapp.MainActivity.MainActivityActions.REFRESH_ALL_BALANCES_ALL_LINKED_BROKERS;
import static it.trade.android.exampleapp.MainActivity.MainActivityActions.REFRESH_ALL_BALANCES_FIRST_LINKED_BROKER;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    public final static String LINKED_BROKERS_PARAMETER = "it.trade.android.exampleapp.LINKED_BROKERS";
    public final static String LINKED_BROKER_ACCOUNTS_PARAMETER = "it.trade.android.exampleapp.LINKED_BROKER_ACCOUNTS";
    public final static String BALANCES_PARAMETER = "it.trade.android.exampleapp.BALANCES";
    public final static String POSITIONS_PARAMETER = "it.trade.android.exampleapp.POSITIONS";
    public final static String PREVIEW_ORDER_PARAMETER = "it.trade.android.exampleapp.PREVIEW_ORDER";

    private TradeItLinkedBrokerManager linkedBrokerManager;

    public enum MainActivityActions {
        OAUTH_LINKED_A_BROKER("Link a broker via the oAuth flow"),
        GET_LINKED_BROKERS("getLinkedBrokers"),
        DELETE_ALL_LINKED_BROKERS("Delete all linked brokers"),
        AUTHENTICATE_FIRST_LINKED_BROKER("Authenticate first linked broker"),
        AUTHENTICATE_ALL_LINKED_BROKERS("Authenticate all linked brokers"),
        AUTHENTICATE_WITH_SECURITY_QUESTION_SIMPLE("Simple security question"),
        AUTHENTICATE_WITH_SECURITY_QUESTION_OPTIONS("Security question with options"),
        REFRESH_ALL_BALANCES_ALL_LINKED_BROKERS("Refresh all balances for all linked broker"),
        REFRESH_ALL_BALANCES_FIRST_LINKED_BROKER("Refresh all balances for first linked broker"),
        GET_BALANCES_FIRST_LINKED_BROKER_ACCOUNT("Get balances for first linked broker account"),
        GET_POSITIONS_FIRST_LINKED_BROKER_ACCOUNT("Get positions for first linked broker account"),
        PREVIEW_TRADE_FIRST_LINKED_BROKER_ACCOUNT("Preview trade for first linked broker account");

        private String label;
        MainActivityActions(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initTable();

        //TradeItSDK.configure(this.getApplicationContext(), "tradeit-test-api-key", TradeItEnvironment.QA);
        TradeItSDK.configure(this.getApplicationContext(), "tradeit-test-api-key", TradeItEnvironment.QA, new RequestCookieProviderParcelableImpl());
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

        addRow(tableLayout, OAUTH_LINKED_A_BROKER.label, OAUTH_LINKED_A_BROKER.ordinal());
        addRow(tableLayout, GET_LINKED_BROKERS.label, GET_LINKED_BROKERS.ordinal());
        addRow(tableLayout, DELETE_ALL_LINKED_BROKERS.label, DELETE_ALL_LINKED_BROKERS.ordinal());
        addRow(tableLayout, AUTHENTICATE_FIRST_LINKED_BROKER.label, AUTHENTICATE_FIRST_LINKED_BROKER.ordinal());
        addRow(tableLayout, AUTHENTICATE_ALL_LINKED_BROKERS.label, AUTHENTICATE_ALL_LINKED_BROKERS.ordinal());
        addRow(tableLayout, GET_BALANCES_FIRST_LINKED_BROKER_ACCOUNT.label, GET_BALANCES_FIRST_LINKED_BROKER_ACCOUNT.ordinal());
        addRow(tableLayout, REFRESH_ALL_BALANCES_FIRST_LINKED_BROKER.label, REFRESH_ALL_BALANCES_FIRST_LINKED_BROKER.ordinal());
        addRow(tableLayout, REFRESH_ALL_BALANCES_ALL_LINKED_BROKERS.label, REFRESH_ALL_BALANCES_ALL_LINKED_BROKERS.ordinal());
        addRow(tableLayout, GET_POSITIONS_FIRST_LINKED_BROKER_ACCOUNT.label, GET_POSITIONS_FIRST_LINKED_BROKER_ACCOUNT.ordinal());
        addRow(tableLayout, PREVIEW_TRADE_FIRST_LINKED_BROKER_ACCOUNT.label, PREVIEW_TRADE_FIRST_LINKED_BROKER_ACCOUNT.ordinal());
        addRow(tableLayout, AUTHENTICATE_WITH_SECURITY_QUESTION_SIMPLE.label, AUTHENTICATE_WITH_SECURITY_QUESTION_SIMPLE.ordinal());
        addRow(tableLayout, AUTHENTICATE_WITH_SECURITY_QUESTION_OPTIONS.label, AUTHENTICATE_WITH_SECURITY_QUESTION_OPTIONS.ordinal());
    }

    private void addRow(TableLayout tableLayout, String label, int id) {
        TableRow row = new TableRow(this);
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
            switch (MainActivityActions.values()[view.getId()]) {
                case OAUTH_LINKED_A_BROKER:
                    Log.d(TAG, "Link a broker tapped!");
                    Intent intentOauth = new Intent(view.getContext(), OauthLinkBrokerActivity.class);
                    startActivity(intentOauth);
                    break;
                case GET_LINKED_BROKERS:
                    Log.d(TAG, "Get linked brokers tapped!");
                    goToLinkedBrokersActivity();
                    break;
                case DELETE_ALL_LINKED_BROKERS:
                    Log.d(TAG, "Delete all linked brokers tapped!");
                    deleteLinkedBrokers();
                    break;
                case AUTHENTICATE_FIRST_LINKED_BROKER:
                    Log.d(TAG, "Authenticate first linked broker tapped!");
                    authenticateLinkedBroker(0);
                    break;
                case AUTHENTICATE_ALL_LINKED_BROKERS:
                    Log.d(TAG, "Authenticate all linked brokers tapped!");
                    authenticateAllLinkedBrokers();
                    break;
                case AUTHENTICATE_WITH_SECURITY_QUESTION_SIMPLE:
                    Log.d(TAG, "Simple security question was tapped!");
                    authenticateWithSimpleSecurityQuestion();
                    break;
                case AUTHENTICATE_WITH_SECURITY_QUESTION_OPTIONS:
                    Log.d(TAG, "Security question with options was tapped!");
                    authenticateWithSecurityQuestionOptions();
                    break;
                case REFRESH_ALL_BALANCES_FIRST_LINKED_BROKER:
                    Log.d(TAG, "Refresh all balances for first linked broker was tapped!");
                    refreshAllBalancesFirstLinkedBroker();
                    break;
                case REFRESH_ALL_BALANCES_ALL_LINKED_BROKERS:
                    Log.d(TAG, "refresh balances for all linked brokers was tapped!");
                    refreshBalancesForAllLinkedBrokers();
                    break;
                case GET_BALANCES_FIRST_LINKED_BROKER_ACCOUNT:
                    Log.d(TAG, "get balances for first linked broker was tapped!");
                    getBalancesForFirstLinkedBrokerAccount();
                    break;
                case GET_POSITIONS_FIRST_LINKED_BROKER_ACCOUNT:
                    Log.d(TAG, "get positions first linked broker was tapped!");
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

    private void showSecurityQuestion(TradeItSecurityQuestion securityQuestion, EditText editText, DialogInterface.OnClickListener onSubmitListener, DialogInterface.OnClickListener onCancelListener) {
        final EditText input = editText;
        String message = !securityQuestion.getSecurityQuestionOptions().isEmpty() ? TextUtils.join("\n", securityQuestion.getSecurityQuestionOptions()) : "";
        new AlertDialog.Builder(this)
                .setTitle(securityQuestion.getSecurityQuestion())
                .setMessage(message)
                .setView(input)
                .setPositiveButton(android.R.string.ok, onSubmitListener)
                .setNegativeButton(android.R.string.cancel, onCancelListener)
                .show();
    }

    private void deleteLinkedBrokers() {
        List<TradeItLinkedBrokerParcelable> linkedBrokersToDelete = linkedBrokerManager.getLinkedBrokers();
        if (linkedBrokersToDelete.isEmpty()) {
            String message = "No linked brokers to delete!: " + linkedBrokersToDelete.size();
            showAlert("Delete linked brokers", message);
        } else {
            Log.d(TAG, "# of linkedBrokers before deletion: " + linkedBrokersToDelete.size());

            for (final TradeItLinkedBrokerParcelable linkedBroker : linkedBrokersToDelete) {
                linkedBrokerManager.unlinkBroker(linkedBroker, new TradeItCallback() {
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
            //TODO: see how to wait for all calls to be finished before calling the alert
            showAlert("Delete linked brokers", "# of linkedBrokers after deletion: " + linkedBrokerManager.getLinkedBrokers().size());
        }
    }

    private void authenticateAllLinkedBrokers() {
        final MainActivity mainActivity = this;
        linkedBrokerManager.authenticateAll(new TradeItCallbackWithSecurityQuestionAndCompletion() {
            @Override
            public void onFinished() {
                Log.d(TAG, "authenticateAllLinkedBrokers - onFinished");
                goToLinkedBrokersActivity();
            }

            @Override
            public void onSecurityQuestion(TradeItSecurityQuestion securityQuestion, final TradeItCallbackWithSecurityQuestionImpl callback) {
                final EditText input = new EditText(mainActivity);
                showSecurityQuestion(securityQuestion, input, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.submitSecurityAnswer(input.getText().toString());
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.cancelSecurityQuestion();
                    }
                });
            }
        });
    }

    private void authenticateLinkedBroker(int index) {
        List<TradeItLinkedBrokerParcelable> linkedBrokers = linkedBrokerManager.getLinkedBrokers();
        if (linkedBrokers.isEmpty() || linkedBrokers.size() < (index + 1)) {
            String message = "No linked broker to authenticate! Index: " + index + ", linked broker count: " + linkedBrokers.size();
            showAlert("Authenticate linked broker", message);
        } else {
            TradeItLinkedBrokerParcelable linkedBroker = linkedBrokers.get(index);
            final MainActivity mainActivity = this;
                linkedBroker.authenticate(new TradeItCallbackWithSecurityQuestionImpl<List<TradeItLinkedBrokerAccountParcelable>>() {
                @Override
                public void onSuccess(final List<TradeItLinkedBrokerAccountParcelable> accounts) {
                    goToLinkedBrokerAccountsActivity(accounts);
                }

                @Override
                public void onSecurityQuestion(TradeItSecurityQuestion securityQuestion) {
                    final EditText input = new EditText(mainActivity);
                    final TradeItCallbackWithSecurityQuestionImpl securityQuestionImpl = this;
                    showSecurityQuestion(securityQuestion, input,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    securityQuestionImpl.submitSecurityAnswer(input.getText().toString());
                                }
                            },
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    securityQuestionImpl.cancelSecurityQuestion();
                                }
                            }
                    );
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
        linkedBrokerManager.linkBroker(dummyLogin, "Dummy", dummyLogin, "dummy", new TradeItCallback<TradeItLinkedBrokerParcelable>() {
            @Override
            public void onSuccess(final TradeItLinkedBrokerParcelable linkedBroker) {
                linkedBroker.authenticate(new TradeItCallbackWithSecurityQuestionImpl<List<TradeItLinkedBrokerAccountParcelable>>() {
                    @Override
                    public void onSuccess(final List<TradeItLinkedBrokerAccountParcelable> accounts) {
                        showAlert("Dummy security", "Successfully Authenticate " + dummyLogin);
                        linkedBrokerManager.unlinkBroker(linkedBroker, new TradeItCallback() {
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
                        showSecurityQuestion(securityQuestion, input,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        securityQuestionImpl.submitSecurityAnswer(input.getText().toString());
                                    }
                                },
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        securityQuestionImpl.cancelSecurityQuestion();
                                    }
                                }
                                );
                    }

                    @Override
                    public void onError(TradeItErrorResult error) {
                        showAlert("authenticate " + dummyLogin, "Error authenticating: " + error);
                    }
                });
            }

            @Override
            public void onError(TradeItErrorResult error) {
                showAlert("authenticate " + dummyLogin, "Error linking " + dummyLogin + ": " + error);
            }
        });
    }

    private void getBalancesForFirstLinkedBrokerAccount() {
        final MainActivity mainActivity = this;
        List<TradeItLinkedBrokerParcelable> linkedBrokers = linkedBrokerManager.getLinkedBrokers();
        if (linkedBrokers.isEmpty()) {
            showAlert("getBalancesFirstLinkedBroker", "No linked broker!");
        } else if (linkedBrokers.get(0).getAccounts().isEmpty()) {
            showAlert("getBalancesFirstLinkedBroker", "No linked broker accounts detected for first linked broker! Try authenticating.");
        } else {
            TradeItLinkedBrokerParcelable linkedBroker = linkedBrokers.get(0);
            TradeItLinkedBrokerAccountParcelable linkedBrokerAccount = linkedBroker.getAccounts().get(0);
            linkedBrokerAccount.refreshBalance(new TradeItCallback<TradeItBalanceParcelable>() {
                @Override
                public void onSuccess(TradeItBalanceParcelable balance) {
                    Intent intent = new Intent(mainActivity, BalancesActivity.class);
                    intent.putExtra(BALANCES_PARAMETER, balance);
                    startActivity(intent);
                }

                @Override
                public void onError(TradeItErrorResult error) {
                    showAlert("getBalancesFirstLinkedBroker", "Error refreshing balances: " + error);
                }
            });
        }
    }

    private void refreshAllBalancesFirstLinkedBroker() {
        final MainActivity mainActivity = this;
        List<TradeItLinkedBrokerParcelable> linkedBrokers = linkedBrokerManager.getLinkedBrokers();
        if (linkedBrokers.isEmpty()) {
            showAlert("refreshAllBalancesFirstLinkedBroker", "No linked broker!");
        } else if (linkedBrokers.get(0).getAccounts().isEmpty()) {
            showAlert("refreshAllBalancesFirstLinkedBroker", "No linked broker accounts detected for first linked broker! Try authenticating.");
        } else {
            final TradeItLinkedBrokerParcelable linkedBroker = linkedBrokers.get(0);
            linkedBroker.refreshAccountBalances(new TradeItCallBackCompletion() {
                @Override
                public void onFinished() {
                    goToLinkedBrokerAccountsActivity(linkedBroker.getAccounts());
                }
            });
        }
    }

    private void refreshBalancesForAllLinkedBrokers() {
        List<TradeItLinkedBrokerParcelable> linkedBrokers = linkedBrokerManager.getLinkedBrokers();
        if (linkedBrokers.isEmpty()) {
            showAlert("refreshAllBalancesFirstLinkedBroker", "No linked broker!");
        } else if (linkedBrokers.get(0).getAccounts().isEmpty()) {
            showAlert("refreshAllBalancesFirstLinkedBroker", "No linked broker accounts detected for first linked broker! Try authenticating.");
        } else {
            final TradeItLinkedBrokerParcelable linkedBroker = linkedBrokers.get(0);
            linkedBrokerManager.refreshAccountBalances(new TradeItCallBackCompletion() {
                @Override
                public void onFinished() {
                    goToLinkedBrokersActivity();
                }
            });
        }
    }

    private void getPositionsFirstLinkedBroker() {
        final MainActivity mainActivity = this;
        List<TradeItLinkedBrokerParcelable> linkedBrokers = linkedBrokerManager.getLinkedBrokers();
        if (linkedBrokers.isEmpty()) {
            showAlert("getPositionsFirstLinkedBroker", "No linked broker!");
        } else if (linkedBrokers.get(0).getAccounts().isEmpty()) {
            showAlert("getPositionsFirstLinkedBroker", "No linked broker accounts detected for first linked broker! Try authenticating.");
        } else {
            TradeItLinkedBrokerParcelable linkedBroker = linkedBrokers.get(0);
            for (TradeItLinkedBrokerAccountParcelable linkedBrokerAccount : linkedBroker.getAccounts()) {
                linkedBrokerAccount.refreshPositions(new TradeItCallback<List<TradeItPositionParcelable>>() {
                    @Override
                    public void onSuccess(List<TradeItPositionParcelable> positions) {
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
    }

    private void previewTradeFirstLinkedBroker() {
        final MainActivity mainActivity = this;
        List<TradeItLinkedBrokerParcelable> linkedBrokers = linkedBrokerManager.getLinkedBrokers();
        if (linkedBrokers.isEmpty()) {
            showAlert("previewTradeFirstLinkedBroker", "No linked broker!");
        } else if (linkedBrokers.get(0).getAccounts().isEmpty()) {
            showAlert("previewTradeFirstLinkedBroker", "No linked broker accounts detected for first linked broker! Try authenticating.");
        } else {
            final TradeItOrderParcelable order = new TradeItOrderParcelable(linkedBrokers.get(0).getAccounts().get(0), "GE");
            order.setLimitPrice(20.0);
            order.setExpiration(TradeItOrderExpiration.GOOD_FOR_DAY);
            order.setPriceType(TradeItOrderPriceType.LIMIT);
            Intent intent = new Intent(mainActivity, PreviewOrderActivity.class);
            intent.putExtra(PREVIEW_ORDER_PARAMETER, order);
            startActivity(intent);
        }
    }

    private void goToLinkedBrokersActivity() {
        Intent intent = new Intent(this.getApplicationContext(), LinkedBrokersActivity.class);
        intent.putParcelableArrayListExtra(LINKED_BROKERS_PARAMETER, (ArrayList<? extends Parcelable>) linkedBrokerManager.getLinkedBrokers());
        startActivity(intent);
    }

    private void goToLinkedBrokerAccountsActivity(List<TradeItLinkedBrokerAccountParcelable> accounts) {
        Intent intent = new Intent(this.getApplicationContext(), LinkedBrokerAccountsActivity.class);
        intent.putParcelableArrayListExtra(LINKED_BROKER_ACCOUNTS_PARAMETER, (ArrayList<? extends Parcelable>) accounts);
        startActivity(intent);
    }
}
