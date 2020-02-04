package dev.michaeljung.androidclojurereframetemplate;

import org.json.JSONObject;

interface EventListener {
    void onEvent(JSONObject payload);
}
