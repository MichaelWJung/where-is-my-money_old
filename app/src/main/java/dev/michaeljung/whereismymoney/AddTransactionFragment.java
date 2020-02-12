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

public class AddTransactionFragment extends CljsFragment {
    private EditText description;
    private EditText date;
    private EditText amount;
    private Spinner account;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_transaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        description = view.findViewById(R.id.transaction_description);
        date = view.findViewById(R.id.transaction_date);
        amount = view.findViewById(R.id.transaction_amount);
        account = view.findViewById(R.id.transaction_account);

        Button cancelButton = view.findViewById(R.id.button_transaction_cancel);
        cancelButton.setOnClickListener(v -> dispatch("close-transaction-screen"));
    }
}
