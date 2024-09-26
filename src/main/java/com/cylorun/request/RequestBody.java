package com.cylorun.request;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.util.Optional;

public class RequestBody {

    private JsonElement jsonData;
    private String stringData;
    private RequestBody(String stringData) {
        this.stringData = stringData;

        JsonElement bodyElement;
        try {
            bodyElement = JsonParser.parseString(this.stringData);
            if (bodyElement.isJsonArray()) {
                this.jsonData = bodyElement.getAsJsonArray();
            } else if (bodyElement.isJsonObject()) {
                this.jsonData = bodyElement.getAsJsonObject();
            }
        } catch (JsonParseException e) {
            System.out.println("not json data: " + this.stringData);
        }
    }

    public static RequestBody fromString(String body) {
        return new RequestBody(body);
    }

    public Optional<String> getJsonValue(String key) {
        if (this.jsonData != null && this.jsonData.isJsonObject()) {
            return Optional.ofNullable(this.jsonData.getAsJsonObject().get(key).getAsString());
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        if (this.jsonData != null) {
            return this.jsonData.toString();
        }
        return "";
    }
}
