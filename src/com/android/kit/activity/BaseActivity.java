package com.android.kit.activity;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.SparseArray;

import com.android.kit.utils.KitLog;
import com.android.kit.utils.KitUtils;


public class BaseActivity extends FragmentActivity {
	private SparseArray<FutureTask<?>> mFutureTasks;
	private ExecutorService mExecutorService;
	private SparseArray<WeakReference<AsyncTask>> mSparseArray;
	public boolean isDestroy = false;
	public boolean isPause = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (null == mFutureTasks) {
			mFutureTasks = new SparseArray<FutureTask<?>>();
		}
		if (null == mExecutorService) {
			mExecutorService = KitUtils.getThreasPools();
		}
		if (null == mSparseArray) {
			mSparseArray = new SparseArray<WeakReference<AsyncTask>>();
		}
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
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				destroyAsync();
			}
		});
		thread.start();
	}

	@Override
	protected void onPause() {
		isPause = true;
		super.onPause();
	}
	
	public SparseArray<FutureTask<?>> runAsyncTask(ITaskListener task,int... tags) {
		for (int i = 0; i < tags.length; i++) {
			runAsyncTask(task, tags[i]);
		}
		return mFutureTasks;
	}

	/**
	 * 在线程中加载，和处理耗时事物
	 * 
	 * @param task 异步工作线程回调
	 * @param tag 标记线程tag
	 */
	public AsyncTask runAsyncTask(final ITaskListener task, final int tag) {
		if (isDestroy) {
			KitLog.e("BaseActivity", "Activity have Destroy……");
			return null;
		}
		
		removeTask(tag);
		Bundle bundle = task.onTaskStart(tag);
		AsyncTask mAsyncTask = new AsyncTask(bundle, task, tag);
		WeakReference<AsyncTask> weakThread = new WeakReference<AsyncTask>(mAsyncTask);
		if (!mExecutorService.isShutdown()) {
			FutureTask<?> futureTask = (FutureTask<?>) mExecutorService.submit(weakThread.get());
			mFutureTasks.put(tag, futureTask);
			mSparseArray.put(tag, weakThread);
		}
		return weakThread.get();
	}

	/**
	 * 销毁异步操作的线程，同时关闭线程池
	 */
	public void destroyAsync() {
		if (null != mSparseArray && mSparseArray.size() > 0) {
			for (int i = 0; i < mSparseArray.size(); i++) {
				int mTaskTag = mSparseArray.keyAt(i);
				removeAsyncTask(mTaskTag);
			}
			mSparseArray.clear();
		}
		if (null != mFutureTasks && mFutureTasks.size() > 0) {

			for (int i = 0; i < mFutureTasks.size(); i++) {
				int mTaskTag = mFutureTasks.keyAt(i);
				removeFutureTasks(mTaskTag);
			}
			mFutureTasks.clear();
		}
		if (null != mExecutorService && !mExecutorService.isShutdown()) {
			mExecutorService.shutdown();
		}
		mExecutorService = null;
	}

	public class AsyncTask extends Thread {
		private ITaskListener mAsyncTask;
		private int tag;
		private boolean isCancel = false;
		private Bundle bundle;

		public AsyncTask(Bundle bundle, ITaskListener task, int tag) {
			this.mAsyncTask = task;
			this.tag = tag;
			this.bundle = bundle;
		}

		@Override
		public void run() {
			if (isDestroy) {
				return;
			}

			if (isCancel) {
				removeTask(tag);
				return;
			}

			final Object result = mAsyncTask.onTaskLoading(bundle, tag);
			if (isDestroy) { // 如果当前界面已经销毁，那么取消回调
				return;
			}
			if (!isCancel) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mAsyncTask.onTaskFinish(bundle, tag, result);
					}
				});
			}
			removeTask(tag);
		}

		public boolean isCancel() {
			return isCancel;
		}

		public void setCancel(boolean isCancel) {
			this.isCancel = isCancel;
		}
	}
	
	public synchronized void removeTask(int tag){
		removeAsyncTask(tag);
		removeFutureTasks(tag);
	}
	
	private void removeAsyncTask(int tag){
		if (null == mSparseArray) {
			return;
		}
		
		WeakReference<AsyncTask> wrat = mSparseArray.get(tag);
		if (null == wrat) {
			return;
		}
		
		AsyncTask asyncTask = wrat.get();
		if (null == asyncTask) {
			return;
		}
		
		if (!asyncTask.isCancel) {
			asyncTask.setCancel(true);
		}
		asyncTask.interrupt();
		mSparseArray.remove(tag);
	}
	
	private void removeFutureTasks (int tag) {
		if (null == mFutureTasks) {
			return;
		}
		
		FutureTask<?> futureTask = mFutureTasks.get(tag);
		if (null == futureTask) {
			return;
		}
		
		if(!futureTask.isCancelled()){
			futureTask.cancel(true);
		}
		mFutureTasks.remove(tag);
	}
}