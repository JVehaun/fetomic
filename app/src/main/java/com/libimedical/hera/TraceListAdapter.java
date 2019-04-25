package com.libimedical.hera;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.libimedical.hera.RecordingEntryFragment.ListEntryPlaybackListener;
import com.libimedical.hera.RecordingEntryFragment.ListEntryShareListener;
import com.libimedical.hera.RecordingEntryFragment.FragmentInteractionListener;
import com.libimedical.hera.tracing.TracingItem;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TraceListAdapter extends RecyclerView.Adapter<TraceListAdapter.TraceViewHolder> {

    private final ListEntryPlaybackListener mPlaybackListener;
    private final ListEntryShareListener mShareListener;
    private final FragmentInteractionListener mItemListener;
    private final LayoutInflater mInflater;
    private List<TracingItem> mTraces; // Cached copy of items

    public TraceListAdapter(Context context,
                            ListEntryPlaybackListener playbackListener,
                            ListEntryShareListener shareListener,
                            FragmentInteractionListener itemListener) {
        mPlaybackListener = playbackListener;
        mShareListener = shareListener;
        mItemListener = itemListener;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public TraceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.fragment_recordingentry, parent, false);
        return new TraceViewHolder(view, mPlaybackListener, mShareListener, mItemListener);
    }

    @Override
    public void onBindViewHolder(TraceViewHolder holder, int position) {
        if (mTraces != null) {
            TracingItem current = mTraces.get(position);
            holder.mRecordIdView.setText(current.recordId);
            holder.mRecordingDateView.setText(getDateFromFile(current.fileName));
            holder.mRecordingTimeView.setText(getTimeFromFile(current.fileName));
            holder.mRecordingLengthView.setText(getLengthFromFile(current.fileName));
        } else {
            // Covers the case of data not being ready yet
            // TODO: This does not currently work
            holder.mRecordIdView.setText("No Traces saved yet");
        }
    }

    private static String getTimeFromFile(String fileName) {
        File file = new File(fileName);
        Date lastModified = new Date(file.lastModified());
        DateFormat df = new SimpleDateFormat("h:mm a");
        return df.format(lastModified);
    }

    private static String getDateFromFile(String fileName) {
        File file = new File(fileName);
        Date lastModified = new Date(file.lastModified());
        DateFormat df = new SimpleDateFormat("MMM d yyyy");
        return df.format(lastModified);
    }

    private static String getLengthFromFile(String fileName) {
        MediaMetadataRetriever metaRetriever;
        metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(fileName);
        int milliseconds = Integer.valueOf(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        int seconds = milliseconds / 1000;
        metaRetriever.release();
        String length = String.format("%02d:%02d", seconds / 100, seconds % 100);
        return length;
    }

    void setTraces(List<TracingItem> traces) {
        mTraces = traces;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mTraces != null) {
            return mTraces.size();
        }
        else return 0;
    }

    public static class TraceViewHolder extends RecyclerView.ViewHolder {

        // State fields
        private boolean isPlaying;

        // TextViews of the list entry
        private final TextView mRecordIdView;
        private final TextView mRecordingDateView;
        private final TextView mRecordingTimeView;
        private final TextView mRecordingLengthView;

        // Buttons of the list entry
        private ImageButton mPlaybackButton;
        private ImageButton mShareButton;

        public TraceViewHolder(View view,
                               ListEntryPlaybackListener playbackListener,
                               ListEntryShareListener shareListener,
                               FragmentInteractionListener itemListener) {
            super(view);

            // State fields
            isPlaying = false;

            // Set the whole view as a button
            view.setOnClickListener(v -> {
                itemListener.onItemClicked(getAdapterPosition());
            });

            // Buttons
            mShareButton = view.findViewById(R.id.share_button);
            mShareButton.setOnClickListener(v -> {
                shareListener.onShareClicked(getAdapterPosition());
            });
            mPlaybackButton = view.findViewById(R.id.playback_button);
            mPlaybackButton.setOnClickListener(v -> togglePlaying(v, playbackListener));

            // TextViews
            mRecordIdView = (TextView) view.findViewById(R.id.item_number);
            mRecordingDateView = (TextView) view.findViewById(R.id.recording_date);
            mRecordingTimeView = (TextView) view.findViewById(R.id.recording_time);
            mRecordingLengthView = (TextView) view.findViewById(R.id.recording_length);
        }

        private void togglePlaying(View v, ListEntryPlaybackListener playbackListener) {
            if (isPlaying) {
                ImageButton playButton = (ImageButton) v;
                playButton.setImageResource((R.drawable.ic_play_arrow_black_24dp));
                playbackListener.onPauseClicked(getAdapterPosition());
                isPlaying = false;
            } else {
                ImageButton playbackButton = (ImageButton) v;
                playbackButton.setImageResource((R.drawable.ic_stop_black_24dp));
                playbackListener.onPlayClicked(getAdapterPosition());
                isPlaying = true;
            }
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mRecordIdView.getText() + "'";
        }
    }

}
