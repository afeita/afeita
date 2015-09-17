package com.github.afeita.tools.fastjson.parser.deserializer;

import java.lang.reflect.Array;
import java.lang.reflect.Type;

import com.github.afeita.tools.fastjson.JSONArray;
import com.github.afeita.tools.fastjson.parser.DefaultExtJSONParser;
import com.github.afeita.tools.fastjson.parser.JSONToken;
import com.github.afeita.tools.fastjson.util.TypeUtils;

public class ArrayDeserializer implements ObjectDeserializer {

    public final static ArrayDeserializer instance = new ArrayDeserializer();

    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultExtJSONParser parser, Type clazz) {
        if (parser.getLexer().token() == JSONToken.NULL) {
            parser.getLexer().nextToken(JSONToken.COMMA);
            return null;
        }
        
        JSONArray array = new JSONArray();
        parser.parseArray(array);

        return toObjectArray(parser, (Class<T>) clazz, array);
    }

    @SuppressWarnings("unchecked")
    private <T> T toObjectArray(DefaultExtJSONParser parser, Class<T> clazz, JSONArray array) {
        int size = array.size();

        Class<?> componentType = clazz.getComponentType();
        Object objArray = Array.newInstance(componentType, size);
        for (int i = 0; i < size; ++i) {
            Object value = array.get(i);

            if (componentType.isArray()) {
                Object element = toObjectArray(parser, componentType, (JSONArray) value);
                Array.set(objArray, i, element);
            } else {
                Object element = TypeUtils.cast(value, componentType, parser.getConfig());
                Array.set(objArray, i, element);
            }
        }
        return (T) objArray; // TODO
    }

    public int getFastMatchToken() {
        return JSONToken.LBRACKET;
    }
}
