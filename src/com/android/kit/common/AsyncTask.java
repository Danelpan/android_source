package com.android.kit.common;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.android.kit.activity.ITaskListener;

public class AsyncTask extends Thread {
	private ITaskListener mAsyncTaskListener;
	private int mTag;
	private boolean isCancel = false;
	private Bundle mBundle;

	public AsyncTask(ITaskListener listener, int tag) {
		this.mAsyncTaskListener = listener;
		this.mTag = tag;
		mBundle = mAsyncTaskListener.onTaskStart(mTag);
	}

	@Override
	public void run() {
		if (isCancel) {
			return;
		}
		final Object result = mAsyncTaskListener.onTaskLoading(mBundle, mTag);

		if (isCancel) {
			return;
		}

		Message msg = new Message();
		msg.obj = result;
		mHandler.sendMessage(msg);
	}

	public boolean isCancel() {
		return isCancel;
	}

	public void setCancel(boolean cancel) {
		this.isCancel = cancel;
		if (isCancel) {
			interrupt();
		}
	}

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Object result = msg.obj;
			mAsyncTaskListener.onTaskFinish(mBundle, mTag, result);
		}
	};
}
