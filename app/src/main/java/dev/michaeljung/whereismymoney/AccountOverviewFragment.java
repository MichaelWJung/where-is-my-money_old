package dev.michaeljung.whereismymoney;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class AccountOverviewFragment extends CljsFragment implements TransactionListAdapter.Callback,
        BackButtonListener {

    interface Callbacks {
        void setAccountFragmentTitle(String title);
    }

    private Spinner accounts;
    private ArrayAdapter<String> accountAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Callbacks callbacks = (Callbacks) getActivity();

        accounts = view.findViewById(R.id.account_spinner);

        RecyclerView transactionList = view.findViewById(R.id.transactions_view);
        TransactionListAdapter adapter = new TransactionListAdapter(getContext(), this);
        transactionList.setAdapter(adapter);
        transactionList.setLayoutManager(new LinearLayoutManager(getContext()));

        FloatingActionButton floatingActionButton = view.findViewById(R.id.button_add_transaction);
        floatingActionButton.setOnClickListener(
                v -> dispatch("new-transaction"));

        initializeAccountSpinner();

        subscribe("account-overview", payload -> {
            try {
                JSONArray transactions = payload.getJSONArray("value");
                adapter.setTransactions(transactions);
                callbacks.setAccountFragmentTitle("Account xyz");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        subscribe("account-names", payload -> {
            try {
                accountAdapter.clear();
                accountAdapter.addAll(toList(payload.getJSONArray("value")));
                //accounts.setSelection(value.getInt("selected-account"));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void removeTransaction(int id) {
        JSONArray event = new JSONArray();
        event.put("edit-transaction");
        event.put(id);
        dispatch(event);
    }

    @Override
    public void onBackButtonClicked() {

    }

    // TODO: Remove duplicate function
    private List<String> toList(JSONArray accountsJson) throws JSONException {
        ArrayList<String> accounts = new ArrayList<>();
        for (int i = 0; i < accountsJson.length(); i++) {
            accounts.add(accountsJson.getString(i));
        }
        return accounts;
    }

    private void initializeAccountSpinner() {
        accountAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item);
        accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accounts.setAdapter(accountAdapter);
    }
}
