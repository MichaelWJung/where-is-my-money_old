package dev.michaeljung.whereismymoney;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TransactionFragment extends CljsFragment implements BackButtonListener, DatePickerDialog.OnDateSetListener {
    private static final String LOG_TAG = "TransactionFragment";
    private static final String DATE_PICKER_TAG = "DATE_PICKER";
    private EditText description;
    private TextView date;
    private EditText amount;
    private Spinner account;
    private Calendar calendar = Calendar.getInstance();
    private ArrayAdapter<String> accountAdapter;
    private Callbacks callbacks;
    private Button okButton;
    private Button cancelButton;

    interface Callbacks {
        void setTransactionFragmentTitle(String title);
    }

    public TransactionFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        callbacks = (Callbacks) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViewFields(view);
        initializeAccountSpinner();
        setOnClickListeners();
        subscribe("transaction-screen", this::updateViews);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        //account.setOnItemSelectedListener(null);
        super.onStop();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendar.set(year, month, dayOfMonth);
        updateDateView();
        dispatchTransactionData();
        account.requestFocus();
        account.performClick();
    }

    @Override
    public void onBackButtonClicked() {
        closeTransactionScreen();
    }

    private void initializeViewFields(@NonNull View view) {
        description = view.findViewById(R.id.transaction_description);
        date = view.findViewById(R.id.transaction_date);
        amount = view.findViewById(R.id.transaction_amount);
        account = view.findViewById(R.id.transaction_account);
        okButton = view.findViewById(R.id.button_transaction_save);
        cancelButton = view.findViewById(R.id.button_transaction_cancel);
    }

    private void initializeAccountSpinner() {
        accountAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item);
        accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        account.setAdapter(accountAdapter);
    }

    private void setOnClickListeners() {
        date.setOnClickListener(v -> openDatePickerDialog());
        okButton.setOnClickListener(v -> submitTransaction());
        cancelButton.setOnClickListener(v -> closeTransactionScreen());
        description.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                dispatchTransactionData();
            }
        });
        amount.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                dispatchTransactionData();
            }
        });
        date.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                v.performClick();
            }
        });
        account.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dispatchTransactionData();
                amount.requestFocus();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void openDatePickerDialog() {
        DatePickerFragment datePickerFragment = new DatePickerFragment(calendar);
        datePickerFragment.show(getChildFragmentManager(), DATE_PICKER_TAG);
    }

    private void submitTransaction() {
        dispatchTransactionData();
        dispatch("save-transaction");
    }

    private void dispatchTransactionData() {
        try {
            JSONArray event = new JSONArray();
            event.put("update-transaction-data");
            event.put(buildTransactionDataObject());
            dispatch(event);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private JSONObject buildTransactionDataObject() throws JSONException {
        JSONObject transactionData = new JSONObject();
        transactionData.put("description", description.getText());
        transactionData.put("date", calendar.getTimeInMillis());
        transactionData.put("amount", amount.getText());
        // TODO: What if no account is selected??
        transactionData.put("account-idx", account.getSelectedItemPosition());
        return transactionData;
    }

    private void updateDateView() {
        Activity activity = getActivity();
        if (activity != null) {
            DateFormat dateFormat = android.text.format.DateFormat.getLongDateFormat(activity);
            date.setText(dateFormat.format(calendar.getTime()));
        }
    }

    private void updateViews(JSONObject payload) {
        try {
            JSONObject value = payload.getJSONObject("value");

            setTextWithoutFocus(description, value.getString("description"));
            setTextWithoutFocus(amount, value.getString("amount"));
            okButton.setText(value.getString("ok-button-text"));
            callbacks.setTransactionFragmentTitle(value.getString("screen-title"));

            accountAdapter.clear();
            accountAdapter.addAll(toList(value.getJSONArray("accounts")));
            account.setSelection(value.getInt("selected-account"));

            calendar.setTimeInMillis(value.getLong("date"));
            updateDateView();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void setTextWithoutFocus(EditText editText, String newText) {
        if (!editText.getText().toString().equals(newText)) {
            boolean focused = editText.hasFocus();
            if (focused) {
                editText.clearFocus();
            }
            editText.setText(newText);
            if (focused) {
                editText.requestFocus();
            }
        }
    }

    private List<String> toList(JSONArray accountsJson) throws JSONException {
        ArrayList<String> accounts = new ArrayList<>();
        for (int i = 0; i < accountsJson.length(); i++) {
            accounts.add(accountsJson.getString(i));
        }
        return accounts;
    }

    private void closeTransactionScreen() {
        dispatch("close-transaction-screen");
    }
}
