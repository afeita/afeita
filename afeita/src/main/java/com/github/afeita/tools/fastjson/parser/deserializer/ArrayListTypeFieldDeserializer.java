package com.github.afeita.tools.fastjson.parser.deserializer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

import com.github.afeita.tools.fastjson.JSONException;
import com.github.afeita.tools.fastjson.parser.DefaultExtJSONParser;
import com.github.afeita.tools.fastjson.parser.Feature;
import com.github.afeita.tools.fastjson.parser.JSONLexer;
import com.github.afeita.tools.fastjson.parser.JSONToken;
import com.github.afeita.tools.fastjson.parser.ParserConfig;
import com.github.afeita.tools.fastjson.util.FieldInfo;

public class ArrayListTypeFieldDeserializer extends FieldDeserializer {

    private final Type         itemType;
    private int                itemFastMatchToken;
    private ObjectDeserializer deserializer;

    public ArrayListTypeFieldDeserializer(ParserConfig mapping, Class<?> clazz, FieldInfo fieldInfo){
        super(clazz, fieldInfo);

        this.itemType = ((ParameterizedType) getFieldType()).getActualTypeArguments()[0];

    }

    @Override
	public int getFastMatchToken() {
        return JSONToken.LBRACKET;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void parseField(DefaultExtJSONParser parser, Object object) {
        if (parser.getLexer().token() == JSONToken.NULL) {
            setValue(object, null);
            return;
        }

        ArrayList list = new ArrayList();

        parseArray(parser, list);

        setValue(object, list);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public final void parseArray(DefaultExtJSONParser parser, Collection array) {
        final JSONLexer lexer = parser.getLexer();

        if (lexer.token() != JSONToken.LBRACKET) {
            throw new JSONException("exepct '[', but " + JSONToken.name(lexer.token()));
        }

        if (deserializer == null) {
            deserializer = parser.getConfig().getDeserializer(itemType);
            itemFastMatchToken = deserializer.getFastMatchToken();
        }

        lexer.nextToken(itemFastMatchToken);

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

            Object val = deserializer.deserialze(parser, itemType);
            array.add(val);

            if (lexer.token() == JSONToken.COMMA) {
                lexer.nextToken(itemFastMatchToken);
                continue;
            }
        }

        lexer.nextToken(JSONToken.COMMA);
    }
}
