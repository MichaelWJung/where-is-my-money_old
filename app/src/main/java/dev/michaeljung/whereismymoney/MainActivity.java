package dev.michaeljung.whereismymoney;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import org.json.JSONException;

public class MainActivity extends CljsActivity implements AddTransactionFragment.Callbacks,
        AccountOverviewFragment.Callbacks {

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();

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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            BackButtonListener backButtonListener = (BackButtonListener) currentFragment;
            if (backButtonListener != null) {
                backButtonListener.onBackButtonClicked();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setAccountFragmentTitle(String title) {
        actionBar.setTitle(title);
        actionBar.setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void setTransactionFragmentTitle(String title) {
        actionBar.setTitle(title);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
