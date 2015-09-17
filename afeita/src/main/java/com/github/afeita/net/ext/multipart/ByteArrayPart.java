package com.github.afeita.net.ext.multipart;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;

/**
 * 适用小形 从内存中形成的小图，不想保存成file，再上传的。
 * 毕竟，此种做法，已经将整个文件数据加载到内存中了，mData指向了整个文件数据区域
 * <br /> author: chenshufei
 * <br /> date: 15/9/6
 * <br /> email: chenshufei2@sina.com
 */
public class ByteArrayPart extends BasePart {

    private byte[] mData;

    public ByteArrayPart(String key,byte[] data,String filename,final String mimeType){
        if (key == null || mimeType.length()<=0) {
            throw new IllegalArgumentException("key may not be null");
        }
        if (data == null|| data.length <=0) {
            throw new IllegalArgumentException("data may not be null or data's length less zero");
        }
        if (filename == null|| filename.length()<=0) {
            throw new IllegalArgumentException("filename may not be null");
        }
        this.mData = data;
        final String partName = EncodingUtilsHelper.encode(key, DEFAULT_PROTOCOL_CHARSET);
        final String partFilename = EncodingUtilsHelper.encode(filename,DEFAULT_PROTOCOL_CHARSET);
        // 使用URLConnection.guessContentTypeFromName(filename) 根据文件扩展名，尽量可猜出其MIME-TYPE类型，若还是拿不到统一认为是application/octet-stream
        String tempPartContentType = (mimeType == null) ? URLConnection.guessContentTypeFromName(partFilename) : mimeType;
        if (null == tempPartContentType || tempPartContentType.length() <=0){
            tempPartContentType = DEFAULT_CONTENT_TYPE;
        }
        final String partContentType = tempPartContentType;
        headersProvider = new IHeadersProvider() {
            public String getContentDisposition() {
                return "Content-Disposition: form-data; name=\"" + partName
                        + "\"; filename=\"" + partFilename + '"';
            }
            public String getContentType() {
                return "Content-Type: " + mimeType;
            }
            public String getContentTransferEncoding() {
                return "Content-Transfer-Encoding: binary";
            }
        };
    }

    @Override
    public long getContentLength(Boundary boundary) {
        return getHeader(boundary).length + mData.length + CRLF.length;
    }

    @Override
    public void writeTo(OutputStream out, Boundary boundary) throws IOException {
        out.write(getHeader(boundary));
        InputStream in = new ByteArrayInputStream(mData);
        try {
            byte[] tmp = new byte[4096];
            int l;
            while ((l = in.read(tmp)) != -1) {
                out.write(tmp, 0, l);
            }
        } finally {
            in.close();
        }
        out.write(CRLF);
    }


}
