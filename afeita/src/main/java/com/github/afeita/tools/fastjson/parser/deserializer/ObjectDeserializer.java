package com.github.afeita.tools.fastjson.parser.deserializer;

import java.lang.reflect.Type;

import com.github.afeita.tools.fastjson.parser.DefaultExtJSONParser;

public interface ObjectDeserializer {
    <T> T deserialze(DefaultExtJSONParser parser, Type type);
    
    int getFastMatchToken();
}
