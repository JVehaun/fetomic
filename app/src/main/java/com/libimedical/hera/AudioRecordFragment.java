package com.libimedical.hera;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.libimedical.hera.waveform.AudioDataReceivedListener;
import com.libimedical.hera.waveform.RecordingThread;
import com.newventuresoftware.waveform.WaveformView;

public class AudioRecordFragment extends Fragment {

    private WaveformView mRealtimeWaveformView;
    private RecordingThread mRecordingThread;

    View inflaterView;
    TraceRecorder mTraceRecorder;

    public static AudioRecordFragment newInstance() {
        return new AudioRecordFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        inflaterView = inflater.inflate(R.layout.fragment_audio_record, container, false);
        FloatingActionButton fab = inflaterView.findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            if (!mRecordingThread.recording()) {
                onRecord(view);
            } else {
                onPause(view);
            }
        });

        mRealtimeWaveformView = (WaveformView) inflaterView.findViewById(R.id.waveformView);
        mRecordingThread = new RecordingThread(new AudioDataReceivedListener() {
            @Override
            public void onAudioDataReceived(short[] data) {
                mRealtimeWaveformView.setSamples(data);
            }
        });

        return inflaterView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel - Later, I have no idea if this is right
    }

    public void onRecord(View view) {
        FloatingActionButton fab = inflaterView.findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_stop_white_24dp);
        fab.setOnClickListener(v -> onPause(v));
        mRealtimeWaveformView = (WaveformView) inflaterView.findViewById(R.id.waveformView);
        mRecordingThread = new RecordingThread(new AudioDataReceivedListener() {
            @Override
            public void onAudioDataReceived(short[] data) {
                mRealtimeWaveformView.setSamples(data);
            }
        });
        mRecordingThread.startRecording();
        mTraceRecorder.startRecording();
    }

    public void onPause(View view) {
        FloatingActionButton fab = inflaterView.findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_fiber_manual_record_white_24dp);
        fab.setOnClickListener(v -> onRecord(v));

        mTraceRecorder.stopRecording();

        // TODO: Add more robust dialogs here

        DialogFragment recordingDialogFragment = new SaveRecordingDialogFragment();
        recordingDialogFragment.show(getFragmentManager(), "Save Recording");
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Record to the external cache directory for visibility
        String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiorecordtemp.mp3";
        mTraceRecorder = new TraceRecorder(mFileName);

    }

}
