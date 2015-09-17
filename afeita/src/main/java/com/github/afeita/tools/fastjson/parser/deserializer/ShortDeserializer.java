package com.github.afeita.tools.fastjson.parser.deserializer;

import java.lang.reflect.Type;

import com.github.afeita.tools.fastjson.parser.DefaultExtJSONParser;
import com.github.afeita.tools.fastjson.parser.JSONToken;
import com.github.afeita.tools.fastjson.util.TypeUtils;

public class ShortDeserializer implements ObjectDeserializer {
    public final static ShortDeserializer instance = new ShortDeserializer();

    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultExtJSONParser parser, Type clazz) {
        Object value = parser.parse();

        if (value == null) {
            return null;
        }
        
        return (T) TypeUtils.castToShort(value);
    }

    public int getFastMatchToken() {
        return JSONToken.LITERAL_INT;
    }
}
