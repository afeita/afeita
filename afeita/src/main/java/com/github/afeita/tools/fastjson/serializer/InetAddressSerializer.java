package com.github.afeita.tools.fastjson.serializer;

import java.io.IOException;
import java.net.InetAddress;

public class InetAddressSerializer implements ObjectSerializer {

    public static InetAddressSerializer instance = new InetAddressSerializer();

    public void write(JSONSerializer serializer, Object object) throws IOException {
        if (object == null) {
            serializer.writeNull();
            return;
        }

        InetAddress address = (InetAddress) object;
        
        serializer.write(address.getHostAddress());
    }
}

