package com.github.afeita.tools.fastjson.parser.deserializer;

import java.lang.reflect.Type;
import java.math.BigDecimal;

import com.github.afeita.tools.fastjson.parser.DefaultExtJSONParser;
import com.github.afeita.tools.fastjson.parser.JSONLexer;
import com.github.afeita.tools.fastjson.parser.JSONToken;
import com.github.afeita.tools.fastjson.util.TypeUtils;

public class NumberDeserializer implements ObjectDeserializer {

    public final static NumberDeserializer instance = new NumberDeserializer();

    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultExtJSONParser parser, Type clazz) {
        return (T) deserialze(parser);
    }

    @SuppressWarnings("unchecked")
    public static <T> T deserialze(DefaultExtJSONParser parser) {
        final JSONLexer lexer = parser.getLexer();
        if (lexer.token() == JSONToken.LITERAL_INT) {
            long val = lexer.longValue();
            lexer.nextToken(JSONToken.COMMA);
            
            if (val >= Integer.MIN_VALUE && val <= Integer.MAX_VALUE) {
                return (T) Integer.valueOf((int) val);    
            }
            return (T) Long.valueOf(val);
        }

        if (lexer.token() == JSONToken.LITERAL_FLOAT) {
            BigDecimal val = lexer.decimalValue();
            lexer.nextToken(JSONToken.COMMA);
            return (T) val;
        }

        Object value = parser.parse();

        if (value == null) {
            return null;
        }

        return (T) TypeUtils.castToBigDecimal(value);
    }

    public int getFastMatchToken() {
        return JSONToken.LITERAL_INT;
    }
}
