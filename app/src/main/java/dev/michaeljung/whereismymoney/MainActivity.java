package dev.michaeljung.whereismymoney;

import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;

public class MainActivity extends CljsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        subscribe("current-screen", payload -> {
            try {
                String screen = payload.getString("value");
                switch (screen) {
                    case "account":
                        showAccount();
                        break;
                    case "transaction":
                        showTransaction();
                        break;
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void showAccount() {
        Log.d("xyz", "Show account");
        AccountOverviewFragment fragment = new AccountOverviewFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void showTransaction() {
        Log.d("xyz", "Show transaction");
        AddTransactionFragment fragment = new AddTransactionFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
