package com.github.afeita.net.ext.multipart;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 上传部分核心类，将数据内容写到out中时，采用流式不缓存
 * <br /> author: chenshufei
 * <br /> date: 15/9/6
 * <br /> email: chenshufei2@sina.com
 */
public class MultipartEntity {

    public static final String CRLF = "\r\n";

    private List<Part> parts = new ArrayList<Part>();

    private Boundary boundary;

    public MultipartEntity(String boundaryStr) {
        boundary = new Boundary(boundaryStr);
    }

    public MultipartEntity() {
        this(null);
    }

    public void addPart(Part part) {
        parts.add(part);
    }

    public String getContentType(){
        return "multipart/form-data; boundary=\"" + boundary.getBoundary() + '"';
    }

    /**
     * 获取整体个multipart/form-data上传请求的报文大小
     * @return
     */
    public long getContentLength() {
        long result = 0;
        for (Part part : parts) {
            result += part.getContentLength(boundary);
        }
        result += boundary.getClosingBoundary().length;
        return result;
    }

    /**
     * 返回 请求中，所有文件类型的参数的，文件内容长度累计的总大小
     * @return
     */
    public long getFileContentLength(){
        long result = 0;
        for (Part part : parts) {
            if (part instanceof  FilePart){
                FilePart filePart = (FilePart)part;
                result += filePart.getFileContentLength();
            }
        }
        return result;
    }

    public int getFilePartNum(){
        int result = 0;
        for (Part part : parts) {
            if (part instanceof  FilePart){
                result += 1;
            }
        }
        return result;
    }

    public void writeTo(final OutputStream out) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("Output stream may not be null");
        }
        for (Part part : parts) {
            part.writeTo(out, boundary);
        }
        out.write(boundary.getClosingBoundary());
        out.flush();
    }


}
