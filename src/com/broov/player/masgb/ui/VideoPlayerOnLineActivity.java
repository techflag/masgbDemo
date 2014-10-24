package com.broov.player.masgb.ui;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
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
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.broov.player.R;
import com.broov.player.masgb.entity.PowerKeyObserver;
import com.broov.player.masgb.entity.PowerKeyObserver.OnPowerKeyListener;
import com.broov.player.masgb.handler.ExitAppUtils;
import com.broov.player.masgb.net.request.CheckIsLoginRequest;
import com.broov.player.masgb.net.request.CloseCourseRequest;
import com.broov.player.masgb.net.request.OpenCourseRequest;
import com.broov.player.masgb.utils.HandleMessageState;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemSelectedListener;
//import android.widget.ArrayAdapter;

@SuppressLint("NewApi")
public class VideoPlayerOnLineActivity extends Activity implements
		SurfaceHolder.Callback, OnClickListener,IVideoPlayer {

	public final static String TAG = "DEBUG/VideoPlayerOnLineActivity";

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
	@SuppressWarnings("unused")
	private ProgressDialog progressDialog;
	private PowerManager.WakeLock wakeLock = null;
	private TelephonyManager mgr;
	/**
	 * 判断用户是否继续观看视频
	 */
	private Handler handlerIsContinue = new Handler();
	/**
	 * 隐藏进度条
	 */
	private Handler handlerIsShowBar = new Handler();
	/**
	 * 记录用户观看时间
	 */
	private Handler handlerTimer = new Handler();
	private CheckIsLoginRequest checkIsLoginRequest;
	private PowerKeyObserver mPowerKeyObserver;

	// private boolean handlerTimerRun=true;

	@SuppressLint("HandlerLeak")
	private Handler handler2 = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandleMessageState.OPEN_COURSE_SUCCESS:
				// Toast.makeText(getApplicationContext(), "打开课程成功，开始计时!",
				// Toast.LENGTH_SHORT).show();
				break;
			case HandleMessageState.CLOSE_COURSE_SUCCESS:
				// Toast.makeText(getApplicationContext(), "关闭课程成功，停止计时!",
				// Toast.LENGTH_SHORT).show();
				break;
			case HandleMessageState.CLOSE_COURSE_FAILURE:
				break;
			case HandleMessageState.OPEN_COURSE_FAILURE:
				break;
			case HandleMessageState.NO_NEED_LOGIN_AGAIN:
				break;
			case HandleMessageState.NEED_LOGIN_AGAIN:
				closeCourse();
				mLibVLC.pause();
				btnPlayPause.setImageResource(R.drawable.rlc_ic_play_selector);
				handlerIsContinue.removeCallbacks(runnableIsCountinue);
				handlerTimer.removeCallbacks(runnableTimer);
				AlertDialog.Builder builder = new AlertDialog.Builder(
						VideoPlayerOnLineActivity.this);
				builder.setCancelable(false);
				builder.setTitle("提示：");
				builder.setMessage("账号已在其他地方使用，需要重新登陆!");
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(
										VideoPlayerOnLineActivity.this,
										LoginActivity.class);
								startActivity(intent);
								ExitAppUtils.getInstance().exit();
								finish();
							}
						});
				builder.show();
				break;
			default:
				break;
			}
		};

	};
	
	
	private void loadMedia(String mLocation) {
        //mLocation = null;
       // String title = getResources().getString(R.string.title);
        boolean dontParse = false;
        boolean fromStart = false;
        String itemTitle = null;
        int itemPosition = -1; // Index in the media list as passed by AudioServer (used only for vout transition internally)
        long intentPosition = -1; // position passed in by intent (ms)

        if (getIntent().getAction() != null
                && getIntent().getAction().equals(Intent.ACTION_VIEW)) {
            /* Started from external application 'content' */
            if (getIntent().getData() != null
                    && getIntent().getData().getScheme() != null
                    && getIntent().getData().getScheme().equals("content")) {

                // Media or MMS URI
                if(getIntent().getData().getHost().equals("media")
                        || getIntent().getData().getHost().equals("mms")) {
                    try {
                        Cursor cursor = getContentResolver().query(getIntent().getData(),
                                new String[]{ MediaStore.Video.Media.DATA }, null, null, null);
                        if (cursor != null) {
                            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                            if (cursor.moveToFirst())
                                mLocation = LibVLC.PathToURI(cursor.getString(column_index));
                            cursor.close();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Couldn't read the file from media or MMS");
                        encounteredError();
                    }
                }

                // Mail-based apps - download the stream to a temporary file and play it
                else if(getIntent().getData().getHost().equals("com.fsck.k9.attachmentprovider")
                       || getIntent().getData().getHost().equals("gmail-ls")) {
                    try {
                        Cursor cursor = getContentResolver().query(getIntent().getData(),
                                new String[]{MediaStore.MediaColumns.DISPLAY_NAME}, null, null, null);
                        if (cursor != null) {
                            cursor.moveToFirst();
                            String filename = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME));
                            cursor.close();
                            Log.i(TAG, "Getting file " + filename + " from content:// URI");

                            InputStream is = getContentResolver().openInputStream(getIntent().getData());
                            OutputStream os = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/Download/" + filename);
                            byte[] buffer = new byte[1024];
                            int bytesRead = 0;
                            while((bytesRead = is.read(buffer)) >= 0) {
                                os.write(buffer, 0, bytesRead);
                            }
                            os.close();
                            is.close();
                            mLocation = LibVLC.PathToURI(Environment.getExternalStorageDirectory().getPath() + "/Download/" + filename);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Couldn't download file from mail URI");
                        encounteredError();
                    }
                }

                // other content-based URI (probably file pickers)
                else {
                    mLocation = getIntent().getData().getPath();
                }
            } /* External application */
            else if (getIntent().getDataString() != null) {
//                // Plain URI
//                mLocation = getIntent().getDataString();
//                // Remove VLC prefix if needed
//                if (mLocation.startsWith("vlc://")) {
//                    mLocation = mLocation.substring(6);
//                }
//                // Decode URI
//                if (!mLocation.contains("/")){
//                    try {
//                        mLocation = URLDecoder.decode(mLocation,"UTF-8");
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    }
//                }
            } else {
                Log.e(TAG, "Couldn't understand the intent");
                encounteredError();
            }

            // Try to get the position
            if(getIntent().getExtras() != null)
                intentPosition = getIntent().getExtras().getLong("position", -1);
        } /* ACTION_VIEW */
        /* Started from VideoListActivity */
//        else if(getIntent().getAction() != null
//                && getIntent().getAction().equals(PLAY_FROM_VIDEOGRID)
//                && getIntent().getExtras() != null) {
//            mLocation = getIntent().getExtras().getString("itemLocation");
//            itemTitle = getIntent().getExtras().getString("itemTitle");
//            dontParse = getIntent().getExtras().getBoolean("dontParse");
//            fromStart = getIntent().getExtras().getBoolean("fromStart");
//            itemPosition = getIntent().getExtras().getInt("itemPosition", -1);
//            
//            Toast.makeText(this, "PLAY_FROM_VIDEOGRID", Toast.LENGTH_LONG).show();
//          //mLocation = "http://f.youku.com/player/getFlvPath/sid/00_00/st/flv/fileid/03000201005372BFB56E3D08EB51973DAE3BB8-18AE-8DC7-435B-B8EA5FEA673F?K=57e5fabce070302b282989ee";
//            
//            mLocation = "http://btcworld.duapp.com//keai.flv";
//            mLibVLC.playMRL(mLocation);
//        }
//        mLocation = getIntent().getExtras().getString("itemLocation");
//        itemTitle = getIntent().getExtras().getString("itemTitle");
//        dontParse = getIntent().getExtras().getBoolean("dontParse");
//        fromStart = getIntent().getExtras().getBoolean("fromStart");
//        itemPosition = getIntent().getExtras().getInt("itemPosition", -1);
        
        Toast.makeText(this, "PLAY_FROM_VIDEOGRID", Toast.LENGTH_LONG).show();
      //mLocation = "http://f.youku.com/player/getFlvPath/sid/00_00/st/flv/fileid/03000201005372BFB56E3D08EB51973DAE3BB8-18AE-8DC7-435B-B8EA5FEA673F?K=57e5fabce070302b282989ee";
        
        mLocation = "http://btcworld.duapp.com//keai.flv";
        mLibVLC.playMRL(mLocation);
        
    }
	
	private void encounteredError() {
        /* Encountered Error, exit player with a message */
        AlertDialog dialog = new AlertDialog.Builder(VideoPlayerOnLineActivity.this)
        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        })
        .setTitle("cuow")
        .setMessage("cuow xiangxi")
        .create();
        dialog.show();
    }
	
	
	private Runnable runableIsShowBar = new Runnable() {
		@Override
		public void run() {
			btnPlayPause.setVisibility(View.INVISIBLE);
			mSeekBar.setVisibility(View.INVISIBLE);
		}
	};
	private Runnable runnableIsCountinue = new Runnable() {
		@Override
		public void run() {
			closeCourse();
			handlerIsContinue.removeCallbacks(runnableIsCountinue);
			handlerTimer.removeCallbacks(runnableTimer);
			mLibVLC.pause();
			btnPlayPause.setImageResource(R.drawable.rlc_ic_play_selector);
			if (!isFinishing()) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						VideoPlayerOnLineActivity.this);
				builder.setCancelable(false);
				builder.setTitle("提示：");
				builder.setMessage("是否要继续观看？");
				builder.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// handlerIsContinue.removeCallbacks(runnableIsCountinue);
								finish();
							}
						});
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								openCourse();
								mLibVLC.play();
								btnPlayPause
										.setImageResource(R.drawable.rlc_ic_pause_selector);
								handlerIsContinue.postDelayed(
										runnableIsCountinue, 1000 * 60 * 15);
								handlerTimer.postDelayed(runnableTimer,
										1000 * 60 * 2);
							}
						});
				builder.show();
			}
		}
	};

	private Runnable runnableTimer = new Runnable() {
		@Override
		public void run() {
			closeCourse();
			openCourse();
			handlerTimer.postDelayed(runnableTimer, 1000 * 60 * 2);
		}
	};

	/**
	 * 关闭课程请求
	 */
	public void closeCourse() {
		CloseCourseRequest closeCourseRequest = new CloseCourseRequest(
				handler2, VideoPlayerOnLineActivity.this);
		closeCourseRequest.setWareId(bundle.getString("wareid"));
		closeCourseRequest.sendRequest();
	}

	/**
	 * 打开课程请求
	 */
	public void openCourse() {
		checkIsLoginRequest = new CheckIsLoginRequest(handler2,
				VideoPlayerOnLineActivity.this);
		checkIsLoginRequest.sendRequest();
		OpenCourseRequest openCourseRequest = new OpenCourseRequest(handler2,
				VideoPlayerOnLineActivity.this);
		openCourseRequest.setCourseId(bundle.getString("wareid"));
		openCourseRequest.sendRequest();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();

		switch (action) {
		case (MotionEvent.ACTION_DOWN):
			// Toast.makeText(getApplicationContext(), "我点击了屏幕", 1).show();
			btnPlayPause.setVisibility(View.VISIBLE);
			mSeekBar.setVisibility(View.VISIBLE);
			handlerIsShowBar.postDelayed(runableIsShowBar, 1000 * 10);
			return true;
		case (MotionEvent.ACTION_MOVE):
			return true;
		case (MotionEvent.ACTION_UP):
			return true;
		case (MotionEvent.ACTION_CANCEL):
			return true;
		case (MotionEvent.ACTION_OUTSIDE):
			return true;
		default:
			return super.onTouchEvent(event);
		}
	}

	/**
	 * 拦截返回按键
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			mTextTitle.setText("");
			closeCourse();
			if (mLibVLC.isPlaying()) {
				mLibVLC.pause();
			}
			btnPlayPause.setImageResource(R.drawable.rlc_ic_play_selector);
			handlerTimer.removeCallbacks(runnableTimer);
			handlerIsContinue.removeCallbacks(runnableIsCountinue);
			if (!isFinishing()) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						VideoPlayerOnLineActivity.this);
				builder.setCancelable(false);
				builder.setTitle("提示：");
				builder.setMessage("是否要退出？");
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								mgr.listen(phoneStateListener,
										PhoneStateListener.LISTEN_NONE);
								finish();
							}
						});
				builder.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								if (mLibVLC.getLength() == 0) {
									mTextTitle.setText("视频正在缓冲,请稍候!");
								} else {
									mLibVLC.play();
									btnPlayPause
											.setImageResource(R.drawable.rlc_ic_pause_selector);
									openCourse();
									handlerTimer.postDelayed(runnableTimer,
											1000 * 60 * 2);
									handlerIsContinue
											.postDelayed(runnableIsCountinue,
													1000 * 60 * 15);
								}
							}
						});
				builder.show();
			}
			// handlerTimerRun = false;

		} else if (KeyEvent.KEYCODE_HOME == keyCode) {

		}
		return false;
	}

	PhoneStateListener phoneStateListener = new PhoneStateListener() {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			if (state == TelephonyManager.CALL_STATE_RINGING) {
				// Incoming call: Pause music
				// Pause the video, only if video is playing
				if (mLibVLC.isPlaying()) {
					mLibVLC.pause();
					closeCourse();
					handlerTimer.removeCallbacks(runnableTimer);
					btnPlayPause
							.setImageResource(R.drawable.rlc_ic_play_selector);
				}

				// seekBarUpdater = new Updater();
				// mHandler.postDelayed(seekBarUpdater, 500);
			} else if (state == TelephonyManager.CALL_STATE_IDLE) {
				// Not in call: Play music
				// do not resume, if already paused by User
				if (mLibVLC.isPlaying()) {
					mLibVLC.pause();
					closeCourse();
					handlerTimer.removeCallbacks(runnableTimer);
					btnPlayPause
							.setImageResource(R.drawable.rlc_ic_play_selector);
				}
				// seekBarUpdater.stopIt();
			} else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
				// A call is dialing, active or on hold
				if (mLibVLC.isPlaying()) {
					mLibVLC.pause();
					closeCourse();
					handlerTimer.removeCallbacks(runnableTimer);
					btnPlayPause
							.setImageResource(R.drawable.rlc_ic_play_selector);
				}
				// seekBarUpdater = new Updater();
				// mHandler.postDelayed(seekBarUpdater, 500);
			}
			super.onCallStateChanged(state, incomingNumber);
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitAppUtils.getInstance().addActivity(this);
		setContentView(R.layout.vlc_video_player);
		mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		if (mgr != null) {
			mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

		}
		setupView();
		bundle = getIntent().getExtras();
		if (LibVlcUtil.isICSOrLater())
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
			//LibVLC.useIOMX(getApplicationContext());
			
			mLibVLC =  Util.getLibVlcInstance();
			if (mLibVLC != null) {
				String path = bundle.getString("uri");
				//mLibVLC.readMedia(path, false);
				//mLibVLC.playMRL(path);
				loadMedia(path);
				handler.sendEmptyMessageDelayed(0, 1000);
			}
		} catch (LibVlcException e) {
			e.printStackTrace();
		}
		initPowerKey();
	}

	@Override
	protected void onResume() {
		super.onResume();
		initPower();
	}

	private void initPower() {
		// Wake lock code
		try {
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			// wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
			// Globals.ApplicationName);
			wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "干部在线");
			wakeLock.acquire();
		} catch (Exception e) {
			System.out.println("Inside wake lock exception" + e.toString());
		}
	}

	private void initPowerKey() {
		mPowerKeyObserver = new PowerKeyObserver(this);
		mPowerKeyObserver.setHomeKeyListener(new OnPowerKeyListener() {
			@Override
			public void onPowerKeyPressed() {
				if (mLibVLC.isPlaying()) {
					mLibVLC.pause();
					btnPlayPause
							.setImageResource(R.drawable.rlc_ic_play_selector);
					closeCourse();
					handlerTimer.removeCallbacks(runnableTimer);
					handlerIsContinue.removeCallbacks(runnableIsCountinue);
				}
			}
		});
		mPowerKeyObserver.startListen();
	}

	@Override
	protected void onStop() {
		if (wakeLock != null) {
			wakeLock.release();
			wakeLock = null;
		}

		super.onStop();
	}

	@Override
	public void onClick(View v) {
		if (mLibVLC.isPlaying()) {
			mLibVLC.pause();
			btnPlayPause.setImageResource(R.drawable.rlc_ic_play_selector);
			closeCourse();
			handlerTimer.removeCallbacks(runnableTimer);
			// handlerIsContinue.removeCallbacks(runnableIsCountinue);
		} else {
			mLibVLC.play();
			btnPlayPause.setImageResource(R.drawable.rlc_ic_pause_selector);
			openCourse();
			// handlerIsContinue.postDelayed(runnableIsCountinue, 1000 * 60 *
			// 15);
			handlerTimer.postDelayed(runnableTimer, 1000 * 60 * 2);
		}
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
	@SuppressWarnings("unused")
	private FrameLayout mLayout;
	private TextView mTextTitle;
	private TextView mTextTime;
	private TextView mTextLength;

	private ImageView btnPlayPause;
	private ImageView btnSize;
	private SeekBar mSeekBar;
	@SuppressWarnings("unused")
	private TextView mTextShowInfo;

	// private Spinner mAudioTrackSpinner;

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

		mTextTitle.setText("视频正在缓冲,请稍候!");
	}

	// 是否是第一次观看
	private boolean isFirstPlay = true;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int time = (int) mLibVLC.getTime();
			if (time > 0) {
				mTextTitle.setText("");
				if (isFirstPlay) {
					openCourse();
					btnPlayPause.setImageResource(R.drawable.rlc_ic_pause_selector);
					handlerTimer.postDelayed(runnableTimer, 1000 * 60 * 2);
					handlerIsContinue.postDelayed(runnableIsCountinue,
							1000 * 60 * 15);
					handlerIsShowBar.postDelayed(runableIsShowBar, 1000 * 10);
					isFirstPlay = false;
				}
			}
			int length = (int) mLibVLC.getLength();
			mSeekBar.setMax(length);
			mSeekBar.setProgress(time);
			showVideoTime(time, length);
			handler.sendEmptyMessageDelayed(0, 1000);
		}
	};

	private void showVideoTime(int t, int l) {
		mTextTime.setText(millisToString(t));
		mTextLength.setText(millisToString(l));
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

	private final Handler mHandler = new VideoPlayerHandler(this);

	private static class VideoPlayerHandler extends
			WeakHandler<VideoPlayerOnLineActivity> {
		public VideoPlayerHandler(VideoPlayerOnLineActivity owner) {
			super(owner);
		}

		@Override
		public void handleMessage(Message msg) {
			VideoPlayerOnLineActivity activity = getOwner();
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

		switch (mCurrentSize) {
		case SURFACE_BEST_FIT:
			// mTextShowInfo.setText(R.string.video_player_best_fit);
			if (dar < ar)
				dh = (int) (dw / ar);
			else
				dw = (int) (dh * ar);
			break;
		case SURFACE_FIT_HORIZONTAL:
			// mTextShowInfo.setText(R.string.video_player_fit_horizontal);
			dh = (int) (dw / ar);
			break;
		case SURFACE_FIT_VERTICAL:
			// mTextShowInfo.setText(R.string.video_player_fit_vertical);
			dw = (int) (dh * ar);
			break;
		case SURFACE_FILL:
			break;
		case SURFACE_16_9:
			// mTextShowInfo.setText(R.string.video_player_16x9);
			ar = 16.0 / 9.0;
			if (dar < ar)
				dh = (int) (dw / ar);
			else
				dw = (int) (dh * ar);
			break;
		case SURFACE_4_3:
			// mTextShowInfo.setText(R.string.video_player_4x3);
			ar = 4.0 / 3.0;
			if (dar < ar)
				dh = (int) (dw / ar);
			else
				dw = (int) (dh * ar);
			break;
		case SURFACE_ORIGINAL:
			// mTextShowInfo.setText(R.string.video_player_original);
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

	@SuppressWarnings("unused")
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
			mLibVLC.attachSurface(holder.getSurface(),
					VideoPlayerOnLineActivity.this);
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			mLibVLC.detachSurface();
		}
	};

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		mLibVLC.attachSurface(holder.getSurface(),
				VideoPlayerOnLineActivity.this);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mTextTitle.setText("视频正在缓冲,请稍候!");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mLibVLC.detachSurface();
	}

	private final Handler eventHandler = new VideoPlayerEventHandler(this);

	private Bundle bundle;

	private static class VideoPlayerEventHandler extends
			WeakHandler<VideoPlayerOnLineActivity> {
		public VideoPlayerEventHandler(VideoPlayerOnLineActivity owner) {
			super(owner);
		}

		@Override
		public void handleMessage(Message msg) {
			VideoPlayerOnLineActivity activity = getOwner();
			if (activity == null)
				return;

			switch (msg.getData().getInt("event")) {
			case EventHandler.MediaPlayerPlaying:
				Log.i(TAG, "MediaPlayerPlaying");
				break;
			case EventHandler.MediaPlayerPaused:
				Log.i(TAG, "MediaPlayerPaused");
				break;
			case EventHandler.MediaPlayerStopped:
				Log.i(TAG, "MediaPlayerStopped");
				break;
			case EventHandler.MediaPlayerEndReached:
				Log.i(TAG, "MediaPlayerEndReached");
				activity.finish();
				break;
			case EventHandler.MediaPlayerVout:
				activity.finish();
				break;
			default:
				Log.e(TAG, "Event not handled");
				break;
			}
			// activity.updateOverlayPausePlay();
		}
	}

	@Override
	protected void onDestroy() {
		mPowerKeyObserver.stopListen();
		if (mLibVLC != null) {
			mLibVLC.stop();
			// mLibVLC.destroy();
			// handler.removeMessages(0);
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
