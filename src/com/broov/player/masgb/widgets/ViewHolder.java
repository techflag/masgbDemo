package com.broov.player.masgb.widgets;

import java.util.HashMap;

import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.broov.player.R;
import com.broov.player.masgb.service.DownloadTask;
import com.broov.player.masgb.ui.BaseActivity;

public class ViewHolder {

	public static final int KEY_URL = 0;
	public static final int KEY_SPEED = 1;
	public static final int KEY_PROGRESS = 2;
	public static final int KEY_IS_PAUSED = 3;
	public static final int KEY_FILENAME = 4;

	public TextView titleText;
	public ProgressBar progressBar;
	public TextView speedText;
	public Button pauseButton;
	public Button deleteButton;
	public Button continueButton;
	public LinearLayout layoutDocDownLoadItem;

	private boolean hasInited = false;

	public ViewHolder(View parentView) {
		if (parentView != null) {
			layoutDocDownLoadItem = (LinearLayout) parentView
					.findViewById(R.id.layoutDocDownLoadItem);
			titleText = (TextView) parentView.findViewById(R.id.title);
			speedText = (TextView) parentView.findViewById(R.id.speed);
			progressBar = (ProgressBar) parentView
					.findViewById(R.id.progress_bar);
			pauseButton = (Button) parentView.findViewById(R.id.btn_pause);
			deleteButton = (Button) parentView.findViewById(R.id.btn_delete);
			continueButton = (Button) parentView
					.findViewById(R.id.btn_continue);
			AbsListView.LayoutParams layoutCourseParams = new AbsListView.LayoutParams(
					LayoutParams.FILL_PARENT,
					(int) (120 * BaseActivity.heightScale));
			layoutDocDownLoadItem.setLayoutParams(layoutCourseParams);
			hasInited = true;
		}
	}

	public static HashMap<Integer, String> getItemDataMap(String url,
			String fileName, String speed, String progress, String isPaused) {
		HashMap<Integer, String> item = new HashMap<Integer, String>();
		item.put(KEY_URL, url);
		item.put(KEY_FILENAME, fileName);
		item.put(KEY_SPEED, speed);
		item.put(KEY_PROGRESS, progress);
		item.put(KEY_IS_PAUSED, isPaused);
		return item;
	}

	public void setData(HashMap<Integer, String> item) {
		if (hasInited) {
			titleText.setText(item.get(KEY_FILENAME));
			speedText.setText(item.get(KEY_SPEED));
			String progress = item.get(KEY_PROGRESS);
			if (TextUtils.isEmpty(progress)) {
				progressBar.setProgress(0);
			} else {
				progressBar.setProgress(Integer.parseInt(progress));
			}
			if (Boolean.parseBoolean(item.get(KEY_IS_PAUSED))) {
				onPause();
			}
		}
	}

	public void onPause() {
		if (hasInited) {
			pauseButton.setVisibility(View.GONE);
			continueButton.setVisibility(View.VISIBLE);
		}
	}

	public void setData(String url, String fileName, String speed,
			String progress) {
		setData(url, fileName, speed, progress, false + "");
	}

	public void setData(String url, String fileName, String speed,
			String progress, String isPaused) {
		if (hasInited) {
			HashMap<Integer, String> item = getItemDataMap(url, fileName,
					speed, progress, isPaused);

			titleText.setText(fileName);
			speedText.setText(speed);
			if (TextUtils.isEmpty(progress)) {
				progressBar.setProgress(0);
			} else {
				progressBar
						.setProgress(Integer.parseInt(item.get(KEY_PROGRESS)));
			}

		}
	}

	public void bindTask(DownloadTask task) {
		if (hasInited) {
			long completeSize = task.getDownloadSize();
			long fileSize = task.getTotalSize();
			titleText.setText(task.getFileName());
			// speedText.setText(task.getDownloadSpeed() + "kbps | "
			// + task.getDownloadSize() + " / " + task.getTotalSize());
			float num = (float) completeSize / (float) fileSize;
			int result = (int) (num * 100);
			speedText.setText(result + "%");
			progressBar.setProgress((int) task.getDownloadPercent());
			if (task.isInterrupt()) {
				onPause();
			}
		}
	}

}
