package com.libimedical.hera;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;

public class TraceRecorder {

    private static final String LOG_TAG = "TraceRecorder";
    private static String mFileName = null;

    private MediaRecorder mRecorder = null;
    private boolean       isRecording = false;

    private MediaPlayer   mPlayer = null;
    private boolean       isPlaying = false;

    public TraceRecorder(String fileName) {
        mFileName = fileName;
    }

    public void startPlaying() {
        if (isPlaying)
            return;
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (java.io.IOException e) {
            Log.e(LOG_TAG, "prepare() failed with file " + mFileName);
        }
        isPlaying = true;
    }

    public void stopPlaying() {
        if (!isPlaying)
            return;
        mPlayer.release();
        mPlayer = null;
        isPlaying = false;
    }

    public void startRecording() {
        if (isRecording || isPlaying)
            return;
        mRecorder = new MediaRecorder();
        try {
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            mRecorder.setOutputFile(mFileName);
            mRecorder.prepare();
            mRecorder.start();
        } catch (Exception e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        isRecording = true;
    }

    public void stopRecording() {
        if (!isRecording)
            return;
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        File recording = new File(mFileName);
        if (!recording.exists()) {
            Log.e(LOG_TAG, "The Recording was not created");
        } else {
            Log.v(LOG_TAG, "The Recording was created");
        }
        isRecording = false;
    }


}
