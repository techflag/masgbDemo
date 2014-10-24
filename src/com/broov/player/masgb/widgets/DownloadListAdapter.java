package com.broov.player.masgb.widgets;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.broov.player.R;
import com.broov.player.masgb.entity.DownloadInfo;
import com.broov.player.masgb.entity.JsonUtil;
import com.broov.player.masgb.ui.DownloadingActivity;
import com.broov.player.masgb.utils.MyIntents;

public class DownloadListAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<HashMap<Integer, String>> dataList;
	private ArrayList<DownloadInfo> list;
	//private Dao dao;
	
	public DownloadListAdapter(Context context) {
		mContext = context;
		dataList = new ArrayList<HashMap<Integer, String>>();
		
		
		//dao=new Dao(mContext);
	}
//	public void setDataList(ArrayList<HashMap<Integer, String>> dataList) {
//		this.dataList = dataList;
//	}
//	private ArrayList<DownloadInfo> getData() {
//		ArrayList<DownloadInfo> list = new ArrayList<DownloadInfo>();
//		SharedPreferences sharedPreferences = mContext.getSharedPreferences("downData",
//				Context.MODE_PRIVATE);
//		// getString()第二个参数为缺省值，如果preference中不存在该key，将返回缺省值
//		String jsonArrayStart = sharedPreferences.getString("json", "");
//		if (!jsonArrayStart.equals(null)) {
//			list = JsonUtil.changeJsonToArray(jsonArrayStart);
//			return list;
//		}
//		return null;
//	}
	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * 
	 * @param url
	 * @param fileName
	 * @param from 0:正在下载页面,1:是从课件列表页面
	 */
	public void addItem(String url, String fileName) {
		addItem(url, fileName, false);
	}

	public void addItem(String url, String fileName, boolean isPaused) {
		HashMap<Integer, String> item = ViewHolder.getItemDataMap(url,
				fileName, null, null, isPaused + "");
		//if(from==1){
			//dataList.clear();
			dataList.add(item);
		//}
		this.notifyDataSetChanged();
	}

	public void removeItem(String url) {
		String tmp;
		for (int i = 0; i < dataList.size(); i++) {
			tmp = dataList.get(i).get(ViewHolder.KEY_URL);
			if (tmp.equals(url)) {
				dataList.remove(i);
				this.notifyDataSetChanged();
			}
		}
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.current_item, null);
		}

		
		HashMap<Integer, String> itemData = dataList.get(position);
		String url = itemData.get(ViewHolder.KEY_URL);
		String fileName = itemData.get(ViewHolder.KEY_FILENAME);
		convertView.setTag(url);

		ViewHolder viewHolder = new ViewHolder(convertView);
		viewHolder.setData(itemData);

		viewHolder.continueButton.setOnClickListener(new DownloadBtnListener(
				url, fileName, viewHolder));
		viewHolder.pauseButton.setOnClickListener(new DownloadBtnListener(url,
				fileName, viewHolder));
		viewHolder.deleteButton.setOnClickListener(new DownloadBtnListener(url,
				fileName, viewHolder));
		return convertView;
	}

	private ArrayList<DownloadInfo> getData() {
		ArrayList<DownloadInfo> list = new ArrayList<DownloadInfo>();
		SharedPreferences sharedPreferences = mContext.getSharedPreferences("downData",
				Context.MODE_PRIVATE);
		// getString()第二个参数为缺省值，如果preference中不存在该key，将返回缺省值
		String jsonArrayStart = sharedPreferences.getString("json", "");
		if (!jsonArrayStart.equals(null)) {
			list = JsonUtil.changeJsonToArray(jsonArrayStart);
			return list;
		}
		return null;
	}
	public void removeDownList(ArrayList<DownloadInfo> list,String url) {
		String tmp;
		for (int i = 0; i < list.size(); i++) {
			tmp = list.get(i).getUrl();
			if (tmp.equals(url)) {
				list.remove(i);
			}
		}
		
		String jsonArrayEnd = JsonUtil.changeArrayDateToJson(list);

		SharedPreferences sharedPreferences = mContext.getSharedPreferences("downData",
				Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();// 获取编辑器
		editor.putString("json", jsonArrayEnd);
		editor.commit();// 提交修改
	}
	private class DownloadBtnListener implements View.OnClickListener {
		private String url;
		private String fileName;
		private ViewHolder mViewHolder;

		public DownloadBtnListener(String url, String fileName,
				ViewHolder viewHolder) {
			this.url = url;
			this.fileName = fileName;
			this.mViewHolder = viewHolder;
		}

		@Override
		public void onClick(View v) {
			final Intent downloadIntent = new Intent(
					"com.broov.player.masgb.service.IDownloadService");
			switch (v.getId()) {
			case R.id.btn_continue:
				downloadIntent.putExtra(MyIntents.TYPE,
						MyIntents.Types.CONTINUE);
				downloadIntent.putExtra(MyIntents.URL, url);
				downloadIntent.putExtra(MyIntents.FILE_NAME, fileName);
				mContext.startService(downloadIntent);
				mViewHolder.continueButton.setVisibility(View.GONE);
				mViewHolder.pauseButton.setVisibility(View.VISIBLE);
				break;
			case R.id.btn_pause:
				downloadIntent.putExtra(MyIntents.TYPE, MyIntents.Types.PAUSE);
				downloadIntent.putExtra(MyIntents.URL, url);
				downloadIntent.putExtra(MyIntents.FILE_NAME, fileName);
				mContext.startService(downloadIntent);
				mViewHolder.continueButton.setVisibility(View.VISIBLE);
				mViewHolder.pauseButton.setVisibility(View.GONE);
				break;
			case R.id.btn_delete:
				AlertDialog.Builder builder = new AlertDialog.Builder(
						DownloadingActivity.downloadingActivity);
				builder.setCancelable(false);
				builder.setTitle("提示：");
				builder.setMessage("确定删除正在下载的视频文件？");
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								File file = new File("/storage/sdcard0/干部在线/" + fileName
										+ ".download");
								if (file.delete()) {
									Toast.makeText(DownloadingActivity.downloadingActivity,
											"文件删除成功", Toast.LENGTH_SHORT).show();
									//dao.delete(url);
								}
								downloadIntent.putExtra(MyIntents.TYPE, MyIntents.Types.DELETE);
								downloadIntent.putExtra(MyIntents.URL, url);
								downloadIntent.putExtra(MyIntents.FILE_NAME, fileName);
								mContext.startService(downloadIntent);
								removeItem(url);
								list=getData();
								removeDownList(list, url);
							}
						});
				builder.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
							}
						});
				builder.show();
				
				break;
			}
		}
	}
}