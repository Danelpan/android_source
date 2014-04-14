
package com.android.kit.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.StringTokenizer;

import android.text.TextUtils;

import com.android.kit.cache.imge.FlushedInputStream;

/**
 * 文件工具
 * 
 * @author Danel
 */
public final class KitFileUtils {

    private KitFileUtils() {
    }

    /**
     * 级连创建文件，通过一个分解字符串的形式循环创建目录，例如：<br>
     * <br>
     * /data/data/com.android.kit <br>
     * <br>
     * 这样的格式
     * 
     * @param path
     */
    public static final File createFile(String path) {
        StringTokenizer st = new StringTokenizer(path, File.separator);
        String rootPath = st.nextToken() + File.separator;
        String tempPath = rootPath;
        File boxFile = null;
        while (st.hasMoreTokens()) {
            rootPath = st.nextToken() + File.separator;
            tempPath += rootPath;
            boxFile = new File(tempPath);
            if (!boxFile.exists()) {
                boxFile.mkdirs();
            }
        }
        return boxFile;
    }

    /**
     * 把字符串数据写到文件
     * 
     * @param data
     * @param file
     */
    public static final synchronized void str2File(String data, File file, boolean append) {
        if (TextUtils.isEmpty(data)) {
            throw new NullPointerException("data of String is null");
        }
        FileWriter mWriter = null;
        try {
            mWriter = new FileWriter(file, append);
            mWriter.write(data);
            mWriter.flush();
        } catch (IOException e) {
            KitLog.printStackTrace(e);
        } finally {
            KitStreamUtils.closeStream(mWriter);
        }

    }

    /**
     * 覆盖写文件操作
     * 
     * @param data
     * @param file
     */
    public static final synchronized void str2File(String data, File file) {
        str2File(data, file, false);
    }

    public static final synchronized String file2Str(File file) {
        if (null == file) {
            throw new NullPointerException("File is null");
        }
        FileReader mReader = null;
        try {
            mReader = new FileReader(file);
            char[] cTemp = new char[1024 * 4];
            int i = -1;
            StringBuilder strBuilder = new StringBuilder();
            while ((i = mReader.read(cTemp)) != -1) {
                strBuilder.append(cTemp, 0, i);
            }
            return strBuilder.toString();
        } catch (FileNotFoundException e) {
            KitLog.printStackTrace(e);
        } catch (IOException e) {
            KitLog.printStackTrace(e);
        } finally {
            KitStreamUtils.closeStream(mReader);
        }
        return null;
    }

    /**
     * 字节写入文件
     * 
     * @param bs
     * @param file
     */
    public static final synchronized void byte2File(byte[] bs, File file) {
        stream2File(KitStreamUtils.byte2InputStream(bs), file);
    }

    /**
     * 保存流到文件
     * 
     * @param is
     * @param file
     */
    public static final synchronized void stream2File(InputStream is, File file) {
        BufferedOutputStream out = null;
        FlushedInputStream in = null;
        FileOutputStream outputStream = null;
        try {
            in = new FlushedInputStream(new BufferedInputStream(is, 8 * 1024));
            outputStream = new FileOutputStream(file);
            out = new BufferedOutputStream(outputStream, 8 * 1024);
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
        } catch (Exception e) {
            if (file != null && file.exists()) {
                file.delete();
            }
            file = null;
        } finally {
            KitStreamUtils.closeStream(out);
            KitStreamUtils.closeStream(outputStream);
            KitStreamUtils.closeStream(in);
            KitStreamUtils.closeStream(is);
        }
    }

    /**
     * 获取存储的class
     * 
     * @param name
     * @return
     */
    public static final Object file2Object(File file) {
        if (null == file) {
            throw new NullPointerException("file is null ...");
        }
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        if (file.exists()) {
            try {
                fis = new FileInputStream(file);
                ois = new ObjectInputStream(fis);
                return ois.readObject();
            } catch (FileNotFoundException e) {
                KitLog.printStackTrace(e);
            } catch (IOException e) {
                KitLog.printStackTrace(e);
            } catch (ClassNotFoundException e) {
                KitLog.printStackTrace(e);
            } finally {
                KitStreamUtils.closeStream(ois);
                KitStreamUtils.closeStream(fis);
            }
        }
        return null;
    }

    /**
     * 保存类到存储,必须是实现了序列化之后的操作
     * 
     * @param name
     * @param obj
     */
    public static final void object2File(Object obj, File file) {
        if (null == obj) {
            throw new NullPointerException("Object is null ...");
        }
        if (null == file) {
            throw new NullPointerException("file is null ...");
        }
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        if (file.exists()) {
            file.delete();
        }
        try {
            fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(obj);
            oos.flush();
            fos.flush();
        } catch (FileNotFoundException e) {
            KitLog.printStackTrace(e);
        } catch (IOException e) {
            KitLog.printStackTrace(e);
        } finally {
            KitStreamUtils.closeStream(oos);
            KitStreamUtils.closeStream(fos);
        }
    }

    /**
     * 文件复制
     * 
     * @param fileFrom
     * @param fileTo
     * @return
     */
    public static final boolean copyFile2File(File from, File to) {
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream(from);
            out = new FileOutputStream(to);
            byte[] bt = new byte[1024 * 4];
            int count;
            while ((count = in.read(bt)) > 0) {
                out.write(bt, 0, count);
            }
            out.flush();
            return true;
        } catch (IOException e) {
            KitLog.printStackTrace(e);
        } finally {
            KitStreamUtils.closeStream(in);
            KitStreamUtils.closeStream(out);
        }
        return false;
    }

    /**
     * 清除该文件下的所有文件
     * @param file
     * @return
     */
    public static int clearFile(File file) {
        int deletedFiles = 0;

        if (null == file || !file.exists()) {
            return deletedFiles;
        }

        try {
            if (file.isDirectory()) {
                for (File child : file.listFiles()) {
                    if (child.isDirectory()) {
                        deletedFiles += clearFile(child);
                    }

                    if (child.delete()) {
                        deletedFiles++;
                    }
                }
            } else {
                if (file.delete()) {
                    deletedFiles++;
                }
            }
        } catch (Exception e) {
            KitLog.printStackTrace(e);
        }
        return deletedFiles;
    }
}
