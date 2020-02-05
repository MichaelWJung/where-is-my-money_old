package dev.michaeljung.whereismymoney;

import org.json.JSONObject;

interface EventListener {
    void onEvent(JSONObject payload);
}
