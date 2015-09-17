package com.github.afeita.tools.fastjson.parser.deserializer;

import java.util.ArrayList;

import com.github.afeita.tools.fastjson.parser.DefaultExtJSONParser;
import com.github.afeita.tools.fastjson.parser.JSONToken;
import com.github.afeita.tools.fastjson.parser.ParserConfig;
import com.github.afeita.tools.fastjson.util.FieldInfo;

public class ArrayListStringFieldDeserializer extends FieldDeserializer {

    public ArrayListStringFieldDeserializer(ParserConfig mapping, Class<?> clazz, FieldInfo fieldInfo){
        super(clazz, fieldInfo);

    }

    @Override
	public int getFastMatchToken() {
        return JSONToken.LBRACKET;
    }

    @Override
    public void parseField(DefaultExtJSONParser parser, Object object) {
        ArrayList<Object> list = new ArrayList<Object>();

        ArrayListStringDeserializer.parseArray(parser, list);

        setValue(object, list);
    }
}
