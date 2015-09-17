package com.github.afeita.tools.fastjson.parser.deserializer;

import com.github.afeita.tools.fastjson.parser.DefaultExtJSONParser;
import com.github.afeita.tools.fastjson.parser.JSONLexer;
import com.github.afeita.tools.fastjson.parser.JSONToken;
import com.github.afeita.tools.fastjson.parser.ParserConfig;
import com.github.afeita.tools.fastjson.util.FieldInfo;
import com.github.afeita.tools.fastjson.util.TypeUtils;

public class IntegerFieldDeserializer extends FieldDeserializer {

    public IntegerFieldDeserializer(ParserConfig mapping, Class<?> clazz, FieldInfo fieldInfo){
        super(clazz, fieldInfo);
    }

    @Override
    public void parseField(DefaultExtJSONParser parser, Object object) {
        Integer value;

        final JSONLexer lexer = parser.getLexer();
        if (lexer.token() == JSONToken.LITERAL_INT) {
            int val = lexer.intValue();
            lexer.nextToken(JSONToken.COMMA);
            setValue(object, val);
            return;
        } else if (lexer.token() == JSONToken.NULL) {
            value = null;
            lexer.nextToken(JSONToken.COMMA);
        } else {
            Object obj = parser.parse();

            value = TypeUtils.castToInt(obj);
        }

        if (value == null && getFieldClass() == int.class) {
            // skip
            return;
        }

        setValue(object, value);
    }

    @Override
	public int getFastMatchToken() {
        return JSONToken.LITERAL_INT;
    }
}
