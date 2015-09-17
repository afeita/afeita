package com.github.afeita.tools.fastjson.parser.deserializer;

import com.github.afeita.tools.fastjson.parser.DefaultExtJSONParser;
import com.github.afeita.tools.fastjson.parser.JSONLexer;
import com.github.afeita.tools.fastjson.parser.JSONToken;
import com.github.afeita.tools.fastjson.parser.ParserConfig;
import com.github.afeita.tools.fastjson.util.FieldInfo;
import com.github.afeita.tools.fastjson.util.TypeUtils;

public class BooleanFieldDeserializer extends FieldDeserializer {

    public BooleanFieldDeserializer(ParserConfig mapping, Class<?> clazz, FieldInfo fieldInfo){
        super(clazz, fieldInfo);
    }

    @Override
    public void parseField(DefaultExtJSONParser parser, Object object) {
        Boolean value;

        final JSONLexer lexer = parser.getLexer();
        if (lexer.token() == JSONToken.TRUE) {
            lexer.nextToken(JSONToken.COMMA);
            setValue(object, true);
            return;
        }

        if (lexer.token() == JSONToken.LITERAL_INT) {
            int val = lexer.intValue();
            lexer.nextToken(JSONToken.COMMA);
            if (val == 1) {
                setValue(object, true);
            } else {
                setValue(object, false);
            }
            return;
        }

        if (lexer.token() == JSONToken.NULL) {
            value = null;
            lexer.nextToken(JSONToken.COMMA);

            if (getFieldClass() == boolean.class) {
                // skip
                return;
            }

            setValue(object, null);
            return;
        }

        if (lexer.token() == JSONToken.FALSE) {
            lexer.nextToken(JSONToken.COMMA);
            setValue(object, false);
            return;
        }

        Object obj = parser.parse();

        value = TypeUtils.castToBoolean(obj);

        if (value == null && getFieldClass() == boolean.class) {
            // skip
            return;
        }

        setValue(object, value);
    }

    @Override
	public int getFastMatchToken() {
        return JSONToken.TRUE;
    }
}
