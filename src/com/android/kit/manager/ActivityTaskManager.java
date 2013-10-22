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
package com.android.kit.manager;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import android.app.Activity;

/**
 * android_source
 * 2012-12-10 下午2:33:51
 * @author Danel
 * @summary activity活动管理类，该类是一个简单的Activity栈；</br>
 * 通过改工具，客户端使用者可以自行维护属于自己的Activity栈；</br>
 * 注意事项：建议使用者无无特殊情况，不建议使用,并且该类属于单例模式
 */
public class ActivityTaskManager {
	private static ActivityTaskManager ATM = null;
	private static Map<String, Activity> activityList = null;
	private ActivityTaskManager(){
		activityList = Collections.synchronizedMap(new LinkedHashMap<String,Activity>());
	}
	/**
	 * 实例化{@link ActivityTaskManager}；</br>
	 * 使用方法:
	 * <pre>
	 * ActivityTaskManager atm = ActivityTaskManager.getInstance();
	 * atm.putXXX();
	 * atm.getXXX();
	 * </pre>
	 * @return
	 */
	public static synchronized ActivityTaskManager getInstance() {
		if (ATM == null) {
			ATM = new ActivityTaskManager();
		}
		return ATM;
	}
	
	/**
	 * 将activity添加到管理器，由统一的管理器来维护
	 * @param name 当前Activity的key值
	 * @param activity 当前Activity的实例
	 */
	public void putActivity(String name, Activity activity) {
		activityList.put(name, activity);
	}
	/**
	 * 从管理器中的Activity对象。
	 * @param name map中的Activity的Key值
	 */
	public Activity getActivity(String name) {
		return activityList.get(name);
	}
	
	/**
	 * 判断管理器的Activity是否为空。
	 * @return 当且当管理器中的Activity对象为空时返回true，否则返回false。
	 */
	public boolean isEmpty() {
		return activityList.isEmpty();
	}
	
	/**
	 * 管理器中Activity对象的个数。
	 * @return 
	 */
	public int size() {
		return activityList.size();
	}
	
	/**
	 * 返回管理器中是否包含指定的名字。
	 * @param name 要查找的名字。
	 * @return 当且仅当包含指定的名字时返回true, 否则返回false。
	 */
	public boolean containsName(String name) {
		return activityList.containsKey(name);
	}
	
	/**
	 * 返回管理器中是否包含指定的Activity。
	 * @param activity 要查找的Activity。
	 * @return 当且仅当包含指定的Activity对象时返回true, 否则返回false。
	 */
	public boolean containsActivity(Activity activity) {
		return activityList.containsValue(activity);
	}
	/**
	 * 关闭所有活动的Activity。
	 */
	public void closeAllActivity() {
		Set<String> activityNames = activityList.keySet();
		for (String string : activityNames) {
			finisActivity(activityList.get(string));
		}
		activityList.clear();
	}

	/**
	 * 关闭所有活动的Activity除了指定的一个之外。
	 * @param nameSpecified 指定的不关闭的Activity对象的名字。
	 */
	public void closeAllActivityExceptOne(String nameSpecified) {
		Set<String> activityNames = activityList.keySet();
		Activity activitySpecified = activityList.get(nameSpecified);
		for (String name : activityNames) {
			if (!name.equals(nameSpecified)) {
				finisActivity(activityList.get(name));
			}
		}
		activityList.clear();
		activityList.put(nameSpecified, activitySpecified);
	}

	/**
	 * 移除Activity对象,如果它未结束则结束它。
	 * @param name Activity对象的名字。
	 */
	public void removeActivity(String name) {
		Activity activity = activityList.remove(name);
		finisActivity(activity);
	}

	/**
	 * 结束指定的Activity
	 * @param activity 指定的Activity。
	 */
	private void finisActivity(Activity activity) {
		if (activity != null && !activity.isFinishing()) {
			activity.finish();
		}
	}
}
