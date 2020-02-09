package dev.michaeljung.whereismymoney;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

public class AccountOverviewFragment extends CljsFragment implements TransactionListAdapter.Callback {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView transactionList = view.findViewById(R.id.transactions_view);
        TransactionListAdapter adapter = new TransactionListAdapter(getContext(), this);
        transactionList.setAdapter(adapter);
        transactionList.setLayoutManager(new LinearLayoutManager(getContext()));

        subscribe("account-overview", payload -> {
            try {
                JSONArray transactions = payload.getJSONArray("value");
                adapter.setTransactions(transactions);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void removeTransaction(int id) {
        Log.d("xyz", "removing tr: " + Integer.toString(id));
        JSONArray event = new JSONArray();
        event.put("remove-transaction");
        event.put(id);
        dispatch(event);
    }
}
