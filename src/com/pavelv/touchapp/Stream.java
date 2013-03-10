package com.pavelv.touchapp;

import java.io.IOException;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;

public class Stream {

	private static MediaPlayer mp = new MediaPlayer();
	public static int mpState = 0;

	static void playSong(String songPath) {
		try {

			mp.reset();
			mp.setDataSource(songPath);
			mp.setVolume(1.0f, 1.0f);
//			mp.prepareAsync();
			mp.prepare();
//			mp.setOnPreparedListener(new OnPreparedListener() {
//				public void onPrepared(MediaPlayer arg0) {
//					mp.start();
//				}
//			});
			mp.start();
			mpState = 1;

			// Setup listener so next song starts automatically
			mp.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer arg0) {
//
				}

			});

		} catch (IOException e) {
			//
		}
	}

	public static boolean getPlayerStatus() {
		if (mp.isPlaying() == true) {
			mpState = 1;
			return true;
		} else {
			return false;
		}
	}

	public static boolean getPauseStatus() {
		if (mp.getDuration() != 0) {
			return true;
		} else {
			return false;
		}
	}

	public static void setOnPause() {
		mp.pause();
		mpState = 2;
	}

	public static void setResume() {
		mp.start();
		mpState = 1;
	}
}
