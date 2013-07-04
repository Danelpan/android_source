package com.android.kit.utils;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Json处理工具类
 * @author Danel
 *
 */
public final class KitJSONUtils {
	private KitJSONUtils(){}
	/**
	 * json字符串转map，以key和value的形式成对存储
	 * @param jsonStr
	 * @return
	 */
	public final static HashMap<String,String> json2Map(String jsonStr){
		HashMap<String,String> map = new HashMap<String, String>();
		try {
			JSONObject object = new JSONObject(jsonStr);
		} catch (JSONException e) {
			
		}
		return map;
	}
}
