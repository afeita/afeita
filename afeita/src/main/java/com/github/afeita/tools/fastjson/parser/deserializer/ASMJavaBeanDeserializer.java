package com.github.afeita.tools.fastjson.parser.deserializer;

import java.lang.reflect.Type;

import com.github.afeita.tools.fastjson.JSONException;
import com.github.afeita.tools.fastjson.parser.DefaultExtJSONParser;
import com.github.afeita.tools.fastjson.parser.Feature;
import com.github.afeita.tools.fastjson.parser.JSONScanner;
import com.github.afeita.tools.fastjson.parser.ParserConfig;
import com.github.afeita.tools.fastjson.util.FieldInfo;

public abstract class ASMJavaBeanDeserializer implements ObjectDeserializer {

    protected InnerJavaBeanDeserializer serializer;

    public ASMJavaBeanDeserializer(ParserConfig mapping, Class<?> clazz){
        serializer = new InnerJavaBeanDeserializer(mapping, clazz);

        serializer.getFieldDeserializerMap();
    }

    public abstract Object createInstance(DefaultExtJSONParser parser, Type type);

    public InnerJavaBeanDeserializer getInnterSerializer() {
        return serializer;
    }

    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultExtJSONParser parser, Type type) {
        return (T) serializer.deserialze(parser, type);
    }

    public int getFastMatchToken() {
        return serializer.getFastMatchToken();
    }

    public FieldDeserializer createFieldDeserializer(ParserConfig mapping, Class<?> clazz, FieldInfo fieldInfo) {
        return mapping.createFieldDeserializer(mapping, clazz, fieldInfo);
    }

    public boolean parseField(DefaultExtJSONParser parser, String key, Object object) {
        JSONScanner lexer = (JSONScanner) parser.getLexer(); // xxx

        FieldDeserializer fieldDeserializer = serializer.getFieldDeserializerMap().get(key);

        if (fieldDeserializer == null) {
            if (!parser.isEnabled(Feature.IgnoreNotMatch)) {
                throw new JSONException("setter not found, class " + serializer.getClass() + ", property " + key);
            }

            lexer.nextTokenWithColon();
            parser.parse(); // skip

            return false;
        }

        lexer.nextTokenWithColon(fieldDeserializer.getFastMatchToken());
        fieldDeserializer.parseField(parser, object);
        return true;
    }

    public final class InnerJavaBeanDeserializer extends JavaBeanDeserializer {

        private InnerJavaBeanDeserializer(ParserConfig mapping, Class<?> clazz){
            super(mapping, clazz);
        }

        @Override
		public boolean parseField(DefaultExtJSONParser parser, String key, Object object) {
            return ASMJavaBeanDeserializer.this.parseField(parser, key, object);
        }

        @Override
		public FieldDeserializer createFieldDeserializer(ParserConfig mapping, Class<?> clazz, FieldInfo fieldInfo) {
            return ASMJavaBeanDeserializer.this.createFieldDeserializer(mapping, clazz, fieldInfo);
        }
    }

}
