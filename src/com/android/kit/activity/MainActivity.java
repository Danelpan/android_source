/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.android.kit.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.kit.activity.BaseActivity.AsyncTask;
import com.android.kit.bitmap.KitBitmapCache;
import com.android.kit.net.NetworkAgent;
import com.android.kit.utils.KitUtils;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class MainActivity extends BaseActivity implements AsyncTask {

	String[] imageUrls;
	KitBitmapCache bitmapFactory = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_image_grid);
		
		System.err.println("检查手机应用商网络："+KitUtils.isMobileNetworkOnline(this));
		System.err.println("检查手wifi网络："+KitUtils.isWifiOnline(this));
		System.err.println("检查手手机网络："+KitUtils.isNetworkOnline(this));
		
		
		imageUrls = new String[]{
				"http://r01.stu.sogou.com/3293457ccd83b000.jpg",
				"http://t02.pic.sogou.com/2da0f85d9919fbb3.jpg",
				"http://t02.pic.sogou.com/f57aea542a6a3170.jpg",
				"http://t01.pic.sogou.com/346fde4d10033f94.jpg",
				"http://t01.pic.sogou.com/90b82e4c533b8d4a_i.jpg",
				"http://t02.pic.sogou.com/9741145e46a53e65.jpg",
				"http://t01.pic.sogou.com/67a99f81e3299ca7_i.jpg",
				"http://t04.pic.sogou.com/b18ab5f58fb448e3.jpg",
				"http://t01.pic.sogou.com/3e8c464f4990cf4b_i.jpg",
				"http://t03.pic.sogou.com/433f0623c48da65d_i.jpg",
				"http://t01.pic.sogou.com/52774b4e46a53e65.jpg",
				"http://t01.pic.sogou.com/417aea8e46a53e65.jpg",
				"http://t03.pic.sogou.com/87cfbc202babb047.jpg",
				"http://t03.pic.sogou.com/c8d54da9e398c0b5.jpg",
				"http://t03.pic.sogou.com/3ee20c232cacedf1.jpg",
				"http://t01.pic.sogou.com/5a9f7bc054111190.jpg",
				"http://t04.pic.sogou.com/ec34d1bbcbf56276.jpg",
				"http://t04.pic.sogou.com/b8468a71cf5ae6fd_i.jpg",
				"http://t04.pic.sogou.com/82db2df321c27f15.jpg",
				"http://t01.pic.sogou.com/84193e4bf77a44bb.jpg",
				"http://t04.pic.sogou.com/26edf3b8c181cf56_i.jpg",
				"http://t03.pic.sogou.com/78f2e0a09bcc177b_i.jpg",
				"http://t04.pic.sogou.com/07db67faf196e622.jpg",
				"http://t02.pic.sogou.com/12a4ad550adb9695.jpg",
				"http://t01.pic.sogou.com/11ade7ce46a53e65.jpg",
				"http://t04.pic.sogou.com/dfc668b82fc3f239.jpg",
				"http://t04.pic.sogou.com/d666c73975119ef2_i.jpg",
				"http://t02.pic.sogou.com/d41fac1bf77a44bb.jpg",
				"http://t01.pic.sogou.com/87f8af4b7a50c03b_i.jpg",
				"http://t01.pic.sogou.com/782af14321c27f15.jpg",
				"http://t04.pic.sogou.com/d0f5297321c27f15.jpg",
				"http://t02.pic.sogou.com/dba7869a50b50408.jpg",
				"http://t04.pic.sogou.com/aaea2ff3658fa780.jpg",
				"http://t04.pic.sogou.com/7ead15faeca97966.jpg",
				"http://t01.pic.sogou.com/f9612604c2da36bb.jpg",
				"http://t04.pic.sogou.com/3c2dfbb054111190.jpg",
				"http://t03.pic.sogou.com/6af866aec6ba3391.jpg",
				"http://t04.pic.sogou.com/d3079efa50b50408.jpg",
				"http://t01.pic.sogou.com/8050f443a4f1e1f9.jpg",
				"http://t04.pic.sogou.com/0f7d833e910508aa.jpg",
				"http://t04.pic.sogou.com/68552d7e910508aa.jpg",
				"http://t04.pic.sogou.com/9c72a4b50adb9695.jpg",
				"http://t04.pic.sogou.com/30eaa83ec6ba3391.jpg",
				"http://t01.pic.sogou.com/f702dd0a50b50408.jpg",
				"http://t01.pic.sogou.com/5f0079ce910508aa.jpg",
				"http://t03.pic.sogou.com/280bdfa8d968b13e.jpg",
				"http://t03.pic.sogou.com/02808a6054111190.jpg",
				"http://t04.pic.sogou.com/d3f21e30d1bd3812_i.jpg",
				"http://t04.pic.sogou.com/aee02df8d968b13e.jpg",
				"http://t02.pic.sogou.com/83a01e99e398c0b5.jpg",
				"http://t01.pic.sogou.com/cef8b5c6ae6584b5.jpg",
				"http://t01.pic.sogou.com/3f11d5446080ebf8.jpg",
    			};


		GridView listView = (GridView) findViewById(R.id.gridview);
		((GridView) listView).setAdapter(new ImageAdapter(this));
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startImagePagerActivity(position);
			}
		});
//		for(int i=0;i<100;i++){
//			runAsyncTask(this,i);
//		}
//		bitmapFactory = new CMBBitmapCache(this);

	}

	private void startImagePagerActivity(int position) {
		Toast.makeText(this, ""+position, Toast.LENGTH_SHORT).show();
	}

	public class ImageAdapter extends BaseAdapter {
		public ImageAdapter(Context context){
			bitmapFactory = new KitBitmapCache(context);
			bitmapFactory.setCachePath(Environment.getExternalStorageDirectory().getPath()+"/测试环境目录");
			bitmapFactory.setDefBitmapOfLoading(R.drawable.ic_launcher);
			bitmapFactory.setDefBitmapOfFailure(R.drawable.ic_error);
		}
		
		@Override
		public int getCount() {
			return imageUrls.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = (View) getLayoutInflater().inflate(R.layout.item_grid_image,null);
			} 
			TextView imageView = (TextView) convertView.findViewById(R.id.image);
			bitmapFactory.display(imageView, imageUrls[position]);
			return convertView;
		}

	}

	@Override
	protected void onDestroy() {
		bitmapFactory.destroy();
		super.onDestroy();
	}

	@Override
	public void onTaskStart(int tag) {
		
		System.err.println("任务开始执行:"+tag);
	}
	
	@Override
	public void onTaskLoading(int tag) {
		System.err.println("任务执行中:"+tag);

		try {
			String is = NetworkAgent.getInstance().getString("http://99.48.237.101:9080/NeptuneApp/queryMerchants.json?_pro=0&_pla=pluto_andr_3.0.0_uni_demo&_ver=3.0.0&_mt=HTCEUROPE,HTCSAGA,4.0.4&appId=0e2caebb44aa4690a580351c3a3459d3&_appId=0e2caebb44aa4690a580351c3a3459d3&DeviceID=358967040034991&_r=no&_channel=demo&_uid=a1d546cdcbead6cd666d0cdc484c0206&_ss=480x800&sortType=&childTypeId=&districtId=&parentTypeId=&pageIndex=1&regionId=&cityNo=21", null, NetworkAgent.getInstance().POST);
			System.err.println(is);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onTaskFinish(int tag) {
		System.err.println("任务执行结束:"+tag);
	}
	
	
}