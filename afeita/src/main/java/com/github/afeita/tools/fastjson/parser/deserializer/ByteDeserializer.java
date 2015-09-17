package com.github.afeita.tools.fastjson.parser.deserializer;

import java.lang.reflect.Type;

import com.github.afeita.tools.fastjson.parser.DefaultExtJSONParser;
import com.github.afeita.tools.fastjson.parser.JSONToken;
import com.github.afeita.tools.fastjson.util.TypeUtils;

public class ByteDeserializer implements ObjectDeserializer {
    public final static ByteDeserializer instance = new ByteDeserializer();

    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultExtJSONParser parser, Type clazz) {
        Object value = parser.parse();

        if (value == null) {
            return null;
        }
        
        return (T) TypeUtils.castToByte(value);
    }

    public int getFastMatchToken() {
        return JSONToken.LITERAL_INT;
    }
}
