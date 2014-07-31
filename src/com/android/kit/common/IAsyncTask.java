package com.android.kit.common;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;

import com.android.kit.activity.ITaskListener;

public class IAsyncTask<T> {
	@SuppressLint("UseSparseArrays")
	private Map<Integer, AsyncTask<T>> tasks = Collections.synchronizedMap(new HashMap<Integer, AsyncTask<T>>());
	private Context mContext;
	
	public IAsyncTask(Context context) {
		this.mContext = context;
	}

	public void runAsyncTask(ITaskListener<T> taskListener,int... taskTags) {
		for (int taskTag : taskTags) {
			runAsyncTask(taskListener,taskTag);
		}
	}

	public void runAsyncTask(ITaskListener<T> taskListener, int taskTag) {
		AsyncTask<T> asyncTask = new AsyncTask<T>(mContext, taskTag);
		asyncTask.setTaskListener(taskListener);
		insertTask(asyncTask, taskTag);
	}

	public void removeTask(int key) {
		AsyncTask<T> task = getAsyncTask(key);
		if (null != task) {
			task.stopLoading();
			tasks.remove(key);
		}
	}

	public void insertTask(AsyncTask<T> task, int key) {
		if (getAsyncTask(key) == null) {
			tasks.put(key, task);
		}
	}

	public AsyncTask<T> getAsyncTask(int key) {
		AsyncTask<T> task = tasks.get(key);
		return task;
	}
	
	public void destoryAyncTasks(){
		Collection<Integer> keys = tasks.keySet();
		for (Integer key : keys) {
			removeTask(key);
		}
		tasks.clear();
	}
}
