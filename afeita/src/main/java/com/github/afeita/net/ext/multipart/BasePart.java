package com.github.afeita.net.ext.multipart;

import org.apache.http.util.EncodingUtils;

/**
 * 获取Header部分数据
 * <br /> author: chenshufei
 * <br /> date: 15/9/6
 * <br /> email: chenshufei2@sina.com
 */
public abstract class BasePart implements Part{
    protected static final byte[] CRLF = EncodingUtilsHelper.getAsciiBytes(MultipartEntity.CRLF);

    public static final String DEFAULT_PARAMVALUE_CHARSET = "utf-8";
    protected static final String DEFAULT_PROTOCOL_CHARSET = "US-ASCII";
    //默认的content-type,为二进制流
    protected static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    protected interface IHeadersProvider {
        public String getContentDisposition();
        public String getContentType();
        public String getContentTransferEncoding();
    }

    //BasePart 子类，必须初始化赋值IHeadersProvider，由各有子类告诉其contenttype等是什么值
    protected IHeadersProvider headersProvider;

    private byte[] header;

    protected byte[] getHeader(Boundary boundary) {
        if (header == null) {
            header = generateHeader(boundary);
        }
        return header;
    }

    private byte[] generateHeader(Boundary boundary) {
        if (headersProvider == null) {
            throw new RuntimeException("Uninitialized headersProvider");
        }
        final ByteArrayBuffer buf = new ByteArrayBuffer(256);
        append(buf, boundary.getStartingBoundary());
        append(buf, headersProvider.getContentDisposition());
        append(buf, CRLF);
        append(buf, headersProvider.getContentType());
        append(buf, CRLF);
        append(buf, headersProvider.getContentTransferEncoding());
        append(buf, CRLF);
        append(buf, CRLF);
        return buf.toByteArray();
    }

    private static void append(ByteArrayBuffer buf, String data) {
        append(buf, EncodingUtils.getAsciiBytes(data));
    }

    private static void append(ByteArrayBuffer buf, byte[] data) {
        buf.append(data, 0, data.length);
    }
}
