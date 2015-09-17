package com.github.afeita.tools.fastjson.parser.deserializer;

import java.lang.reflect.Type;
import java.util.regex.Pattern;

import com.github.afeita.tools.fastjson.parser.DefaultExtJSONParser;
import com.github.afeita.tools.fastjson.parser.JSONToken;

public class PatternDeserializer implements ObjectDeserializer {
    public final static PatternDeserializer instance = new PatternDeserializer();

    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultExtJSONParser parser, Type clazz) {
        Object value = parser.parse();

        if (value == null) {
            return null;
        }
        
        String pattern = (String) value;
        
        return (T) Pattern.compile(pattern);
    }

    public int getFastMatchToken() {
        return JSONToken.LITERAL_STRING;
    }
}
