package com.github.afeita.tools.fastjson.parser.deserializer;

import java.lang.reflect.Type;

import com.github.afeita.tools.fastjson.parser.DefaultExtJSONParser;
import com.github.afeita.tools.fastjson.parser.JSONToken;
import com.github.afeita.tools.fastjson.parser.ParserConfig;
import com.github.afeita.tools.fastjson.util.TypeUtils;

public class ThrowableDeserializer extends JavaBeanDeserializer {

    public ThrowableDeserializer(ParserConfig mapping, Class<?> clazz){
        super(mapping, clazz);
    }

    @Override
	@SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultExtJSONParser parser, Type clazz) {
        Object jsonValue = parser.parse();
        return (T) TypeUtils.cast(jsonValue, clazz, parser.getConfig());
    }

    @Override
	public int getFastMatchToken() {
        return JSONToken.LBRACE;
    }
}
