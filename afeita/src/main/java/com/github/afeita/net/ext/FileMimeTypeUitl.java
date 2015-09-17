package com.github.afeita.net.ext;

import android.text.TextUtils;

import com.github.afeita.net.ext.exception.NotSupportDataFormatException;
import com.github.afeita.net.ext.exception.ReadIOException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * <br /> author: chenshufei
 * <br /> date: 15/9/4
 * <br /> email: chenshufei2@sina.com
 */
public class FileMimeTypeUitl {
    public static Map<String, String> fileFormatMap = new HashMap<String, String>();

    //0x4D42  bmp 文件,mmd 度娘好久才找到...
    private static final int GIF_HEADER = 0x474946; //gif
    private static final int PNG_HEADER = 0x89504E47; //png
    private static final int EXIF_MAGIC_NUMBER = 0xFFD8; //jpg
    private static final int BMP_HEADER = 0x4D42; //BMP

    static {
        fileFormatMap.put("art", "image/x-jg");
        fileFormatMap.put("bmp", "image/bmp");
        fileFormatMap.put("gif", "image/gif");
        fileFormatMap.put("jpe", "image/jpeg");
        fileFormatMap.put("jpeg", "image/jpeg");
        fileFormatMap.put("jpg", "image/jpeg");
        fileFormatMap.put("png", "image/png");
    }

    // 修改图片类型
    public static String getImgRealMimeType(String imgFormat) {
        String mineType = fileFormatMap.get(imgFormat);
        if (TextUtils.isEmpty(mineType)) {
            mineType = "image/jpeg"; // 默认为jpg
        }
        return mineType;
    }

    public static String guessImgExtByDataArray(byte[] data) throws NotSupportDataFormatException {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        StreamReader sr = new StreamReader(bis);
        int firstTwoBytes = 0;
        String fileExt = null; //文件名后缀，默认为jpeg形式
        try {
            firstTwoBytes = sr.getUInt16();
            // JPEG.
            if (firstTwoBytes == EXIF_MAGIC_NUMBER) {
                fileExt = "jpeg";
            } else if (firstTwoBytes == BMP_HEADER) {
                fileExt = "bmp";
            }

            final int firstFourBytes = firstTwoBytes << 16 & 0xFFFF0000 | sr.getUInt16() & 0xFFFF;
            // PNG.
            if (firstFourBytes == PNG_HEADER) {
                fileExt = "png";
            }
            // GIF from first 3 bytes.
            if (firstFourBytes >> 8 == GIF_HEADER) {
                fileExt = "gif";
            }
        } catch (IOException e) {
            throw new ReadIOException(e.getMessage(), e);
        }

        if (null == fileExt || fileExt.length()<=0){
            throw  new NotSupportDataFormatException(" only support jpg/bmp/png/gif data stream ");
        }else{
            return fileExt;
        }
    }
    /**
     * 注意，此构造只支持小型的图片，不支持其它类型的文件上传。
     * 图片格式 支持 jpg/png/gif/bmp 四种图片类型
     * @param data
     * @return
     */
    public static String guessImgMimeTypeByDataArray(byte[] data) throws NotSupportDataFormatException {
        String ext = guessImgExtByDataArray(data);
        return getImgRealMimeType(ext);
    }

    public static class StreamReader {
        private InputStream is;

        public StreamReader(InputStream is) {
            this.is = is;
        }

        /**
         * 获取流中的前两个字节
         */
        public int getUInt16() throws IOException {
            return (is.read() << 8 & 0xFF00) | (is.read() & 0xFF);
        }

        /**
         * 获取流中的前一个字节
         */
        public short getUInt8() throws IOException {
            return (short) (is.read() & 0xFF);
        }
    }
}
