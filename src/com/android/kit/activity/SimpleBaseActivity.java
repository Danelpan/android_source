package com.android.kit.activity;

import android.os.Bundle;
/**
 * 一个机遇网络的simple acyivity
 * @author Danel
 *
 */
public class SimpleBaseActivity extends BaseActivity implements TaskListener {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	
	public void startLoader(int tag){
		runAsyncTask(this, tag);
	}
	
	@Override
	public void onTaskStart(int mTaskTag) {
		
	}

	@Override
	public Object onTaskLoading(int mTaskTag) {
		
		return null;
	}

	@Override
	public void onTaskSuccess(int mTaskTag, Object result) {
		
	}

	@Override
	public void onTaskFailure(int mTaskTag, Object result) {
		
	}
	
}
