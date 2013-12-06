package com.android.kit.activity;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseArray;

import com.android.kit.utils.KitLog;
import com.android.kit.utils.KitUtils;

public class BaseFragment extends Fragment{
	private SparseArray<FutureTask<?>> mFutureTasks;
	private ExecutorService mExecutorService;
	private SparseArray<WeakReference<AsyncTask>> mSparseArray;
	public boolean isDestroy = false;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
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
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		isDestroy = false;
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}
	
	public SparseArray<FutureTask<?>> runAsyncTask(TaskListener task,int ...tags){
		for(int i=0;i<tags.length;i++){
			runAsyncTask(task,tags[i]);
		}
		return mFutureTasks;
	}
	
	/**
	 * 在线程中加载，和处理耗时事物
	 * @param task 异步工作线程回调
	 * @param tag 标记线程tag
	 */
	public AsyncTask runAsyncTask(final TaskListener task,final int tag){
		if(isDestroy){
			KitLog.e("BaseActivity", "Activity have Destroy……");
			return null;
		}
		if(null == mFutureTasks){
			mFutureTasks = new SparseArray<FutureTask<?>>();
		}
		if(null == mExecutorService){
			mExecutorService = KitUtils.getThreasPools();
		}
		if(null == mSparseArray){
			mSparseArray = new SparseArray<WeakReference<AsyncTask>>();
		}
		if(null != mSparseArray.get(tag) || null != mFutureTasks.get(tag)){
			if(null != mSparseArray.get(tag) && null != mSparseArray.get(tag).get()){
				if(!mSparseArray.get(tag).get().isCancel){
					mSparseArray.get(tag).get().setCancel(true);
				}
				mSparseArray.remove(tag);
			}
			if(null != mFutureTasks.get(tag)){
				mFutureTasks.get(tag).cancel(true);
				mFutureTasks.remove(tag);
			}
		}
		task.onTaskStart(tag);
		AsyncTask mAsyncTask = new AsyncTask(task, tag);
		WeakReference<AsyncTask> weakThread = new WeakReference<AsyncTask>(mAsyncTask); 
		if(!mExecutorService.isShutdown()){
			FutureTask<?> futureTask = (FutureTask<?>) mExecutorService.submit(weakThread.get());
			mFutureTasks.put(tag,futureTask);
			mSparseArray.put(tag, weakThread);
		}
		return weakThread.get();
	}
	/**
	 * 销毁异步操作的线程，同时关闭线程池
	 */
	public void destroyAsync(){
		if(null != mSparseArray && mSparseArray.size()>0){
			for (int i = 0; i < mSparseArray.size(); i++) {
				int mTaskTag = mSparseArray.keyAt(i);
				AsyncTask mAsyncTask = mSparseArray.get(mTaskTag).get();
				if(null != mAsyncTask && !mAsyncTask.isCancel){
					mAsyncTask.setCancel(true);
				}
			}
			mSparseArray.clear();
		}
		
		if(null != mFutureTasks && mFutureTasks.size()>0){
			
			for (int i = 0; i < mFutureTasks.size(); i++) {
				int mTaskTag = mFutureTasks.keyAt(i);
				FutureTask<?> mFutureTask = mFutureTasks.get(mTaskTag);
				if(null != mFutureTask && !mFutureTask.isCancelled()){
					mFutureTask.cancel(true);
				}
			}
			mFutureTasks.clear();
		}
		
		if(null != mExecutorService && !mExecutorService.isShutdown()){
			mExecutorService.shutdown();
		}
		mExecutorService = null;
	}
	
	public class AsyncTask extends Thread{
		private TaskListener mAsyncTask;
		private int tag ;
		private boolean isCancel = false;
		public AsyncTask(TaskListener task, int tag){
			this.mAsyncTask = task;
			this.tag = tag;
		}
		@Override
		public void run() {
			final Object result = mAsyncTask.onTaskLoading(tag);
			if(isDestroy || getActivity() == null){ //如果当前界面已经销毁，那么取消回调
				return;
			}
			if(!isCancel){
				getActivity().runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						mAsyncTask.onTaskSuccess(tag, result);
					}
				});
			}
			mFutureTasks.delete(tag);
			mSparseArray.delete(tag);
		}
		public boolean isCancel() {
			return isCancel;
		}
		public void setCancel(boolean isCancel) {
			this.isCancel = isCancel;
		}
	}
}
