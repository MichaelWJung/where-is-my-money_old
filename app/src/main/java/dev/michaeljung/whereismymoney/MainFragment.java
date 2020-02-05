package dev.michaeljung.whereismymoney;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;

public class MainFragment extends CljsFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        subscribe("message", payload -> {
            try {
                String text = payload.getString("value");
                TextView textView = view.findViewById(R.id.textView);
                textView.setText(text);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
