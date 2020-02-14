package dev.michaeljung.whereismymoney;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

public class AddTransactionFragment extends CljsFragment implements BackButtonListener {
    private EditText description;
    private EditText date;
    private EditText amount;
    private Spinner account;

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

        description = view.findViewById(R.id.transaction_description);
        date = view.findViewById(R.id.transaction_date);
        amount = view.findViewById(R.id.transaction_amount);
        account = view.findViewById(R.id.transaction_account);

        Button okButton = view.findViewById(R.id.button_transaction_save);

        subscribe("transaction-screen", payload -> {
            try {
                JSONObject value = payload.getJSONObject("value");
                description.setText(value.getString("description"));
                amount.setText(value.getString("amount"));
                okButton.setText(value.getString("ok-button-text"));
                callbacks.setTransactionFragmentTitle(value.getString("screen-title"));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        Button cancelButton = view.findViewById(R.id.button_transaction_cancel);
        cancelButton.setOnClickListener(v -> closeTransactionScreen());
    }

    @Override
    public void onBackButtonClicked() {
        closeTransactionScreen();
    }

    void closeTransactionScreen() {
        dispatch("close-transaction-screen");
    }
}
