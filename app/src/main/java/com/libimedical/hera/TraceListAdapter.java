package com.libimedical.hera;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.libimedical.hera.RecordingEntryFragment.FragmentInteractionListener;
import com.libimedical.hera.tracing.TracingItem;

import java.util.List;

public class TraceListAdapter extends RecyclerView.Adapter<TraceListAdapter.TraceViewHolder> {

    private final FragmentInteractionListener mItemListener;
    private final LayoutInflater mInflater;
    private List<TracingItem> mTraces; // Cached copy of items

    public TraceListAdapter(Context context,
                            FragmentInteractionListener itemListener) {
        mItemListener = itemListener;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public TraceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.fragment_recordingentry, parent, false);
        return new TraceViewHolder(view, mItemListener);
    }

    @Override
    public void onBindViewHolder(TraceViewHolder holder, int position) {
        if (mTraces != null) {
            TracingItem current = mTraces.get(position);
            holder.mRecordIdView.setText(current.recordId);
            holder.mRecordingDateView.setText(TracingItem.getDateFromFile(current.fileName));
            holder.mRecordingTimeView.setText(TracingItem.getTimeFromFile(current.fileName));
            holder.mRecordingLengthView.setText(TracingItem.getLengthFromFile(current.fileName));
        } else {
            // Covers the case of data not being ready yet
            // TODO: This does not currently work
            holder.mRecordIdView.setText("No Traces saved yet");
        }
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

        public TraceViewHolder(View view,
                               FragmentInteractionListener itemListener) {
            super(view);

            // State fields
            isPlaying = false;

            // Set the whole view as a button
            view.setOnClickListener(v -> {
                itemListener.onItemClicked(getAdapterPosition());
            });

            // TextViews
            mRecordIdView = (TextView) view.findViewById(R.id.item_number);
            mRecordingDateView = (TextView) view.findViewById(R.id.recording_date);
            mRecordingTimeView = (TextView) view.findViewById(R.id.recording_time);
            mRecordingLengthView = (TextView) view.findViewById(R.id.recording_length);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mRecordIdView.getText() + "'";
        }
    }

}
