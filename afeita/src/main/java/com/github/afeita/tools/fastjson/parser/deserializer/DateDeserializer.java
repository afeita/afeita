package com.github.afeita.tools.fastjson.parser.deserializer;

import java.lang.reflect.Type;

import com.github.afeita.tools.fastjson.JSONException;
import com.github.afeita.tools.fastjson.parser.DefaultExtJSONParser;
import com.github.afeita.tools.fastjson.parser.JSONScanner;
import com.github.afeita.tools.fastjson.parser.JSONToken;

public class DateDeserializer implements ObjectDeserializer {

    public final static DateDeserializer instance = new DateDeserializer();

    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultExtJSONParser parser, Type clazz) {
        Object val = parser.parse();

        if (val == null) {
            return null;
        }

        if (val instanceof java.util.Date) {
            return (T) val;
        } else if (val instanceof Number) {
            return (T) new java.util.Date(((Number) val).longValue());
        } else if (val instanceof String) {
            String strVal = (String) val;
            if (strVal.length() == 0) {
                return null;
            }
            
            JSONScanner dateLexer = new JSONScanner(strVal);
            if (dateLexer.scanISO8601DateIfMatch()) {
                return (T) dateLexer.getCalendar().getTime();
            }

            long longVal = Long.parseLong(strVal);
            return (T) new java.util.Date(longVal);
        }
        
        throw new JSONException("parse error");
    }

    public int getFastMatchToken() {
        return JSONToken.LITERAL_INT;
    }
}
