package com.github.afeita.tools.fastjson.serializer;

import java.io.IOException;

public class AppendableSerializer implements ObjectSerializer {

    public final static AppendableSerializer instance = new AppendableSerializer();

    public void write(JSONSerializer serializer, Object object) throws IOException {
        if (object == null) {
            SerializeWriter out = serializer.getWriter();
            if (out.isEnabled(SerializerFeature.WriteNullStringAsEmpty)) {
                out.writeString("");
            } else {
                out.writeNull();
            }
            return;
        }

        serializer.write(object.toString());
    }

}
