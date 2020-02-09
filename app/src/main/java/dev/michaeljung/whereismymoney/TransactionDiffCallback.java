package dev.michaeljung.whereismymoney;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TransactionDiffCallback extends DiffUtil.Callback {

    private JSONArray oldTransactions;
    private JSONArray newTransactions;

    TransactionDiffCallback(JSONArray oldTransactions, JSONArray newTransactions) {
        this.oldTransactions = oldTransactions;
        this.newTransactions = newTransactions;
    }

    @Override
    public int getOldListSize() {
        return oldTransactions.length();
    }

    @Override
    public int getNewListSize() {
        return newTransactions.length();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        try {
            JSONObject oldTransaction = oldTransactions.getJSONObject(oldItemPosition);
            JSONObject newTransaction = newTransactions.getJSONObject(newItemPosition);
            return oldTransaction.getInt("id") == newTransaction.getInt("id");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        try {
            JSONObject oldTransaction = oldTransactions.getJSONObject(oldItemPosition);
            JSONObject newTransaction = newTransactions.getJSONObject(newItemPosition);
            return oldTransaction.getString("description").equals(newTransaction.getString("description")) &&
                    oldTransaction.getString("amount").equals(newTransaction.getString("amount")) &&
                    oldTransaction.getString("account").equals(newTransaction.getString("account")) &&
                    oldTransaction.getString("date").equals(newTransaction.getString("date")) &&
                    oldTransaction.getString("balance").equals(newTransaction.getString("balance"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
/*        try {
            JSONObject oldTransaction = oldTransactions.getJSONObject(oldItemPosition);
            JSONObject newTransaction = oldTransactions.getJSONObject(newItemPosition);

            Bundle diff = new Bundle();
            if (!oldTransaction.getString("description").equals(newTransaction.getString("description"))) {

            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }*/
    }
}
