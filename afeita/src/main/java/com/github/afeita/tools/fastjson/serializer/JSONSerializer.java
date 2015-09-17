/*
 * Copyright 1999-2101 Alibaba Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.afeita.tools.fastjson.serializer;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.github.afeita.tools.fastjson.JSONAware;
import com.github.afeita.tools.fastjson.JSONException;
import com.github.afeita.tools.fastjson.JSONStreamAware;
import com.github.afeita.tools.fastjson.util.ServiceLoader;

/**
 * @author wenshao<szujobs@hotmail.com>
 */
public class JSONSerializer {

    private final SerializeConfig config;

    private final SerializeWriter   out;

    private List<PropertyFilter>    propertyFilters = null;
    private List<ValueFilter>       valueFilters    = null;
    private List<NameFilter>        nameFilters     = null;

    private int                     indentCount     = 0;
    private String                  indent          = "\t";

    public List<ValueFilter> getValueFilters() {
        if (valueFilters == null) {
            valueFilters = new ArrayList<ValueFilter>();
        }

        return valueFilters;
    }

    public List<ValueFilter> getValueFiltersDirect() {
        return valueFilters;
    }

    public int getIndentCount() {
        return indentCount;
    }

    public void incrementIndent() {
        indentCount++;
    }

    public void decrementIdent() {
        indentCount--;
    }

    public void println() {
        out.write('\n');
        for (int i = 0; i < indentCount; ++i) {
            out.write(indent);
        }
    }

    public List<NameFilter> getNameFilters() {
        if (nameFilters == null) {
            nameFilters = new ArrayList<NameFilter>();
        }

        return nameFilters;
    }

    public List<NameFilter> getNameFiltersDirect() {
        return nameFilters;
    }

    public List<PropertyFilter> getPropertyFilters() {
        if (propertyFilters == null) {
            propertyFilters = new ArrayList<PropertyFilter>();
        }

        return propertyFilters;
    }

    public List<PropertyFilter> getPropertyFiltersDirect() {
        return propertyFilters;
    }

    public JSONSerializer(){
        this(new SerializeWriter(), SerializeConfig.getGlobalInstance());
    }

    public JSONSerializer(SerializeWriter out){
        this(out, SerializeConfig.getGlobalInstance());
    }

    public JSONSerializer(SerializeConfig mapping){
        this(new SerializeWriter(), mapping);
    }

    public JSONSerializer(SerializeWriter out, SerializeConfig config){
        this.out = out;
        this.config = config;
    }

    public SerializeWriter getWriter() {
        return out;
    }

    @Override
	public String toString() {
        return out.toString();
    }

    public void config(SerializerFeature feature, boolean state) {
        out.config(feature, state);
    }

    public boolean isEnabled(SerializerFeature feature) {
        return out.isEnabled(feature);
    }

    public void writeNull() {
        this.out.writeNull();
    }

    public SerializeConfig getMapping() {
        return config;
    }

    public static final void write(Writer out, Object object) {
        SerializeWriter writer = new SerializeWriter();
        try {
            JSONSerializer serializer = new JSONSerializer(writer);
            serializer.write(object);
            writer.writeTo(out);
        } catch (IOException ex) {
            throw new JSONException(ex.getMessage(), ex);
        } finally {
            writer.close();
        }
    }

    public static final void write(SerializeWriter out, Object object) {
        JSONSerializer serializer = new JSONSerializer(out);
        serializer.write(object);
    }

    public final void write(Object object) {
        try {
            if (object == null) {
                out.writeNull();
                return;
            }

            Class<?> clazz = object.getClass();

            ObjectSerializer writer = getObjectWriter(clazz);

            writer.write(this, object);
        } catch (IOException e) {
            throw new JSONException(e.getMessage(), e);
        }
    }

    public final void writeWithFormat(Object object, String format) {
        if (object instanceof Date) {
            String text = new SimpleDateFormat(format).format((Date) object);
            out.writeString(text);
            return;
        }
        write(object);
    }

    public final void write(String text) {
        StringSerializer.instance.write(this, text);
    }

    public ObjectSerializer getObjectWriter(Class<?> clazz) {
        ObjectSerializer writer = config.get(clazz);

        if (writer == null) {
            final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            for (AutowiredObjectSerializer autowired : ServiceLoader.load(AutowiredObjectSerializer.class, classLoader)) {
                for (Type forType : autowired.getAutowiredFor()) {
                    config.put(forType, autowired);
                }
            }
            
            writer = config.get(clazz);
        }

        if (writer == null) {
            if (Map.class.isAssignableFrom(clazz)) {
                config.put(clazz, MapSerializer.instance);
            } else if (List.class.isAssignableFrom(clazz)) {
                config.put(clazz, ListSerializer.instance);
            } else if (Collection.class.isAssignableFrom(clazz)) {
                config.put(clazz, CollectionSerializer.instance);
            } else if (Date.class.isAssignableFrom(clazz)) {
                config.put(clazz, DateSerializer.instance);
            } else if (JSONAware.class.isAssignableFrom(clazz)) {
                config.put(clazz, JSONAwareSerializer.instance);
            } else if (JSONStreamAware.class.isAssignableFrom(clazz)) {
                config.put(clazz, JSONStreamAwareSerializer.instance);
            } else if (clazz.isEnum()) {
                config.put(clazz, EnumSerializer.instance);
            } else if (clazz.isArray()) {
                Class<?> componentType = clazz.getComponentType();
                ObjectSerializer compObjectSerializer = getObjectWriter(componentType);
                config.put(clazz, new ArraySerializer(compObjectSerializer));
            } else if (Throwable.class.isAssignableFrom(clazz)) {
                config.put(clazz, new ExceptionSerializer(clazz));
            } else if (TimeZone.class.isAssignableFrom(clazz)) {
                config.put(clazz, TimeZoneSerializer.instance);
            } else if (Appendable.class.isAssignableFrom(clazz)) {
                config.put(clazz, AppendableSerializer.instance);
            } else if (Charset.class.isAssignableFrom(clazz)) {
                config.put(clazz, CharsetSerializer.instance);
            } else {
                config.put(clazz, config.createJavaBeanSerializer(clazz));
            }

            writer = config.get(clazz);
        }
        return writer;
    }

}
