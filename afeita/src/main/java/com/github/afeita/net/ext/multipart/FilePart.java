package com.github.afeita.net.ext.multipart;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;

/**
 * 文件参数  aaa.png = File
 * <br /> author: chenshufei
 * <br /> date: 15/9/6
 * <br /> email: chenshufei2@sina.com
 */
public class FilePart extends BasePart {
    private final File file;

    /**
     * @param name String - name of parameter (may not be <code>null</code>).
     * @param file File (may not be <code>null</code>).
     * @param filename String. If <code>null</code> is passed,
     *        then <code>file.getName()</code> is used.
     * @param contentType String. If <code>null</code> is passed,根据file.getName的文件扩展名尽量猜类型
     *        then default "application/octet-stream" is used.
     *
     * @throws IllegalArgumentException if either <code>file</code>
     *         or <code>name</code> is <code>null</code>.
     */
    public FilePart(String name, File file, String filename, String contentType) {
        if (file == null || file.length()<=0) {
            throw new IllegalArgumentException("File may not be null");
        }
        if (name == null || name.length()<=0) {
            throw new IllegalArgumentException("Name may not be null");
        }

        this.file                    = file;
        final String partName        = EncodingUtilsHelper.encode(name, DEFAULT_PROTOCOL_CHARSET);
        final String partFilename    = EncodingUtilsHelper.encode(
                (filename == null) ? file.getName() : filename,
                DEFAULT_PROTOCOL_CHARSET
        );
        // 使用URLConnection.guessContentTypeFromName(filename) 根据文件扩展名，尽量可猜出其MIME-TYPE类型，若还是拿不到统一认为是application/octet-stream
        String tempPartContentType = (contentType == null) ? URLConnection.guessContentTypeFromName(file.getName()) : contentType;
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
                return "Content-Type: " + partContentType;
            }
            public String getContentTransferEncoding() {
                return "Content-Transfer-Encoding: binary";
            }
        };
    }

    public String getFileName(){
        return file.getName();
    }

    public long getContentLength(Boundary boundary) {
        return getHeader(boundary).length + file.length() + CRLF.length;
    }

    public long getFileContentLength(){
        return file.length();
    }

    public void writeTo(OutputStream out, Boundary boundary) throws IOException {
        out.write(getHeader(boundary));
        InputStream in = new FileInputStream(file);
        try {
            byte[] tmp = new byte[4096];
            int l;
            while ((l = in.read(tmp)) != -1) {
                if (out instanceof FileMultipartEntity.CountingOutputStream){
                    ((FileMultipartEntity.CountingOutputStream) out).write(file.getName(),tmp, 0, l);
                }else{
                    out.write(tmp, 0, l);
                }
            }
        } finally {
            in.close();
        }
        out.write(CRLF);
    }
}
