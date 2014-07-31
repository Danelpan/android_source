package com.android.kit.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.android.kit.common.IAsyncTask;

public class BaseActivity<T> extends FragmentActivity implements ITaskListener<T> {

	private IAsyncTask<T> mAsyncTask;

	public boolean isDestroy = false;
	public boolean isPause = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAsyncTask = new IAsyncTask<T>(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		isDestroy = false;
		isPause = false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		isDestroy = true;
		mAsyncTask.destoryAyncTasks();
	}

	@Override
	protected void onPause() {
		isPause = true;
		super.onPause();
	}

	@Override
	public Bundle onTaskStart(int taskTag) {
		return null;
	}

	@Override
	public T onTaskLoading(Bundle bundle, int taskTag) {
		return null;
	}

	@Override
	public void onTaskFinish(Bundle bundle, int taskTag, Object data) {

	}

	public void runAsyncTask(int taskTag) {
		runAsyncTask(this, taskTag);
	}

	public void runAsyncTask(int... taskTags) {
		for (int taskTag : taskTags) {
			runAsyncTask(taskTag);
		}
	}

	public void runAsyncTask(ITaskListener<T> taskListener, int taskTag) {
		mAsyncTask.runAsyncTask(taskListener, taskTag);
	}
}