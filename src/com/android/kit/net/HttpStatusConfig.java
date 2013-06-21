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

/**
 * Create on 2012-12-1 下午1:33:33 
 * @author Danel 
 * @Email danel.pan@sohu.com
 * @description 配置服务器状态的返回种类，与JDK文档中相一致
 */
public class HttpStatusConfig {
	/**
	 * @description HTTP 状态码 200：OK。
	 */
	public static final int HTTP_OK = 200;
	
	/**
	 * @description HTTP 状态码 201：Created。
	 */
	public static final int HTTP_CREATED = 201;
	
	/**
	 * @description HTTP 状态码 202：Accepted。
	 */
	public static final int HTTP_ACCEPTED = 202;
	
	/**
	 * @description HTTP 状态码 203：Non-Authoritative Information。
	 */
	public static final int HTTP_NOT_AUTHORITATIVE  = 203;
	
	/**
	 * @description HTTP 状态码 204：No Content。
	 */
	public static final int HTTP_NO_CONTENT = 204;
	
	/**
	 * @description HTTP 状态码 205：Reset Content。
	 */
	public static final int HTTP_RESET = 205;
	
	/**
	 * @description HTTP 状态码 206：Partial Content。
	 */
	public static final int HTTP_PARTIAL  = 206;
	
	/**
	 * @description HTTP 状态码 300：Multiple Choices。
	 */
	public static final int HTTP_MULT_CHOICE = 300; 
	
	/**
	 * @description HTTP 状态码 301：Moved Permanently。
	 */
	public static final int HTTP_MOVED_PERM = 301; 
	
	/**
	 * @description HTTP 状态码 302：Temporary Redirect。
	 */
	public static final int HTTP_MOVED_TEMP = 302; 
	
	/**
	 * @description HTTP 状态码 303：See Other。
	 */
	public static final int HTTP_SEE_OTHER  = 303; 
	
	/**
	 * @description HTTP 状态码 304：Not Modified。
	 */
	public static final int HTTP_NOT_MODIFIED = 304; 
	
	/**
	 * @description HTTP 状态码 305：Use Proxy。
	 */
	public static final int HTTP_USE_PROXY = 305; 
	
	/**
	 * @description HTTP 状态码 400：Bad Request。
	 */
	public static final int HTTP_BAD_REQUEST = 400; 
	
	/**
	 * @description HTTP 状态码 401：Unauthorized。
	 */
	public static final int HTTP_UNAUTHORIZED = 401; 
	
	/**
	 * @description HTTP 状态码 402：Payment Required。
	 */
	public static final int HTTP_PAYMENT_REQUIRED = 402; 
	
	/**
	 * @description  HTTP 状态码 403：Forbidden。
	 */
	public static final int HTTP_FORBIDDEN  = 403; 
	
	/**
	 * @description HTTP 状态码 404：Not Found。
	 */
	public static final int HTTP_NOT_FOUND = 404; 
	
	/**
	 * @description HTTP 状态码 405：Method Not Allowed。
	 */
	public static final int HTTP_BAD_METHOD = 405; 
	
	/**
	 * @description HTTP 状态码 406：Not Acceptable。
	 */
	public static final int HTTP_NOT_ACCEPTABLE  = 406; 
	
	/**
	 * @description HTTP 状态码 407：Proxy Authentication Required。
	 */
	public static final int HTTP_PROXY_AUTH = 407;  
	
	/**
	 * @description HTTP 状态码 408：Request Time-Out。
	 */
	public static final int HTTP_CLIENT_TIMEOUT = 408;  
	
	/**
	 * @description HTTP 状态码 409：Conflict。
	 */
	public static final int HTTP_CONFLICT = 409;  
	
	/**
	 * @description HTTP 状态码 410：Gone。
	 */
	public static final int HTTP_GONE  = 410;  
	
	/**
	 * @description HTTP 状态码 411：Length Required。
	 */
	public static final int HTTP_LENGTH_REQUIRED = 411;  
	
	/**
	 * @description HTTP 状态码 412：Precondition Failed。
	 */
	public static final int HTTP_PRECON_FAILED = 412;  
	
	/**
	 * @description HTTP 状态码 413：Request Entity Too Large。
	 */
	public static final int HTTP_ENTITY_TOO_LARGE  = 413; 
	/**
	 * @description HTTP 状态码 414：Request-URI Too Large。
	 */
	public static final int HTTP_REQ_TOO_LONG = 414;   
	
	/**
	 * @description HTTP 状态码 415：Unsupported Media Type。
	 */
	public static final int HTTP_UNSUPPORTED_TYPE   = 415; 
	
	/**
	 * @description HTTP 状态码 500：Internal Server Error。
	 */
	public static final int HTTP_INTERNAL_ERROR = 500; 
	
	/**
	 * @description HTTP 状态码 501：Not Implemented。
	 */
	public static final int HTTP_NOT_IMPLEMENTED = 501; 
	
	/**
	 * @description HTTP 状态码 502：Bad Gateway。
	 */
	public static final int HTTP_BAD_GATEWAY = 502; 
	
	/**
	 * @description HTTP 状态码 503：Service Unavailable。
	 */
	public static final int HTTP_UNAVAILABLE  = 503; 
	
	/**
	 * @description HTTP 状态码 504：Gateway Timeout。
	 */
	public static final int HTTP_GATEWAY_TIMEOUT = 504; 
	
	/**
	 * @description HTTP 状态码 505：HTTP Version Not Supported。
	 */
	public static final int HTTP_VERSION = 505; 
    
}
