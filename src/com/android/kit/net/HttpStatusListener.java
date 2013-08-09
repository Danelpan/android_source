/*
 * Copyright (C) 2012-2013 Author:Danel Email:danel.pan@sohu.com
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
package com.android.kit.net;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URLConnection;

/**
 * Create on 2012-12-1 下午1:28:28 
 * @author Danel 
 * @Email danel.pan@sohu.com
 * @description 接收连接服务器返回的状态码，具体状态吗，请参照HTTP所定义的状态码
 * {@link HttpStatusConfig},{@link HttpURLConnection}
 */
public interface HttpStatusListener {
	/**
	 * 如果该回调返回true的话，那么将不再执行下一步操作
	 * @param conn
	 * @param model
	 * @return
	 */
	public URLConnection onInstance(URLConnection conn,HttpModel model);
	/**
	 * 接收状态码的方法，该方法出现的情况有两种：1.一种是出现在UI线程中；2.出现在子线程中。
	 * 使用的时候请注意这两点
	 * @param responseCode 连接服务器返回的状态码
	 * @param mInputStream 获得一个网络的输入流
	 * @return
	 */
	public int onResponse(int responseCode,InputStream mInputStream);
}
