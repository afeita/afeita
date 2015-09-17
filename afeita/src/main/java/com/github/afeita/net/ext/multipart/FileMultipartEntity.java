package com.github.afeita.net.ext.multipart;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 增加 上传进度监听
 * <br /> author: chenshufei
 * <br /> date: 15/9/6
 * <br /> email: chenshufei2@sina.com
 */
public class FileMultipartEntity extends MultipartEntity {
    private long mOffset;
    private OnUploadProgressListener mOnUploadProgressListener;
    public static interface OnUploadProgressListener{
        public void onUploadProgress(int fileNum,String currentUploadFilename,long fileSumSize,long transferredSize);
    }

    public void setOnUploadProgressListener(OnUploadProgressListener onUploadProgressListener) {
        this.mOnUploadProgressListener = onUploadProgressListener;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        if (mOnUploadProgressListener == null) {
            super.writeTo(out);
        } else {
            long fileContentLength = getFileContentLength();

            super.writeTo(new CountingOutputStream(out, mOffset, mOnUploadProgressListener,getFilePartNum(),getFileContentLength()));
        }
    }

    static class CountingOutputStream  extends FilterOutputStream {
        private final OnUploadProgressListener listener;
        private long transferred;
        private long offset;
        private int fileNum;
        private long fileSumSize;

        public CountingOutputStream(final OutputStream out, long offset, final OnUploadProgressListener listener,int fileNum,long fileSumSize) {
            super(out);
            this.listener = listener;
            this.transferred = 0;
            this.offset = offset;
            this.fileNum = fileNum;
            this.fileSumSize = fileSumSize;
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            out.write(b, off, len);
            /*this.transferred += len;
            this.listener.onUploadProgress(this.transferred + offset);*/
        }
        //只统计 post请求中，文件参数的，已经上传的流大小
        public void write(String filename,byte[] b, int off, int len) throws IOException {
            out.write(b, off, len);
            this.transferred += len;
            this.listener.onUploadProgress(fileNum,filename,fileSumSize,this.transferred + offset);
        }

        @Override
        public void write(int b) throws IOException {
            out.write(b);
            /*this.transferred++;
            this.listener.onUploadProgress(this.transferred + offset);*/
        }

        public void write(String filename,int b) throws IOException {
            out.write(b);
            this.transferred++;
            this.listener.onUploadProgress(fileNum,filename,fileSumSize,this.transferred + offset);
        }

        @Override
        public void write(byte[] buffer) throws IOException {
            out.write(buffer);
            /*this.transferred += buffer.length;
            this.listener.onUploadProgress(this.transferred + offset);*/
        }

        public void write(String filename,byte[] buffer) throws IOException {
            out.write(buffer);
            this.transferred += buffer.length;
            this.listener.onUploadProgress(fileNum,filename,fileSumSize,this.transferred + offset);
        }

    }
}
