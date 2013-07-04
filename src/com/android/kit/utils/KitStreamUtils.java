package com.android.kit.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;

/**
 * 流处理工具类
 * @author Danel
 *
 */
public final class KitStreamUtils {
	private KitStreamUtils(){}
	
	/**
	 * 输入流转字符串
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public final static String stream2String(InputStream is) throws Exception {
		if (is == null) {
			throw new Exception();
		}
		byte[] data = stream2Byte(is);
		return data == null ? null : new String(data);
	}
	/**
	 * 输入流转字符串,以编码限制形式
	 * @param is
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	public final static String stream2String(InputStream is,String encoding) throws Exception {
		if (is == null) {
			throw new Exception();
		}
		byte[] data = stream2Byte(is);
		return data == null ? null : new String(data, encoding);
	}
	
	/**
	 * 输入流转字符串
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
	
	public final static String readFully(Reader reader) throws IOException {
		try {
			StringWriter writer = new StringWriter();
			char[] buffer = new char[1024];
			int count;
			while ((count = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, count);
			}
			return writer.toString();
		} finally {
			reader.close();
		}
	}
	
	/**
	 * 输入流转成字节流
	 * @param is
	 * @return
	 */
	public final static byte[] stream2Byte(InputStream is){
		ByteArrayOutputStream baos = (ByteArrayOutputStream) stream2Output(is,1024*4);
		return null!=baos?baos.toByteArray():null;
	}
	/**
	 * 输入流转输出流
	 * @param is
	 * @return
	 */
	public static OutputStream stream2Output(InputStream is,int size){
		if(null == is){
			throw new NullPointerException();
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try{
			byte[] b = new byte[1024 * 3];
			int i = 0;
			try {
				while ((i = is.read(b)) != -1) {
					baos.write(b, 0, i);
				}
			} catch (IOException e) {
			}
		}finally{
			try {
				is.close();
			} catch (IOException e) {
			}
		}
		return baos;
	}
	
	
}
