package dev.michaeljung.whereismymoney;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Date;
import java.util.List;

public class AddTransactionFragment extends CljsFragment implements BackButtonListener, DatePickerDialog.OnDateSetListener {
    private EditText description;
    private TextView date;
    private EditText amount;
    private Spinner account;
    private Calendar calendar;

    interface Callbacks {
        void setTransactionFragmentTitle(String title);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_transaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Callbacks callbacks = (Callbacks) getActivity();
        calendar = Calendar.getInstance();

        description = view.findViewById(R.id.transaction_description);
        date = view.findViewById(R.id.transaction_date);
        amount = view.findViewById(R.id.transaction_amount);
        account = view.findViewById(R.id.transaction_account);

        ArrayAdapter<String> accountAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item);
        accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        account.setAdapter(accountAdapter);

        date.setOnClickListener(v -> {
            DatePickerFragment datePickerFragment = new DatePickerFragment(calendar);
            datePickerFragment.setTargetFragment(this, 0);
            datePickerFragment.show(getFragmentManager(), "xyz");
        });

        Button okButton = view.findViewById(R.id.button_transaction_save);
        okButton.setOnClickListener(v -> {
            dispatchTransactionData();
            dispatch("save-transaction");
            closeTransactionScreen();
        });

        Button cancelButton = view.findViewById(R.id.button_transaction_cancel);
        cancelButton.setOnClickListener(v -> closeTransactionScreen());

        subscribe("transaction-screen", payload -> {
            try {
                JSONObject value = payload.getJSONObject("value");
                description.setText(value.getString("description"));
                amount.setText(value.getString("amount"));
                okButton.setText(value.getString("ok-button-text"));
                callbacks.setTransactionFragmentTitle(value.getString("screen-title"));
                calendar.setTimeInMillis(value.getLong("date"));
                accountAdapter.clear();
                accountAdapter.addAll(toList(value.getJSONArray("accounts")));
                account.setSelection(value.getInt("selected-account"));
                updateDateView();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private List<String> toList(JSONArray accountsJson) throws JSONException {
        ArrayList<String> accounts = new ArrayList<>();
        for (int i = 0; i < accountsJson.length(); i++) {
            accounts.add(accountsJson.getString(i));
        }
        return accounts;
    }

    @Override
    public void onStop() {
        dispatchTransactionData();
        super.onStop();
    }

    private void dispatchTransactionData() {
        try {
            JSONObject transactionData = new JSONObject();
            transactionData.put("description", description.getText());
            transactionData.put("date", calendar.getTimeInMillis());
            transactionData.put("amount", amount.getText());
            transactionData.put("account-id", account.getSelectedItemPosition());
            JSONArray event = new JSONArray();
            event.put("update-transaction-data");
            event.put(transactionData);
            dispatch(event);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendar.set(year, month, dayOfMonth);
        updateDateView();
    }

    private void updateDateView() {
        Activity activity = getActivity();
        if (activity != null) {
            Date date = calendar.getTime();
            DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(activity);
            this.date.setText(dateFormat.format(date));
        }
    }

    @Override
    public void onBackButtonClicked() {
        closeTransactionScreen();
    }

    private void closeTransactionScreen() {
        dispatch("close-transaction-screen");
    }
}
