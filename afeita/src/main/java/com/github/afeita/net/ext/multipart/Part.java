package com.github.afeita.net.ext.multipart;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 每个部分，只关心，两个：
 * 数据长度及数据内容写入
 * <br /> author: chenshufei
 * <br /> date: 15/9/6
 * <br /> email: chenshufei2@sina.com
 */
public interface Part {
    public long getContentLength(Boundary boundary);
    public void writeTo(final OutputStream out, Boundary boundary) throws IOException;
}
