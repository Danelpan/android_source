
package com.android.kit.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.ByteBuffer;

/**
 * 流处理工具类
 * 
 * @author Danel
 */
public final class KitStreamUtils {
    private KitStreamUtils() {
    }

    /**
     * 字符串转InputStream
     * 
     * @param str
     * @return
     */
    public final static InputStream String2InputStream(String data) {
        if (null == data) {
            throw new NullPointerException("data is null");
        }
        ByteArrayInputStream stream = new ByteArrayInputStream(data.getBytes());
        return stream;
    }

    /**
     * 输入流转字符串,以UTF-8转化
     * 
     * @param is
     * @return
     * @throws IOException
     */
    public final static String inputStream2String(InputStream is) throws Exception {
        return inputStream2String(is, "UTF-8");
    }

    /**
     * 输入流转字符串,以编码限制形式
     * 
     * @param is
     * @param encoding
     * @return
     * @throws Exception
     */
    public final static String inputStream2String(InputStream is, String encoding) throws Exception {
        if (is == null) {
            throw new Exception();
        }
        byte[] data = inputStream2Byte(is);
        return data == null ? null : new String(data, encoding);
    }

    /**
     * 输入流转字符串
     * 
     * @param is
     * @return
     * @throws IOException
     */
    public final static String readAsciiLine(InputStream is) throws IOException {
        StringBuffer result = new StringBuffer();
        byte[] b = new byte[1024 * 4];
        int i = -1;
        while ((i = is.read(b)) != -1) {
            result.append(new String(b, 0, i));
        }
        int length = result.length();
        if (length > 0 && result.charAt(length - 1) == '\r') {
            result.setLength(length - 1);
        }
        return new String(result);
    }

    /**
     * reader 转字符串
     * 
     * @param reader
     * @return
     * @throws IOException
     */
    public final static String reader2String(Reader reader) throws IOException {
        try {
            StringWriter writer = new StringWriter();
            char[] buffer = new char[1024 * 4];
            int count;
            while ((count = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, count);
            }
            return writer.toString();
        } finally {
            closeStream(reader);
        }
    }

    /**
     * 输入流转成字节流
     * 
     * @param is
     * @return
     */
    public final static byte[] inputStream2Byte(InputStream is) {
        ByteArrayOutputStream baos = (ByteArrayOutputStream) stream2OutputStream(is, 1024 * 4);
        return null != baos ? baos.toByteArray() : null;
    }

    /**
     * 字节数组转成一个缓冲的字节
     * 
     * @param bs
     * @return
     */
    public final static ByteBuffer byte2ByteBuffer(byte[] bs) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(bs.length);
        buffer.put(bs);
        buffer.position(0);
        return buffer;
    }

    /**
     * 字节数组转流
     * 
     * @param bs
     * @return
     */
    public final static InputStream byte2InputStream(byte[] bs) {
        InputStream is = new ByteArrayInputStream(bs);
        return is;
    }

    /**
     * 输入流转输出流
     * 
     * @param is
     * @return
     */
    public final static OutputStream stream2OutputStream(InputStream is, int size) {
        if (null == is) {
            throw new NullPointerException();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            byte[] b = new byte[size];
            int i = 0;
            try {
                while ((i = is.read(b)) != -1) {
                    baos.write(b, 0, i);
                }
            } catch (IOException e) {
            }
        } finally {
            closeStream(is);
        }
        return baos;
    }

    /**
     * 输入流转输出流
     * 
     * @param is
     * @return
     * @throws FileNotFoundException
     */
    public final static FileOutputStream file2OutputStream(File file) throws FileNotFoundException {
        FileOutputStream fos = new FileOutputStream(file);
        return fos;
    }

    /**
     * 关闭一个可关闭的流
     * @param closeables
     */
    public static final void closeStream(Closeable... closeables) {
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    KitLog.printStackTrace(e);
                }
            }
        }
    }

}
