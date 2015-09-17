package com.github.afeita.tools.fastjson.parser.deserializer;

import java.lang.reflect.Type;
import java.net.URI;

import com.github.afeita.tools.fastjson.parser.DefaultExtJSONParser;
import com.github.afeita.tools.fastjson.parser.JSONToken;

public class URIDeserializer implements ObjectDeserializer {
    public final static URIDeserializer instance = new URIDeserializer();

    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultExtJSONParser parser, Type clazz) {
        
        String uri = (String) parser.parse();
        
        if (uri == null) {
            return null;
        }
        
        return (T) URI.create(uri);
    }

    public int getFastMatchToken() {
        return JSONToken.LITERAL_STRING;
    }
}
