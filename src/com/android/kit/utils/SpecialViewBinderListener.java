package com.android.kit.utils;

import java.util.Map;

import android.view.View;
/**
 * 接口抽象适配器特殊处理控件
 * @author Danel
 *
 */
public interface SpecialViewBinderListener {

	/**
	 * 处理listView里需要特殊处理的视图
	 * @param v 需要特殊处理的视图
	 * @param data 需要特殊处理的视图所对应的数据
	 * @param parentView 特殊处理视图 所对应的整个条目的视图
	 * @param dataSet 特殊处理视图所处条目的所有数据
	 * @param position 特殊处理视图所处条目在listview中的位置。
	 */
	public boolean onSpecialViewBinder(View v,Object data,View parentView,Map<String, ?> dataSet,int position);

}
