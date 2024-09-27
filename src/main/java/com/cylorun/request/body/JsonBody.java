package com.cylorun.request.body;

import com.cylorun.request.enums.ContentType;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class JsonBody implements RequestBody {
    private JsonElement jsonData;
    private final String stringData;

    public JsonBody(String stringData) {
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
            throw new JsonParseException("Not JSON");
        }
    }

    @Override
    public ContentType getContentType() {
        return ContentType.APPLICATION_JSON;
    }

    @Override
    public String serialize() {
        return this.jsonData.toString();
    }
}
