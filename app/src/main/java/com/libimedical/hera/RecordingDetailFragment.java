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

import java.io.File;

import static android.support.v4.content.FileProvider.getUriForFile;


public class RecordingDetailFragment extends Fragment {

    View inflaterView;

    String recordId;
    String fileName;

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
        mPlaybackButton.setOnClickListener(v -> togglePlaying(v));

        // TextViews
        // mRecordIdView = (TextView) inflaterView.findViewById(R.id.item_number);

        return inflaterView;
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
