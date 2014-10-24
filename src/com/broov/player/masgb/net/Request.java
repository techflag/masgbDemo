package com.broov.player.masgb.net;

import android.os.Handler;

public abstract class Request {

	public Request(Handler handler) {

	}

	public void sendRequest() {
		new Thread(new Runnable() {
			public void run() {
				httpConnect();
			}
		}).start();
	}

	protected abstract void httpConnect();
}
