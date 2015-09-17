package com.github.afeita.net.ext.multipart;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * 普通 字符的入参，比如表单中的username=zhangsa 形式的
 * <br /> author: chenshufei
 * <br /> date: 15/9/6
 * <br /> email: chenshufei2@sina.com
 */
public class StringPart extends BasePart {
    private final byte[] valueBytes;

    /**
     * @param name String - name of parameter (may not be <code>null</code>).
     * @param value String - value of parameter (may not be <code>null</code>).
     * @param charset String, if null is passed then default "utf-8" charset is used.
     *
     * @throws IllegalArgumentException if either <code>value</code>
     *         or <code>name</code> is <code>null</code>.
     * @throws RuntimeException if <code>charset</code> is unsupported by OS.
     */
    public StringPart(String name, String value, String charset) {
        if (name == null || name.length()<=0 ) {
            throw new IllegalArgumentException("Name may not be null");
        }
        if (value == null || name.length()<=0 ) {
            throw new IllegalArgumentException("Value may not be null");
        }
        //以US-ASCII 编码 请求参数Key
        final String partName = EncodingUtilsHelper.encode(name, DEFAULT_PROTOCOL_CHARSET);

        if (charset == null) {
            charset = "utf-8";
        }
        final String partCharset = charset;

        try {
            this.valueBytes = value.getBytes(partCharset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        headersProvider = new IHeadersProvider() {
            public String getContentDisposition() {
                return "Content-Disposition: form-data; name=\"" + partName + '"';
            }
            public String getContentType() {
                return "Content-Type: text/plain; charset=" + partCharset;
            }
            public String getContentTransferEncoding() {
                return "Content-Transfer-Encoding: 8bit";
            }
        };
    }

    /**
     * Default "utf-8" charset is used.
     * @param name String - name of parameter (may not be <code>null</code>).
     * @param value String - value of parameter (may not be <code>null</code>).
     *
     * @throws IllegalArgumentException if either <code>value</code>
     *         or <code>name</code> is <code>null</code>.
     */
    public StringPart(String name, String value) {
        this(name, value, null);
    }

    public long getContentLength(Boundary boundary) {
        return getHeader(boundary).length + valueBytes.length + CRLF.length;
    }

    public void writeTo(final OutputStream out, Boundary boundary) throws IOException {
        out.write(getHeader(boundary));
        out.write(valueBytes);
        out.write(CRLF);
    }
}
