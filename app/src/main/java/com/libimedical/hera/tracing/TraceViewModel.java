package com.libimedical.hera.tracing;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class TraceViewModel extends AndroidViewModel {

    private TraceRepository mTraceRepository;

    private LiveData<List<TracingItem>> mAllTraceItems;

    public TraceViewModel(Application application) {
        super(application);
        mTraceRepository = new TraceRepository(application);
        mAllTraceItems = mTraceRepository.getAllTraces();
    }

    public LiveData<List<TracingItem>> getAllTraces() {
        return mAllTraceItems;
    }

    public void insert(TracingItem item) {
        mTraceRepository.insert(item);
    }

    public TracingItem get(String name) {
        for (TracingItem item : mAllTraceItems.getValue()) {
            if (item.recordId == name) {
                return item;
            }
        }
        return null;
    }
}
