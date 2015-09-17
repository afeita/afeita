package com.github.afeita.tools.fastjson.serializer;

public interface ValueFilter {

    Object process(Object source, String name, Object value);
}
