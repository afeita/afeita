package com.github.afeita.tools.fastjson.parser.deserializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

import com.github.afeita.tools.fastjson.JSONException;
import com.github.afeita.tools.fastjson.parser.DefaultExtJSONParser;
import com.github.afeita.tools.fastjson.parser.Feature;
import com.github.afeita.tools.fastjson.parser.JSONLexer;
import com.github.afeita.tools.fastjson.parser.JSONToken;

public class ArrayListStringDeserializer implements ObjectDeserializer {

    public final static ArrayListStringDeserializer instance = new ArrayListStringDeserializer();

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T> T deserialze(DefaultExtJSONParser parser, Type type) {
        ArrayList list = new ArrayList();

        parseArray(parser, list);

        return (T) list;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void parseArray(DefaultExtJSONParser parser, Collection array) {
        JSONLexer lexer = parser.getLexer();

        if (lexer.token() != JSONToken.LBRACKET) {
        	
        	//解析List的时候如果没有解到"[",则判断是否数据为null,为空的话就忽略该数据,不要抛异常报错
        	//2014-07-06 wuhq start
        	if (lexer.token() == JSONToken.NULL){
        		return;
        	}
        	//2014-07-06 wuhq end
        	
            throw new JSONException("exepct '[', but " + lexer.token());
        }

        lexer.nextToken(JSONToken.LITERAL_STRING);

        for (;;) {
            if (lexer.isEnabled(Feature.AllowArbitraryCommas)) {
                while (lexer.token() == JSONToken.COMMA) {
                    lexer.nextToken();
                    continue;
                }
            }

            if (lexer.token() == JSONToken.RBRACKET) {
                break;
            }

            String value;
            if (lexer.token() == JSONToken.LITERAL_STRING) {
                value = lexer.stringVal();
                lexer.nextToken(JSONToken.COMMA);
            } else {
                Object obj = parser.parse();
                if (obj == null) {
                    value = null;
                } else {
                    value = obj.toString();
                }
            }

            array.add(value);

            if (lexer.token() == JSONToken.COMMA) {
                lexer.nextToken(JSONToken.LITERAL_STRING);
                continue;
            }
        }

        lexer.nextToken(JSONToken.COMMA);
    }

    public int getFastMatchToken() {
        return JSONToken.LBRACKET;
    }
}
