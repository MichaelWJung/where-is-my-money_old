package dev.michaeljung.whereismymoney;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.UUID;

public class CljsActivity extends AppCompatActivity {
    private ArrayList<String> listenerIds;
    private CljsApplication application;

    CljsActivity() {
        listenerIds = new ArrayList<>();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (CljsApplication) getApplication();
    }

    @Override
    protected void onDestroy() {
        for (String id : listenerIds) {
            application.unsubscribe(id);
        }
        super.onDestroy();
    }

    protected void doWhenReady(Runnable runnable) {
        application.doWhenReady(runnable);
    }

    protected String subscribe(String query, EventListener listener) {
        return subscribe(toJsonArray(query), listener);
    }

    protected String subscribe(JSONArray query, EventListener listener) {
        String listenerId = UUID.randomUUID().toString();
        application.subscribe(listenerId, query, listener);
        listenerIds.add(listenerId);
        return listenerId;
    }

    protected void dispatch(String event) {
        dispatch(toJsonArray(event));
    }

    protected void dispatch(JSONArray event) {
        application.dispatch(event);
    }

    private JSONArray toJsonArray(String query) {
        JSONArray array = new JSONArray();
        array.put(query);
        return array;
    }
}
