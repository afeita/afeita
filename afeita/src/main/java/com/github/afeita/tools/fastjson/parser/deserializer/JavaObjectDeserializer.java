package com.github.afeita.tools.fastjson.parser.deserializer;

import java.lang.reflect.Type;

import com.github.afeita.tools.fastjson.parser.DefaultExtJSONParser;
import com.github.afeita.tools.fastjson.parser.JSONToken;

public class JavaObjectDeserializer implements ObjectDeserializer {

    public final static JavaObjectDeserializer instance = new JavaObjectDeserializer();

    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultExtJSONParser parser, Type clazz) {
        return (T) parser.parse();
    }

    public int getFastMatchToken() {
        return JSONToken.LBRACE;
    }
}
