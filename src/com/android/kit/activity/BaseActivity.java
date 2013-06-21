package com.android.kit.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class BaseActivity extends Activity{
	private LinkedList<Thread> threadQueue;
	private ExecutorService threasPools;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
 	}
	/**
	 * 在线程中加载，和处理耗时事物
	 */
	public void runAsyncTask(final AsyncTask task){
		if(null == threadQueue){
			threadQueue = (LinkedList<Thread>) Collections.synchronizedList(new ArrayList<Thread>());
		}
		if(null == threasPools){
			threasPools = Executors.newFixedThreadPool(10);
		}
		task.onTaskStart();
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				task.onTaskLoading();
				threadQueue.remove(this);
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						task.onTaskFinish();
					}
				});
			}
		});
		threadQueue.add(thread);
		threasPools.submit(thread);
	}
	/**
	 * 销毁异步操作的线程，同时关闭线程池
	 */
	public void destroyAsync(){
		if(null != threadQueue && threadQueue.size()>0){
			Iterator<Thread> threads = threadQueue.iterator();
			while(threads.hasNext()){
				Thread thread = threads.next();
				thread.interrupt();
				Log.d(this.getClass().getSimpleName(), "destroyAsync and interrupt all threads");
			}
			if(null != threasPools){
				threasPools.shutdown();
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		destroyAsync();
	}
	
	/**
	 * 执行异步任务的接口，该接口回调是在线程中被回调的
	 * <br>当任务完成之后，我们需要单个唤起跟新UI线程
	 * @author Danel
	 *
	 */
	public interface AsyncTask{
		/**
		 * 任务开始的时候，该函数将会被回调，该函数的回调在UI线程中执行
		 */
		void onTaskStart();
		/**
		 * 任务执行中，该函数将会被回调，该函数回调在线程中执行
		 */
		void onTaskLoading();
		/**
		 * 任务结束后，该函数被毁掉，该函数的回调是在UI线程中执行的
		 */
		void onTaskFinish();
	}
}