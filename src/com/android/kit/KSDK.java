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
package com.android.kit;

/**
 * android_source </br>
 * 2012-12-10 下午1:54:51
 * @author Danel
 * @summary 客户端快捷开发Jar包版本信息。</br>
 * 信息：版本号，名称，版本描述，功能添加，优化的信息，以及废除的信息情况。 <br>
 * 在每次更新信息和修改jar包的时候，必须更新如下的配置信息。
 */
public final class KSDK {
	private static String version = "1.1";
	private static String name = "kit-source";
	private static String summary = "更新网络请求，添加网络请求代理模式，优化网络";
	private static String deprecated = "";
	private static String function = "网络请求";
	private static String optimization = "网络请求";
	private static boolean debug = false;
	/**
	 * 获取Jar包的版本号
	 * @return
	 */
	public static String getVersion(){
		return KSDK.version;
	}
	/**
	 * 获取Jar包的名称
	 * @return
	 */
	public static String getName(){
		return KSDK.name;
	}
	/**
	 * 获取Jar包的描述信息
	 * @return
	 */
	public static String getSummary(){
		return KSDK.summary;
	}
	/**
	 * 获取Jar包的过时信息的描述
	 * @return
	 */
	public static String getDeprecated(){
		return KSDK.deprecated;
	}
	/**
	 * 获取Jar包的新功能信息的描述
	 * @return
	 */
	public static String getFunction(){
		return KSDK.function;
	}
	/**
	 * 获取Jar包的优化API信息的描述
	 * @return
	 */
	public static String getOptimization(){
		return KSDK.optimization;
	}
	/**
	 * 获取SDK是否是打印日志的
	 * @return
	 */
	public static boolean isDebug() {
		return debug;
	}
}
