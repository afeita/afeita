package com.github.afeita.tools.fastjson.serializer;

public interface NameFilter {

    String process(Object source, String name, Object value);
}
