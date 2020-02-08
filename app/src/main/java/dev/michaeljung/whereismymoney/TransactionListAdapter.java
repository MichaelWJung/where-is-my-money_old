package dev.michaeljung.whereismymoney;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TransactionListAdapter extends RecyclerView.Adapter<TransactionListAdapter.TransactionViewHolder> {
    private static final String LOG_TAG = TransactionListAdapter.class.getSimpleName();

    private JSONArray transactionList;
    private LayoutInflater inflater;

    TransactionListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        transactionList = new JSONArray();
    }

    public void setTransactions(JSONArray transactions) {
        transactionList = transactions;
        notifyDataSetChanged();
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

    @Override
    public int getItemCount() {
        return transactionList.length();
    }

    class TransactionViewHolder extends RecyclerView.ViewHolder {
        private final TextView descriptionView;
        private final TextView amountView;
        private final TextView accountView;
        private final TextView dateView;
        private final TextView balanceView;

        TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            descriptionView = itemView.findViewById(R.id.description);
            amountView = itemView.findViewById(R.id.amount);
            accountView = itemView.findViewById(R.id.account);
            dateView = itemView.findViewById(R.id.date);
            balanceView = itemView.findViewById(R.id.balance);
        }

        void setValues(JSONObject values) {
            try {
                descriptionView.setText(values.getString("description"));
                amountView.setText(values.getString("amount"));
                accountView.setText(values.getString("account"));
                dateView.setText(values.getString("date"));
                balanceView.setText(values.getString("balance"));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
