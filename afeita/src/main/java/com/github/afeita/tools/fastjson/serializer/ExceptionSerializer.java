package com.github.afeita.tools.fastjson.serializer;

public class ExceptionSerializer extends JavaBeanSerializer {

    public ExceptionSerializer(Class<?> clazz){
        super(clazz);
    }

    @Override
	protected boolean isWriteClassName(JSONSerializer serializer) {
        return true;
    }
}
