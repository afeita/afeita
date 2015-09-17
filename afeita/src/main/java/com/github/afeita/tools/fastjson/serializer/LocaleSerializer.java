package com.github.afeita.tools.fastjson.serializer;

import java.io.IOException;
import java.util.Locale;

public class LocaleSerializer implements ObjectSerializer {

    public final static LocaleSerializer instance = new LocaleSerializer();

    public void write(JSONSerializer serializer, Object object) throws IOException {
        if (object == null) {
            serializer.writeNull();
            return;
        }

        Locale locale = (Locale) object;
        serializer.write(locale.toString());
    }

}
