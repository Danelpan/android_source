package com.android.kit.activity;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.SparseArray;

import com.android.kit.utils.KitLog;

public class BaseActivity extends FragmentActivity{
	private LinkedList<Thread> threadQueue;
	private ExecutorService threasPools;
	private SparseArray<Thread> sa;
	public boolean isDestroy = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
 	}
	/**
	 * 在线程中加载，和处理耗时事物
	 * @param task 异步工作线程回调
	 * @param tag 标记线程tag
	 */
	public TaskThread runAsyncTask(final AsyncTask task,final int tag){
		if(isDestroy){
			KitLog.e("BaseActivity", "Activity have Destroy……");
			return null;
		}
		if(null == threadQueue){
			threadQueue = new LinkedList<Thread>();
		}
		if(null == threasPools){
			threasPools = Executors.newFixedThreadPool(10);
		}
		if(null == sa){
			sa = new SparseArray<Thread>();
		}
		if(null != sa.get(tag)){
			sa.get(tag).interrupt();
			threadQueue.remove(sa.get(tag));
		}
		task.onTaskStart(tag);
		TaskThread thread = new TaskThread(task, tag);
		WeakReference<Thread> weakThread = new WeakReference<Thread>(thread); 
		sa.put(tag, weakThread.get());
		threadQueue.add(weakThread.get());
		if(!threasPools.isShutdown()){
			threasPools.submit(weakThread.get());
		}
		return (TaskThread) weakThread.get();
	}
	/**
	 * 销毁异步操作的线程，同时关闭线程池
	 */
	public void destroyAsync(){
		if(null != sa){
			sa.clear();
		}
		
		if(null != threadQueue && threadQueue.size()>0){
			Iterator<Thread> threads = threadQueue.iterator();
			while(threads.hasNext()){
				Thread thread = threads.next();
				thread.interrupt();
				KitLog.d(this.getClass().getSimpleName(), "destroyAsync and interrupt all threads");
			}
			if(null != threasPools && !threasPools.isShutdown()){
				threasPools.shutdown();
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		isDestroy = true;
		super.onDestroy();
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				destroyAsync();
			}
		});
		thread.start();
	}
	
	public class TaskThread extends Thread{
		private AsyncTask task;
		private int tag ;
		private boolean isCancel = false;
		public TaskThread(AsyncTask task, int tag){
			this.task = task;
			this.tag = tag;
		}
		@Override
		public void run() {
			final Object result = task.onTaskLoading(tag);
			if(!isCancel){
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						task.onTaskFinish(tag, result);
					}
				});
			}
			sa.delete(tag);
			threadQueue.remove(this);
		}
		public boolean isCancel() {
			return isCancel;
		}
		public void setCancel(boolean isCancel) {
			this.isCancel = isCancel;
		}
	}
}