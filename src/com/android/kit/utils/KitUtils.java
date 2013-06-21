/**
 * Copyright (c) 2012-2013, Danel(E-mail:danel.pan@sohu.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.kit.utils;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;


/**
 * android_source
 * 2012-12-11 下午5:26:30
 * @author Danel
 * @summary 提供一些公用的方法工具
 */
public class KitUtils {
	/**
	 * Returns the remainder of 'reader' as a string, closing it when done.
	 * @param reader
	 */
	public static String readFully(Reader reader) throws IOException {
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
	 * Returns the ASCII characters up to but not including the next "\r\n", or
	 * "\n".
	 * @param in
	 * @throws java.io.EOFException if the stream is exhausted before the next newline
	 *     character.
	 */
	public static String readAsciiLine(InputStream in) throws IOException {
		StringBuilder result = new StringBuilder(80);
		while (true) {
			int c = in.read();
			if (c == -1) {
				throw new EOFException();
			} else if (c == '\n') {
				break;
			}
			
			result.append((char) c);
		}
		int length = result.length();
		if (length > 0 && result.charAt(length - 1) == '\r') {
			result.setLength(length - 1);
		}
		return result.toString();
	}

}
