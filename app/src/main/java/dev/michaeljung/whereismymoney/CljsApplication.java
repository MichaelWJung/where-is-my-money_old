package dev.michaeljung.whereismymoney;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.liquidplayer.service.MicroService;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class CljsApplication extends android.app.Application {

    private static final String LOG_TAG = CljsApplication.class.getSimpleName();
    private static final String DB_FILE_NAME = "db";
    private static final String EMPTY_APP_DB = "{}";

    private MicroService clojure;
    private boolean clojure_ready;

    private ArrayList<Subscription> unregistered_subscriptions;
    private ArrayList<Runnable> waiting_ready_listeners;

    private class Subscription {
        private final String id;
        private final JSONArray query;
        private final EventListener listener;

        private Subscription(String id, JSONArray query, EventListener listener) {
            this.id = id;
            this.query = query;
            this.listener = listener;
        }
    }

    @Override
    public synchronized void onCreate() {
        super.onCreate();

        unregistered_subscriptions = new ArrayList<>();
        waiting_ready_listeners = new ArrayList<>();

        final MicroService.ServiceStartListener startListener = service -> {
            clojure.addEventListener("ready", (service1, event, payload) -> carryOutReadyActions());
        };

        startClojureMicroService(startListener);
    }

    synchronized void doWhenReady(final Runnable runnable) {
        if (clojure_ready) {
            Log.v(LOG_TAG, "Running runnable right away");
            runnable.run();
        } else {
            Log.v(LOG_TAG, "Putting runnable on waiting ready listeners list");
            waiting_ready_listeners.add(runnable);
        }
    }

    void dispatch(JSONArray event) {
        try {
            Log.d(LOG_TAG, "Dispatching signal: " + event.getString(0));
        } catch (JSONException e) {
            Log.w(LOG_TAG, "Error logging dispatch");
        }
        doWhenReady(() -> clojure.emit("dispatch", event));
    }

    synchronized void subscribe(String id, JSONArray query, EventListener listener) {
        if (clojure_ready) {
            doSubscribe(id, query, listener);
        } else {
            Log.v(LOG_TAG, "Add subscription to list of pending subscriptions");
            unregistered_subscriptions.add(new Subscription(id, query, listener));
        }
    }

    synchronized void unsubscribe(String id) {
        if (clojure_ready) {
            clojure.emit("deregister", id);
        } else {
            unregistered_subscriptions.removeIf(sub -> sub.id.equals(id));
        }
    }

    private void startClojureMicroService(MicroService.ServiceStartListener startListener) {
        URI uri = getCompiledClojureScriptCodeUri();
        clojure = new MicroService(this, uri, startListener);
        Log.d(LOG_TAG, "Starting micro service");
        clojure.start();
    }

    private URI getCompiledClojureScriptCodeUri() {
        try {
            return new URI("android.resource://dev.michaeljung.whereismymoney/raw/app");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized void carryOutReadyActions() {
        CljsApplication.this.clojure_ready = true;
        runWaitingReadyListeners();
        registerPendingSubscriptions();
    }

    private void registerPendingSubscriptions() {
        Log.v(LOG_TAG, "Registering pending subscriptions");
        for (Subscription s : unregistered_subscriptions) {
            doSubscribe(s.id, s.query, s.listener);
        }
        unregistered_subscriptions.clear();
    }

    private void runWaitingReadyListeners() {
        Log.v(LOG_TAG, "Running waiting runnables on main thread");
        for (Runnable r : waiting_ready_listeners) {
            new Handler(Looper.getMainLooper()).post(r);
        }
        waiting_ready_listeners.clear();
    }

    private void doSubscribe(String id, JSONArray query, final EventListener listener) {
        Log.v(LOG_TAG, "Do subscription for: " + id + ". Query: " + query);

        clojure.addEventListener(id, toLiquidCoreUiThreadListener(listener));

        JSONObject payload = new JSONObject();
        try {
            payload.put("id", id);
            payload.put("query", query);
            clojure.emit("register", payload);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private MicroService.EventListener toLiquidCoreUiThreadListener(final EventListener listener) {
        return (service, event, payload) -> new Handler(Looper.getMainLooper()).post(
                () -> listener.onEvent(payload));
    }
}
