package com.android.kit.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.kit.bitmap.KitBitmapCache;

public class KitAdapter extends BaseAdapter {
	private int[] mTo;
	private String[] mFrom;
	private List<? extends Map<String, Object>> mData;
	private int mResource;
	private int mDropDownResource;
	private LayoutInflater mInflater;
	private ArrayList<Integer> specialViewIds;
	private SpecialViewBinderListener binderListener;
	private int drawableId = 0;
	private int odd = -1;
	private int even = -1;
	private KitBitmapCache bitmapFactory;

	public KitAdapter(Context context,List<? extends Map<String, Object>> data, int defResource,String[] from, int[] to) {
		bitmapFactory = new KitBitmapCache(context);
		bitmapFactory.setSuffix(".pic");
		this.mData = data;
		this.mResource = (this.mDropDownResource = defResource);
		this.mFrom = from;
		this.mTo = to;
		this.mInflater = ((LayoutInflater) context.getSystemService("layout_inflater"));
	}
	/**
	 * 设置特殊处理的View
	 * @param binderListener
	 * @param viewID
	 */
	public void setSpecialViewBinderListener(SpecialViewBinderListener binderListener,Integer... viewID) {
		this.binderListener = binderListener;
		this.specialViewIds = new ArrayList<Integer>(Arrays.asList(viewID));
	}
	
	/**
	 * 获取需要处理视图的view的初始化背景图片的id，0，未设置
	 * @return
	 */
	public int getCurrentViewBackground() {
		return this.drawableId;
	}
	/**
	 * 设置后台进程加载图片时候view的默认背景图片
	 * @param drawableId
	 */
	public void setCurrentViewBackground(int drawableId) {
		this.drawableId = drawableId;
		bitmapFactory.setDefBitmapOfLoading(drawableId);
	}
	/**
	 * 设置工作线程的大小
	 * @param size
	 */
	public void setThreadSize(int size) {
		bitmapFactory.threadPoolsFile(size);
		bitmapFactory.threadPoolsNetWork(size);
	}
	
	/**
	 * 设置工作线程是否挂起，true为挂起，false唤起
	 * @param pause
	 */
	public void setThreadPause(boolean pause) {
		if (pause) {
			bitmapFactory.pause();
		} else {
			bitmapFactory.resume();
		}
	}
	/**
	 * 销毁当前adapter,中缓存的占用
	 */
	public void destroy() {
		bitmapFactory.destroy();
	}
	
	/**
	 * 获得当前容器大小
	 */
	public int getCount() {
		return this.mData.size();
	}

	
	public Object getItem(int position) {
		return this.mData.get(position);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View view = createViewFromResource(position, convertView, parent,this.mResource);
		return view;
	}

	private View createViewFromResource(int position, View convertView,ViewGroup parent, int resource) {
		View v;
		if (convertView == null) {
			convertView = this.mInflater.inflate(resource, parent, false);
			int[] to = this.mTo;
			int count = to.length;
			View[] holder = new View[count];
			for (int i = 0; i < count; i++) {
				holder[i] = convertView.findViewById(to[i]);
			}

			convertView.setTag(holder);
		}
		v = convertView;

		bindView(position, v);
		if ((this.odd != -1) && (this.even != -1)) {
			v.setBackgroundResource(position % 2 == 0 ? this.even : this.odd);
		}
		return v;
	}

	public void setDropDownViewResource(int resource) {
		this.mDropDownResource = resource;
	}

	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return createViewFromResource(position, convertView, parent,this.mDropDownResource);
	}

	private void bindView(int position, View view) {
		HashMap<String, ?> dataSet = (HashMap<String, ?>) this.mData.get(position);
		if (dataSet == null) {
			return;
		}

		View[] holder = (View[]) view.getTag();
		String[] from = this.mFrom;
		int[] to = this.mTo;
		int count = to.length;

		for (int i = 0; i < count; i++) {
			View v = holder[i];
			if (v != null) {
				Object data = dataSet.get(from[i]);
				boolean binder = true ;
				if ((this.binderListener != null)&& (this.specialViewIds.contains(Integer.valueOf(v.getId())))) {
					binder = this.binderListener.onSpecialViewBinder(v, data, view,dataSet, position);
				}
				if(binder){
					String text = data == null ? "" : data.toString();
					if (text == null) {
						text = "";
					}
					if ((v instanceof Checkable)) {
						if ((data instanceof Boolean)){
							((Checkable) v).setChecked(((Boolean) data).booleanValue());
						}else{
							try {
								((Checkable) v).setChecked(Boolean.valueOf(text));
							} catch (Exception e) {
								KitLog.err(e);
							}
						}
					} else if ((v instanceof TextView)){
						setViewText((TextView) v, text);
					}else if ((v instanceof ImageView)) {
						if ((data instanceof Integer)) {
							setViewImage((ImageView) v,((Integer) data).intValue());
						} else {
							ImageView iv = (ImageView) v;
							bitmapFactory.display(iv, text);
						}
					} else if ((v instanceof RatingBar)){
						try {
							((RatingBar) v).setRating(Float.valueOf(text));
						} catch (NumberFormatException e) {
							KitLog.err(e);
						}
					}else{
						throw new IllegalStateException(v.getClass().getName()+ " is not a "+ " view that can be bounds by this Adapter");
					}
				}
			}
		}
	}
	/**
	 * 设置每项的背景，可以设置两项单项和双向的背景，如果设置一项，那么另外一项也是这一项的背景
	 * @param even
	 * @param odd
	 */
	public void setItemBackground(int even, int odd) {
		this.even = even;
		this.odd = odd;
	}

	public void setViewImage(ImageView v, int value) {
		v.setImageResource(value);
	}

	public void setViewImage(ImageView v, String value) {
		try {
			v.setImageResource(Integer.parseInt(value));
		} catch (NumberFormatException nfe) {
			v.setImageURI(Uri.parse(value));
		}
	}

	public void setViewText(TextView v, String text) {
		v.setText(text);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
