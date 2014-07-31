package com.android.kit.common;

import com.android.kit.activity.ITaskListener;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;

public class AsyncTask<T> extends AsyncTaskLoader<T> {
	public static final int TASK_TAG = 0X999;

	private ITaskListener<T> listener;
	private int mTaskTag = 0x0;
	private Bundle mBundle;

	public AsyncTask(Context context) {
		this(context, TASK_TAG);
	}

	public AsyncTask(Context context, int tag) {
		super(context);
		this.mTaskTag = tag;
	}

	@Override
	public T loadInBackground() {
		return listener.onTaskLoading(mBundle, mTaskTag);
	}

	@Override
	public void deliverResult(T data) {
		super.deliverResult(data);
		listener.onTaskFinish(mBundle, mTaskTag, data);
	}

	@Override
	public void onStartLoading() {
		mBundle = listener.onTaskStart(mTaskTag);
		super.onStartLoading();
	}

	public void setTaskListener(ITaskListener<T> listener) {
		this.listener = listener;
	}
}
