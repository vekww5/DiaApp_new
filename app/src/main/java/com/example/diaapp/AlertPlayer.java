package com.example.diaapp;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Looper;
import android.provider.SyncStateContract;
import android.os.Handler;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.IOException;


public class AlertPlayer  {

    final static int ALERT_LOW = 0;
    final static int ALERT_HIGH = 1;
    private volatile MediaPlayer mediaPlayer = null;


    private final static String TAG = AlertPlayer.class.getSimpleName();


    public synchronized void startAlert(Context ctx, int alertType) {
        int rawResourceId = 0;

        mediaPlayer = new MediaPlayerCreaterHelper().createMediaPlayer(ctx);

        if (mediaPlayer == null) {
            Log.wtf(TAG, "createMediaPlayer failed !!");
            return;
        }

        if (alertType == ALERT_HIGH){
            rawResourceId = R.raw.high_notification;
        }else {
            rawResourceId = R.raw.low_notification;
        }

        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setLooping(false);

            AssetFileDescriptor afd = ctx.getResources().openRawResourceFd(rawResourceId);
            if (afd == null) return;
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();

            mediaPlayer.setOnPreparedListener(mp -> {
                mediaPlayer.start();
            });
            mediaPlayer.prepareAsync();
        } catch (NullPointerException e) {
            Log.wtf(TAG, "Playfile: Concurrency related null pointer exception: " + e.toString());
        } catch (IllegalStateException e) {
            Log.wtf(TAG, "Playfile: Concurrency related illegal state exception: " + e.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public synchronized void stopAlert() {
    }

}


class MediaPlayerCreaterHelper {

    private final static String TAG = AlertPlayer.class.getSimpleName();
    private final Object creationThreadLock = new Object();
    private volatile boolean mplayerCreated_ = false;
    private volatile MediaPlayer mediaPlayer_ = null;

    synchronized MediaPlayer createMediaPlayer(Context ctx) {
        if (isUiThread()) {
            return new MediaPlayer();
        }

        mplayerCreated_ = false;
        mediaPlayer_ = null;
        Handler mainHandler = new Handler(ctx.getMainLooper());

        // TODO use JoH run on ui thread
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                synchronized(creationThreadLock) {
                    try {
                        mediaPlayer_ = new MediaPlayer();
                        Log.i(TAG, "media player created");
                    } finally {
                        mplayerCreated_ = true;
                        creationThreadLock.notifyAll();
                    }

                }
            }
        };
        mainHandler.post(myRunnable);


        try {
            synchronized(creationThreadLock) {
                // TODO thread deadlock possible here?
                while(mplayerCreated_ == false) {
                    creationThreadLock.wait(300);
                }
            }
        }catch (InterruptedException e){
            Log.e(TAG, "Cought exception", e);
        }
        return mediaPlayer_;
    }

    boolean isUiThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }
}
