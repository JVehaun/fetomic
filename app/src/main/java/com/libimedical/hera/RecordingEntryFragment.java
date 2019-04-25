package com.libimedical.hera;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.libimedical.hera.tracing.TraceViewModel;
import com.libimedical.hera.tracing.TracingItem;

import java.util.List;

public class RecordingEntryFragment extends Fragment {

    private TraceViewModel mTraceViewModel;

    public RecordingEntryFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recordingentry_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            // Click listener for the row as a whole
            FragmentInteractionListener itemListener = position -> {
                // Get the TracingItem
                TracingItem item = mTraceViewModel.getAllTraces().getValue().get(position);
                Bundle bundle = new Bundle();
                bundle.putString("name", item.recordId);
                bundle.putString("fileName", item.fileName);
                bundle.putString("notes", item.notes);

                // Create the new fragment page
                RecordingDetailFragment recordingDetailFragment = new RecordingDetailFragment();
                recordingDetailFragment.setArguments(bundle);

                // Switch the page to the new fragment
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, recordingDetailFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            };
            TraceListAdapter adapter = new TraceListAdapter(context, itemListener);
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

}
