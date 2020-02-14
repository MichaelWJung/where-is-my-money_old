package dev.michaeljung.whereismymoney;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TransactionListAdapter extends RecyclerView.Adapter<TransactionListAdapter.TransactionViewHolder> {
    private static final String LOG_TAG = TransactionListAdapter.class.getSimpleName();

    private JSONArray transactionList;
    private LayoutInflater inflater;
    private Callback callback;

    interface Callback {
        void removeTransaction(int id);
    }

    TransactionListAdapter(Context context, Callback callback) {
        inflater = LayoutInflater.from(context);
        transactionList = new JSONArray();
        this.callback = callback;
    }

    public void setTransactions(JSONArray transactions) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new TransactionDiffCallback(transactionList, transactions));
        diffResult.dispatchUpdatesTo(this);
        transactionList = transactions;
    }

    @NonNull
    @Override
    public TransactionListAdapter.TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.transactionlist_item, parent, false);
        return new TransactionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionListAdapter.TransactionViewHolder holder, int position) {
        try {
            JSONObject current = transactionList.getJSONObject(position);
            holder.setValues(current);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    /*@Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            final Bundle diff = (Bundle) payloads.get(0);
            for (String key : diff.keySet()) {
                switch (key) {

                }
            }
        }
    }*/

    @Override
    public int getItemCount() {
        return transactionList.length();
    }

    class TransactionViewHolder extends RecyclerView.ViewHolder {
        private final View view;
        private int id;
        private final TextView descriptionView;
        private final TextView amountView;
        private final TextView accountView;
        private final TextView dateView;
        private final TextView balanceView;

        TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            descriptionView = itemView.findViewById(R.id.description);
            amountView = itemView.findViewById(R.id.amount);
            accountView = itemView.findViewById(R.id.account);
            dateView = itemView.findViewById(R.id.date);
            balanceView = itemView.findViewById(R.id.balance);
        }

        void setValues(JSONObject values) {
            try {
                id = values.getInt("id");
                descriptionView.setText(values.getString("description"));
                amountView.setText(values.getString("amount"));
                accountView.setText(values.getString("account"));
                dateView.setText(values.getString("date"));
                balanceView.setText(values.getString("balance"));
                view.setOnClickListener(view -> callback.removeTransaction(id));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
