package com.broov.player.masgb.ui;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import org.videolan.libvlc.EventHandler;
import org.videolan.libvlc.IVideoPlayer;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.LibVlcException;
import org.videolan.libvlc.LibVlcUtil;
import org.videolan.vlc.Util;
import org.videolan.vlc.WeakHandler;

import com.broov.player.R;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemSelectedListener;
//import android.widget.ArrayAdapter;

public class VideoPlayer extends Activity implements
		SurfaceHolder.Callback, OnClickListener,IVideoPlayer {

	public final static String TAG = "DEBUG/VideoPlayerActivity";

	private SurfaceHolder surfaceHolder = null;
	private LibVLC mLibVLC = null;

	private int mVideoHeight;
	private int mVideoWidth;
	private int mSarDen;
	private int mSarNum;
	private int mUiVisibility = -1;
	private static final int SURFACE_SIZE = 3;

	private static final int SURFACE_BEST_FIT = 0;
	private static final int SURFACE_FIT_HORIZONTAL = 1;
	private static final int SURFACE_FIT_VERTICAL = 2;
	private static final int SURFACE_FILL = 3;
	private static final int SURFACE_16_9 = 4;
	private static final int SURFACE_4_3 = 5;
	private static final int SURFACE_ORIGINAL = 6;
	private int mCurrentSize = SURFACE_BEST_FIT;
	
	private ProgressDialog progressDialog;
	String pathUri="http://218.22.168.163/asfroot/jrsf/02.wmv";

	//private String[] mAudioTracks;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vlc_video_player);
		setupView();
		
		if(LibVlcUtil.isICSOrLater())
		getWindow()
				.getDecorView()
				.findViewById(android.R.id.content)
				.setOnSystemUiVisibilityChangeListener(
						new OnSystemUiVisibilityChangeListener() {

							@Override
							public void onSystemUiVisibilityChange(
									int visibility) {
								if (visibility == mUiVisibility)
									return;
								setSurfaceSize(mVideoWidth, mVideoHeight,
										mSarNum, mSarDen);
								if (visibility == View.SYSTEM_UI_FLAG_VISIBLE) {
									Log.d(TAG, "onSystemUiVisibilityChange");
								}
								mUiVisibility = visibility;
							}
						});

		try {
			mLibVLC = Util.getLibVlcInstance();
			if (mLibVLC != null) {
				mLibVLC.setRate(2);
				String path = getIntent().getStringExtra("path");

				// String pathUri = LibVLC.getInstance().nativeToURI(path);
				// String pathUri="rtsp://122.192.35.80:554/live/tv14";
				 
				//String pathUri = "rtsp://218.204.223.237:554/live/1/66251FC11353191F/e7ooqwcfbqjoo80j.sdp";
				//showProgressDialog();
				//String pathUri = mLibVLC.nativeToURI("/storage/sdcard0/干部在线/可爱@@@769.flv");
				//mLibVLC.readMedia(pathUri, false);	//true似乎只能播放音频
				mLibVLC.playMRL(pathUri);
				handler.sendEmptyMessageDelayed(0, 1000);
			}
		} catch (LibVlcException e) {
			e.printStackTrace();
		}

	}
	
	private void showProgressDialog(){
		if(progressDialog==null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setCancelable(true);
			progressDialog.setMessage("正在加载数据...");
		}
		if (!progressDialog.isShowing()) {
			progressDialog.show();
		}
		
	}
	
	
	/**
	 * 消失等待加载对话框
	 */
	public void dismissProgressDialog() {
		if (progressDialog != null &&

		progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

	@Override
	public void onClick(View v) {
//		if (v.getId() == btnBackward.getId()) {
//
//		} else if (v.getId() == btnPlayPause.getId()) {
			if (mLibVLC.isPlaying()) {
				mLibVLC.pause();
				btnPlayPause.setImageResource(R.drawable.rlc_ic_play_selector);
			} else {
				mLibVLC.play();
				btnPlayPause.setImageResource(R.drawable.rlc_ic_pause_selector);
			}
		//} 
//		else if (v.getId() == btnForward.getId()) {
//		} 
		if (v.getId() == btnSize.getId()) {
			if (mCurrentSize < SURFACE_ORIGINAL) {
				mCurrentSize++;
			} else {
				mCurrentSize = 0;
			}
			changeSurfaceSize();
		}

	}

	private SurfaceView surfaceView = null;
	private FrameLayout mLayout;
	private TextView mTextTitle;
	private TextView mTextTime;
	private TextView mTextLength;
	
	private ImageView btnPlayPause;
	private ImageView btnSize;
	private SeekBar mSeekBar;
	private TextView mTextShowInfo;
	//private Spinner mAudioTrackSpinner;

	private void setupView() {
		surfaceView = (SurfaceView) findViewById(R.id.main_surface);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.setFormat(PixelFormat.RGBX_8888);
		surfaceHolder.addCallback(this);
		mLayout = (FrameLayout) findViewById(R.id.video_player_overlay);
		mTextTitle = (TextView) findViewById(R.id.video_player_title);

		btnPlayPause = (ImageView) findViewById(R.id.video_player_playpause);
		btnSize = (ImageView) findViewById(R.id.video_player_size);
		mTextTime = (TextView) findViewById(R.id.video_player_time);
		mTextLength = (TextView) findViewById(R.id.video_player_length);
		mSeekBar = (SeekBar) findViewById(R.id.video_player_seekbar);
		mTextShowInfo = (TextView) findViewById(R.id.video_player_showinfo);

		mSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
	
		btnPlayPause.setOnClickListener(this);
		btnSize.setOnClickListener(this);

		mTextTitle.setText(getIntent().getStringExtra("name"));
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			
			super.handleMessage(msg);
//			if (mAudioTracks == null) {
//				mAudioTracks = mLibVLC.getAudioTrackDescription();
//				if (mAudioTracks != null && mAudioTracks.length > 1) {
//					for (int i = 0; i < mAudioTracks.length; i++) {
//						Log.d(TAG, mAudioTracks[i]);
//					}
//					ArrayAdapter<String> adapter = new ArrayAdapter<String>(
//							getApplicationContext(),
//							android.R.layout.simple_spinner_item, mAudioTracks);
//					mAudioTrackSpinner.setAdapter(adapter);
//					int curr = mLibVLC.getAudioTrack() - 1;
//					mAudioTrackSpinner.setSelection(curr);
//					mAudioTrackSpinner
//							.setOnItemSelectedListener(new OnItemSelectedListener() {
//								@Override
//								public void onItemSelected(
//										AdapterView<?> parent, View view,
//										int position, long id) {
//									mLibVLC.setAudioTrack(position + 1);
//								}
//
//								@Override
//								public void onNothingSelected(
//										AdapterView<?> parent) {
//								}
//							});
//					mAudioTrackSpinner.setEnabled(true);
//				}
//			}
			
			int what = msg.what;
			switch(what){
				case 0:
				int time = (int) mLibVLC.getTime();
				if(time>0){
					dismissProgressDialog();
				}
				int length = (int) mLibVLC.getLength();
				// Log.d(TAG, "handleMessage length: " + length + "; time: " +
				// time);
				mSeekBar.setMax(length);
				mSeekBar.setProgress(time);
				showVideoTime(time, length);
				handler.sendEmptyMessageDelayed(0, 1000);
				System.out.println("msg--what:"+msg.what);
				
				
				
				break;
			}
		}
	};
	
	private int playtime;//记录了上次播放的时间

	private void showVideoTime(int t, int l) {
		mTextTime.setText(millisToString(t));
		mTextLength.setText(millisToString(l));
		//System.out.println("mLibVLC:"+mLibVLC.getRate()+"---"+mLibVLC.getLength()+"---"+mLibVLC.getPosition()+"---"+mLibVLC.isSeekable()+"---"+mLibVLC.hasMediaPlayer());
		System.out.println("mLibVLC:"+"---"+"---"+mLibVLC.getAout());
		
		//mLibVLC.hasVideoTrack(pathUri)+
		
	}

	private OnSeekBarChangeListener seekBarChangeListener = new OnSeekBarChangeListener() {
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if (fromUser) {
				if (mLibVLC != null) {
					if (!mLibVLC.isPlaying()) {
						mLibVLC.play();
					}
					mLibVLC.setTime(progress);
				}
			}
		}
	};

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		setSurfaceSize(mVideoWidth, mVideoHeight, mSarNum, mSarDen);
		super.onConfigurationChanged(newConfig);
	}
	 public void setSurfaceSize(int width, int height, int sar_num, int sar_den) {
	        // store video size
	        mVideoHeight = height;
	        mVideoWidth = width;
	        mSarNum = sar_num;
	        mSarDen = sar_den;
	        Message msg = mHandler.obtainMessage(SURFACE_SIZE);
	        mHandler.sendMessage(msg);
	    }
//	public void setSurfaceSize(int width, int height) {
//		// store video size
//		mVideoHeight = height;
//		mVideoWidth = width;
//		Message msg = mHandler.obtainMessage(SURFACE_SIZE);
//		mHandler.sendMessage(msg);
//	}

	private final Handler mHandler = new VideoPlayerHandler(this);

	private static class VideoPlayerHandler extends
			WeakHandler<VideoPlayer> {
		public VideoPlayerHandler(VideoPlayer owner) {
			super(owner);
		}

		@Override
		public void handleMessage(Message msg) {
			VideoPlayer activity = getOwner();
			if (activity == null) // WeakReference could be GC'ed early
				return;

			switch (msg.what) {
			case SURFACE_SIZE:
				activity.changeSurfaceSize();
				break;
			}
		}
	};

	
	private void changeSurfaceSize() {
		// get screen size
		int dw = getWindow().getDecorView().getWidth();
		int dh = getWindow().getDecorView().getHeight();

		// getWindow().getDecorView() doesn't always take orientation into
		// account, we have to correct the values
		boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
		if (dw > dh && isPortrait || dw < dh && !isPortrait) {
			int d = dw;
			dw = dh;
			dh = d;
		}
		if (dw * dh == 0)
			return;
		// compute the aspect ratio
				double ar, vw;
				double density = (double) mSarNum / (double) mSarDen;
				if (density == 1.0) {
					/* No indication about the density, assuming 1:1 */
					vw = mVideoWidth;
					ar = (double) mVideoWidth / (double) mVideoHeight;
				} else {
					/* Use the specified aspect ratio */
					vw = mVideoWidth * density;
					ar = vw / mVideoHeight;
				}

				// compute the display aspect ratio
				double dar = (double) dw / (double) dh;

//		// calculate aspect ratio
//		double ar = (double) mVideoWidth / (double) mVideoHeight;
//		// calculate display aspect ratio
//		double dar = (double) dw / (double) dh;

		switch (mCurrentSize) {
		case SURFACE_BEST_FIT:
			mTextShowInfo.setText(R.string.video_player_best_fit);
			if (dar < ar)
				dh = (int) (dw / ar);
			else
				dw = (int) (dh * ar);
			break;
		case SURFACE_FIT_HORIZONTAL:
			mTextShowInfo.setText(R.string.video_player_fit_horizontal);
			dh = (int) (dw / ar);
			break;
		case SURFACE_FIT_VERTICAL:
			mTextShowInfo.setText(R.string.video_player_fit_vertical);
			dw = (int) (dh * ar);
			break;
		case SURFACE_FILL:
			break;
		case SURFACE_16_9:
			mTextShowInfo.setText(R.string.video_player_16x9);
			ar = 16.0 / 9.0;
			if (dar < ar)
				dh = (int) (dw / ar);
			else
				dw = (int) (dh * ar);
			break;
		case SURFACE_4_3:
			mTextShowInfo.setText(R.string.video_player_4x3);
			ar = 4.0 / 3.0;
			if (dar < ar)
				dh = (int) (dw / ar);
			else
				dw = (int) (dh * ar);
			break;
		case SURFACE_ORIGINAL:
			mTextShowInfo.setText(R.string.video_player_original);
			dh = mVideoHeight;
			dw = mVideoWidth;
			break;
		}

		surfaceHolder.setFixedSize(mVideoWidth, mVideoHeight);
		LayoutParams lp = surfaceView.getLayoutParams();
		lp.width = dw;
		lp.height = dh;
		surfaceView.setLayoutParams(lp);
		surfaceView.invalidate();
	}

	
	private final SurfaceHolder.Callback mSurfaceCallback = new Callback() {
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			if (format == PixelFormat.RGBX_8888)
				Log.d(TAG, "Pixel format is RGBX_8888");
			else if (format == ImageFormat.YV12)
				Log.d(TAG, "Pixel format is YV12");
			else
				Log.d(TAG, "Pixel format is other/unknown");
			//mLibVLC.attachSurface(holder.getSurface(),VideoPlayer.this, width, height);
			mLibVLC.attachSurface(holder.getSurface(),VideoPlayer.this);
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			System.out.println("11111111111111111");
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			mLibVLC.detachSurface();
		}
	};
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		mLibVLC.attachSurface(holder.getSurface(), VideoPlayer.this);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		showProgressDialog();
		System.out.println("2222222222");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mLibVLC.detachSurface();
		
	}

	private final Handler eventHandler = new VideoPlayerEventHandler(this);

	private static class VideoPlayerEventHandler extends
			WeakHandler<VideoPlayer> {
		public VideoPlayerEventHandler(VideoPlayer owner) {
			super(owner);
		}

		@Override
		public void handleMessage(Message msg) {
			VideoPlayer activity = getOwner();
			if (activity == null)
				return;

			switch (msg.getData().getInt("event")) {
			case EventHandler.MediaPlayerPlaying:
				System.out.println("---------MediaPlayerPlaying---------");
				Log.i(TAG, "MediaPlayerPlaying");
				break;
			case EventHandler.MediaPlayerPaused:
				System.out.println("---------Event not handled---------");
				Log.i(TAG, "MediaPlayerPaused");
				break;
			case EventHandler.MediaPlayerStopped:
				System.out.println("---------MediaPlayerPaused---------");
				Log.i(TAG, "MediaPlayerStopped");
				break;
			case EventHandler.MediaPlayerEndReached:
				System.out.println("---------MediaPlayerEndReached---------");
				Log.i(TAG, "MediaPlayerEndReached");
				activity.finish();
				break;
			case EventHandler.MediaPlayerVout:
				System.out.println("---------MediaPlayerVout---------");
				activity.finish();
				break;
			default:
				System.out.println("---------Event not handled---------");
				Log.e(TAG, "Event not handled");
				break;
			}
			// activity.updateOverlayPausePlay();
		}
	}

	@Override
	protected void onDestroy() {
		handler.removeMessages(0);
		System.out.println("onDestroy----start");
		if (mLibVLC != null) {
			System.out.println("onDestroy----end");
			mLibVLC.stop();
			mLibVLC.destroy();
			mLibVLC.closeAout();
			handler.sendEmptyMessage(-2);
		}

		EventHandler em = EventHandler.getInstance();
		em.removeHandler(eventHandler);
		super.onDestroy();
	};

	/**
	 * Convert time to a string
	 * 
	 * @param millis
	 *            e.g.time/length from file
	 * @return formated string (hh:)mm:ss
	 */
	public static String millisToString(long millis) {
		boolean negative = millis < 0;
		millis = java.lang.Math.abs(millis);

		millis /= 1000;
		int sec = (int) (millis % 60);
		millis /= 60;
		int min = (int) (millis % 60);
		millis /= 60;
		int hours = (int) millis;

		String time;
		DecimalFormat format = (DecimalFormat) NumberFormat
				.getInstance(Locale.US);
		format.applyPattern("00");
		if (millis > 0) {
			time = (negative ? "-" : "") + hours + ":" + format.format(min)
					+ ":" + format.format(sec);
		} else {
			time = (negative ? "-" : "") + min + ":" + format.format(sec);
		}
		return time;
	}

	@Override
	public void setSurfaceSize(int width, int height, int visible_width,
			int visible_height, int sar_num, int sar_den) {
		// TODO Auto-generated method stub
		
	}

	

}
