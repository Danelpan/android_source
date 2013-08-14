package com.android.kit.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;

public class KitJsonUtils {


	/**
	 * 将给定的HashMap里的数据存入到Intent中
	 * 
	 * @param map
	 *            给定的map集合
	 * @param intent
	 *            要存入的intent
	 * @return 包含了给定hashmap的key，value的Intent。
	 */
	public static Intent map2Intent(HashMap<String, Object> map, Intent intent) {
		for (Entry<String, Object> entry : map.entrySet()) {
			intent.putExtra(entry.getKey(), (String) entry.getValue());
		}
		return intent;
	}

	/**
	 * 将给定的JavaBean的集合，转化成存放HashMap的集合 <br>
	 * javabean里面的每个字段的字段名为map里的key，值为map里的value。<br>
	 * 一般在把bean的集合转化成 listView的adaper数据时使用。
	 * @param <T>
	 * @param beanList
	 *            要转化的bean的集合
	 * @return 转换好之后的集合
	 */
	@SuppressWarnings("rawtypes")
	public static <T> List<HashMap<String, Object>> beanList2Lis(List<T> beanList) {
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		if (beanList == null || beanList.size() == 0) {
			return list;
		}
		Class beanclass = beanList.get(0).getClass();
		HashMap<String, Method> methodNames = new HashMap<String, Method>();
		Method[] methods = beanclass.getDeclaredMethods();
		for (int j = 0; j < methods.length; j++) {
			String methodname = methods[j].getName();
			if (methodname.contains("get")&& methods[j].getParameterTypes().length == 0) {
				methodNames.put(tofirstLowerCase(methodname.substring(3,methodname.length())), methods[j]);
			}
		}
		for (T bean : beanList) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			for (Entry<String, Method> entry : methodNames.entrySet()) {
				try {
					map.put(entry.getKey(),entry.getValue().invoke(bean, new Object[] {}));
				} catch (Exception e) {

				}
			}
			list.add(map);
		}

		return list;
	}

	/**
	 * 将给定的json字符串转换成 HashMap ,注意返回的HashMap 的Value <br>
	 * 的值只可能是 String 和
	 * ArrayList<HashMap|integer|String> 类型<br>
	 * 解析给定的json对象时（包括其包含的对象）如果不是数组，则直接以key，<br>
	 * value的形式放入map中 如果是JSONArray 则已<br>
	 * key，Arraylist<Map|integer|String> 的形式放入map中,<br>
	 * ArrayList里面的map数据形式同上所述。<br>
	 * @param jsonStr
	 * @return
	 */
	public static HashMap<String, Object> getJsonData2Map(String jsonStr) {
		JSONObject json = null;
		try {
			json = new JSONObject(jsonStr);
		} catch (JSONException e) {

		}
		return getJsonData2Map(json);
	}

	public static HashMap<String, Object> getJsonData2Map(JSONObject json) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		if (json != null) {
			putJson2Map(json, map);
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	private static void putJson2Map(JSONObject json,HashMap<String, Object> map) {
		try {
			if (json != null) {
				Iterator<String> keys = json.keys();
				while (keys.hasNext()) {
					String key = keys.next();
					Object object = json.opt(key);
					if (object instanceof JSONObject) {
						putJson2Map((JSONObject) object, map);
					} else if (object instanceof JSONArray) {
						ArrayList<Object> list = new ArrayList<Object>();
						JSONArray array = (JSONArray) object;
						for (int i = 0; i < array.length(); i++) {
							Object subobj = array.get(i);
							if (subobj instanceof JSONObject) {
								list.add(getJsonData2Map((JSONObject) subobj));
							} else {
								list.add(subobj);
							}
						}
						map.put(key, list);
					} else {
						int i = 0;
						String repeat = key;
						while (map.containsKey(repeat)) {
							i++;
							repeat = key + i;
						}
						map.put(repeat, object == null ? "" : object.toString());
					}
				}
			}
		} catch (Exception e) {

		}
	}

	/**
	 * 把json字符串转化成javaBean
	 * 
	 * @param <T>
	 * @param jsonStr
	 *            json字符串
	 * @param beanclass
	 *            要转化的javabean的class
	 * @return 封装好数据之后的javabean
	 */
	public static <T> T getJsonData2Bean(String jsonStr, Class<T> beanclass) {
		if (jsonStr == null) {
			return null;
		}

		JSONObject json = null;
		try {
			json = new JSONObject(jsonStr);
		} catch (JSONException e) {

		}
		return getJsonObject2Bean(json, beanclass);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <T> T getJsonObject2Bean(JSONObject obj, Class<T> beanclass) {
		HashMap<String, Method> methodNames = new HashMap<String, Method>();
		T bb = null;
		try {
			bb = beanclass.newInstance();
			Method[] methods = beanclass.getDeclaredMethods();
			for (int j = 0; j < methods.length; j++) {
				String methodname = methods[j].getName();
				if (methodname.contains("set")) {
					methodNames.put(tofirstLowerCase(methodname.substring(3,methodname.length())), methods[j]);
				}
			}

			for (Entry<String, Method> entry : methodNames.entrySet()) {
				if ((obj.has(entry.getKey()) || obj.has(tofirstUpperCase(entry.getKey())))) {
					String fildname = entry.getKey();
					Object obje = obj.opt(entry.getKey());
					if (obje == null) {
						obje = obj.opt(tofirstUpperCase(entry.getKey()));
						fildname = tofirstUpperCase(entry.getKey());
					}

					if (obje == null || obje == JSONObject.NULL) {
						continue;
					}
					if (obje instanceof JSONArray) {
						JSONArray arr = (JSONArray) obje;
						Field field = beanclass.getDeclaredField(fildname);
						ParameterizedType ptype = (ParameterizedType) field.getGenericType();
						Type[] type = ptype.getActualTypeArguments();
						Class subbeanclass = (Class) type[0];
						ArrayList sublist = new ArrayList();
						for (int i = 0; i < arr.length(); i++) {
							Object subobj = arr.get(i);
							if (subobj instanceof JSONObject) {
								Object subbean = getJsonObject2Bean(
										(JSONObject) subobj, subbeanclass);
								sublist.add(subbean);
							} else {
								sublist.add(subobj);
							}
						}
						entry.getValue().invoke(bb, sublist);
					} else if (obje instanceof JSONObject) {
						Field field = beanclass.getDeclaredField(fildname);
						Type type = field.getGenericType();
						Class subbeanclass = (Class) type;
						Object subobj = getJsonObject2Bean((JSONObject) obje,
								subbeanclass);
						entry.getValue().invoke(bb, subobj);
					} else {
						try {
							entry.getValue().invoke(bb, obje.toString());
						} catch (Exception e) {

						}
					}
				}
			}
		} catch (Exception e) {

		}
		return bb;
	}

	/**
	 * 将字符串的首字符转化成小写
	 * 
	 * @param str
	 *            要转化的字符串
	 * @return 返回首字符变小写之后的字符串
	 */
	public static String tofirstLowerCase(String str) {
		if (str != null && str.length() > 0) {
			return str.substring(0, 1).toLowerCase()
					+ str.substring(1, str.length());
		} else {
			return str;
		}
	}

	/**
	 * 将字符串的首字符转化成大写
	 * 
	 * @param str
	 *            要转化的字符串
	 * @return 返回首字符变大写之后的字符串
	 */
	public static String tofirstUpperCase(String str) {

		if (str != null && str.length() > 0) {
			return str.substring(0, 1).toUpperCase()
					+ str.substring(1, str.length());
		} else if (str != null && str.length() == 0) {
			return str.toUpperCase();
		} else {
			return str;
		}

	}
}
