package com.cylorun.request.body;

import com.cylorun.request.enums.ContentType;

public interface RequestBody {
    ContentType getContentType();

    String serialize();

}
