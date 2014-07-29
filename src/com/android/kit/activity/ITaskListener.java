package com.android.kit.activity;

import android.os.Bundle;

/**
 * 执行异步任务的接口，该接口回调是在线程中被回调的 <br>
 * 当任务完成之后，我们需要单个唤起跟新UI线程
 * 
 * @author Danel
 * 
 */
public interface ITaskListener {
	/**
	 * 任务开始的时候，该函数将会被回调，该函数的回调在UI线程中执行
	 */
	Bundle onTaskStart(int mTaskTag);

	/**
	 * 任务执行中，该函数将会被回调，该函数回调在线程中执行
	 */
	Object onTaskLoading(Bundle bundle, int mTaskTag);

	/**
	 * 任务结束后，该函数被毁掉，该函数的回调是在UI线程中执行的,具体的返回值result是 <br>
	 * onTaskLoading的结果集
	 */
	void onTaskFinish(Bundle bundle, int mTaskTag, Object result);

}
