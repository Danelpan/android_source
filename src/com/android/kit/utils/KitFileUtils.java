
package com.android.kit.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.StringTokenizer;

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
     * 级连创建文件，通过一个分解字符串的形式循环创建目录
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
            try {
                if (out != null) {
                    out.close();
                }
                if (null != outputStream) {
                    outputStream.close();
                }
                if (in != null) {
                    in.close();
                }
                if (null != is) {
                    is.close();
                }
            } catch (final IOException e) {
            }
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
                try {
                    ois.close();
                    fis.close();
                } catch (IOException e) {
                }
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
    public static final void object2File(Object obj,File file) {
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
            try {
                oos.close();
                fos.close();
            } catch (IOException e) {
            }
        }
    }
}
