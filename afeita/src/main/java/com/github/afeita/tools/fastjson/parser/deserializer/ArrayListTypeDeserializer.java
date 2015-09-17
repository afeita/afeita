package com.github.afeita.tools.fastjson.parser.deserializer;

import java.lang.reflect.Type;
import java.util.ArrayList;

import com.github.afeita.tools.fastjson.parser.DefaultExtJSONParser;
import com.github.afeita.tools.fastjson.parser.JSONToken;

public class ArrayListTypeDeserializer implements ObjectDeserializer {

    private Type itemType;

    public ArrayListTypeDeserializer(Type type){
        this.itemType = type;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T> T deserialze(DefaultExtJSONParser parser, Type type) {
        ArrayList list = new ArrayList();

        parser.parseArray(itemType, list);

        return (T) list;
    }

    public int getFastMatchToken() {
        return JSONToken.LBRACKET;
    }

}
