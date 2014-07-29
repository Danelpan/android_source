package com.android.kit.activity;

import android.os.Bundle;
/**
 * 一个机遇网络的simple acyivity
 * @author Danel
 *
 */
public class SimpleBaseActivity extends BaseActivity implements ITaskListener {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	
	public void startLoader(int tag){
		runAsyncTask(this, tag);
	}

	@Override
	public Bundle onTaskStart(int mTaskTag) {
		return null;
	}

	@Override
	public Object onTaskLoading(Bundle bundle, int mTaskTag) {
		return null;
	}

	@Override
	public void onTaskFinish(Bundle bundle, int mTaskTag, Object result) {
		
	}
	
}
