package com.libimedical.hera;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.libimedical.hera.tracing.TraceViewModel;
import com.libimedical.hera.tracing.TracingItem;

import java.io.File;
import java.util.List;

import static android.support.v4.content.FileProvider.getUriForFile;

public class RecordingEntryFragment extends Fragment {

    private TraceViewModel mTraceViewModel;

    private TraceRecorder mTraceRecorder;

    private boolean isPlaying;

    public RecordingEntryFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.isPlaying = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recordingentry_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            // Click listener for playback
            ListEntryPlaybackListener playbackListener = new ListEntryPlaybackListener() {
                @Override
                public void onPlayClicked(int position) {
                    TracingItem item = mTraceViewModel.getAllTraces().getValue().get(position);
                    mTraceRecorder = new TraceRecorder(item.fileName);
                    mTraceRecorder.startPlaying();
                }

                @Override
                public void onPauseClicked(int position) {
                    if (mTraceRecorder != null) {
                        mTraceRecorder.stopPlaying();
                    }
                }
            };

            // Click listener for share button
            ListEntryShareListener shareListener = position -> {
                TracingItem item = mTraceViewModel.getAllTraces().getValue().get(position);
                String recordingPath = item.fileName;
                File recordingFile = new File(recordingPath);
                Context context2 = getContext();
                Uri uriToFile = getUriForFile(context2, "com.libimedical.hera.fileprovider", recordingFile);

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, uriToFile);
                shareIntent.setType("audio/mpeg");
                startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));
            };

            // Click listener for the row as a whole
            FragmentInteractionListener itemListener = position -> {
                // Get the TracingItem
                TracingItem item = mTraceViewModel.getAllTraces().getValue().get(position);
                Bundle bundle = new Bundle();
                bundle.putString("name", item.recordId);
                bundle.putString("fileName", item.fileName);

                // Create the new fragment page
                RecordingDetailFragment recordingDetailFragment = new RecordingDetailFragment();
                recordingDetailFragment.setArguments(bundle);

                // Switch the page to the new fragment
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, recordingDetailFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            };
            TraceListAdapter adapter = new TraceListAdapter(context, playbackListener, shareListener, itemListener);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            mTraceViewModel = ViewModelProviders.of(this).get(TraceViewModel.class);
            mTraceViewModel.getAllTraces().observe(this, new Observer<List<TracingItem>>() {
                @Override
                public void onChanged(@Nullable List<TracingItem> tracingItems) {
                    adapter.setTraces(tracingItems);
                }
            });

        }
        return view;
    }

    private void togglePlaying(View v) {
        if (isPlaying) {
            ImageButton playButton = (ImageButton) v;
            playButton.setImageResource((R.drawable.ic_play_arrow_black_24dp));
            isPlaying = false;
        } else {
            ImageButton playbackButton = (ImageButton) v;
            playbackButton.setImageResource((R.drawable.ic_stop_black_24dp));
            isPlaying = true;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // mListener = null;
    }

    public interface FragmentInteractionListener {
        void onItemClicked(int position);
    }

    public interface ListEntryPlaybackListener {
        // TODO: Update argument type and name
        void onPlayClicked(int position);

        void onPauseClicked(int position);
    }

    public interface ListEntryShareListener {
        // TODO: Update argument type and name
        void onShareClicked(int position);
    }
}
