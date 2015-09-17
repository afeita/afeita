package com.github.afeita.tools.fastjson.parser.deserializer;

import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.github.afeita.tools.fastjson.JSONException;
import com.github.afeita.tools.fastjson.parser.DefaultExtJSONParser;
import com.github.afeita.tools.fastjson.parser.JSONToken;

public class InetAddressDeserializer implements ObjectDeserializer {

    public final static InetAddressDeserializer instance = new InetAddressDeserializer();

    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultExtJSONParser parser, Type clazz) {

        String host = (String) parser.parse();

        if (host == null) {
            return null;
        }
        
        if (host.length() == 0) {
            return null;
        }

        try {
            return (T) InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            throw new JSONException("deserialize error", e);
        }
    }
    
    public int getFastMatchToken() {
        return JSONToken.LITERAL_STRING;
    }

}
