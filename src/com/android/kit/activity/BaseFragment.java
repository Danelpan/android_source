package com.android.kit.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.android.kit.common.IAsyncTask;

public class BaseFragment<T> extends Fragment implements ITaskListener<T>{
	private IAsyncTask<T> mAsyncTask;	
	public boolean isDestroy = false;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mAsyncTask = new IAsyncTask<T>(getActivity());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		isDestroy = true;
		mAsyncTask.destoryAyncTasks();
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
