package com.libimedical.hera;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.libimedical.hera.tracing.TracingItem;
import com.libimedical.hera.waveform.PlaybackListener;
import com.libimedical.hera.waveform.PlaybackThread;
import com.newventuresoftware.waveform.WaveformView;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import static android.support.v4.content.FileProvider.getUriForFile;


public class RecordingDetailFragment extends Fragment {

    private WaveformView mRealtimeWaveformView;
    private PlaybackThread mPlaybackThread;

    View inflaterView;

    private TextView mRecordingLengthView;
    private TextView mNotesView;

    String recordId;
    String fileName;
    String notes;

    private TraceRecorder mTraceRecorder;

    private boolean isPlaying;

    public RecordingDetailFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recordId = getArguments().getString("recordId");
            fileName = getArguments().getString("fileName");
            notes = getArguments().getString("notes");
        }
        this.isPlaying = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflaterView = inflater.inflate(R.layout.fragment_recording_detail, container, false);

        // Buttons
        ImageButton mShareButton = inflaterView.findViewById(R.id.share_button);
        mShareButton.setOnClickListener(v -> shareRecording(v));

        ImageButton mPlaybackButton = inflaterView.findViewById(R.id.playback_button);
        // mPlaybackButton.setOnClickListener(v -> togglePlaying(v));

        // TextViews
        mRecordingLengthView = inflaterView.findViewById((R.id.recording_length));
        mRecordingLengthView.setText(TracingItem.getLengthFromFile(fileName));

        mNotesView = inflaterView.findViewById(R.id.recording_notes);
        mNotesView.setText(notes);

        final WaveformView mPlaybackView = inflaterView.findViewById(R.id.playbackWaveformView);

        short[] samples = null;
        try {
            samples = getAudioSample(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (samples != null) {
            mPlaybackThread = new PlaybackThread(samples, new PlaybackListener() {
                @Override
                public void onProgress(int progress) {
                    mPlaybackView.setMarkerPosition(progress);
                }
                @Override
                public void onCompletion() {
                    mPlaybackView.setMarkerPosition(mPlaybackView.getAudioLength());
                    mPlaybackButton.setImageResource(android.R.drawable.ic_media_play);
                }
            });
            mPlaybackView.setChannels(1);
            mPlaybackView.setSampleRate(PlaybackThread.SAMPLE_RATE);
            mPlaybackView.setSamples(samples);

            mPlaybackButton.setOnClickListener(v -> {
                if (!mPlaybackThread.playing()) {
                    mPlaybackThread.startPlayback();
                    mPlaybackButton.setImageResource(android.R.drawable.ic_media_pause);
                } else {
                    mPlaybackThread.stopPlayback();
                    mPlaybackButton.setImageResource(android.R.drawable.ic_media_play);
                }
            });
        }

        return inflaterView;
    }

    @Override
    public void onStop() {
        super.onStop();

        mPlaybackThread.stopPlayback();
    }

    private short[] getAudioSample(String fileName) throws IOException{
        File recordingFile = new File(fileName);
        FileInputStream is = new FileInputStream(fileName);
        byte[] data;
        try {
            data = IOUtils.toByteArray(is);
        } finally {
            if (is != null) {
                is.close();
            }
        }

        ShortBuffer sb = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
        short[] samples = new short[sb.limit()];
        sb.get(samples);
        return samples;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void togglePlaying(View v) {
        if (isPlaying) {
            ImageButton playButton = (ImageButton) v;
            playButton.setImageResource((R.drawable.ic_play_arrow_black_24dp));
            mTraceRecorder.stopPlaying();
            isPlaying = false;
        } else {
            ImageButton playbackButton = (ImageButton) v;
            playbackButton.setImageResource((R.drawable.ic_stop_black_24dp));
            mTraceRecorder = new TraceRecorder(fileName);
            mTraceRecorder.startPlaying();
            isPlaying = true;
        }
    }

    private void shareRecording(View v) {
        String recordingPath = fileName;
        File recordingFile = new File(recordingPath);
        Context context2 = getContext();
        Uri uriToFile = getUriForFile(context2, "com.libimedical.hera.fileprovider", recordingFile);

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uriToFile);
        shareIntent.setType("audio/mpeg");
        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));
    }
}
