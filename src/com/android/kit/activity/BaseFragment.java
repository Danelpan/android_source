package com.android.kit.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;

public class BaseFragment extends Fragment{
	private List<Thread> threadQueue;
	private ExecutorService threasPools;
	
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
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				destroyAsync();
			}
		});
		thread.start();
		super.onDestroy();
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
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}
	
	/**
	 * 在线程中加载，和处理耗时事物
	 * @param task 异步工作线程回调
	 * @param tag 标记线程tag
	 */
	public void runAsyncTask(final AsyncTask task,final int tag){
		if(null == threadQueue){
			threadQueue = (List<Thread>) Collections.synchronizedList(new ArrayList<Thread>());
		}
		if(null == threasPools){
			threasPools = Executors.newFixedThreadPool(10);
		}
		task.onTaskStart(tag);
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				final Object result = task.onTaskLoading(tag);
				threadQueue.remove(this);
				getActivity().runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						task.onTaskFinish(tag,result);
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
			if(null != threasPools && !threasPools.isShutdown()){
				threasPools.shutdown();
			}
		}
	}
	
}
