package com.libimedical.hera.tracing;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class TraceRepository {
    private TracingItemDao mTracingItemDao;
    private LiveData<List<TracingItem>> mAllTraces;

    TraceRepository(Application application) {
        TraceDatabase db = TraceDatabase.getDatabase(application);
        mTracingItemDao = db.tracingItemDao();
        mAllTraces = mTracingItemDao.getAll();
    }

    LiveData<List<TracingItem>> getAllTraces() {
        return mAllTraces;
    }

    public void insert(TracingItem item) {
        new insertAsyncTask(mTracingItemDao).execute(item);
    }

    private static class insertAsyncTask extends AsyncTask<TracingItem, Void, Void> {

        private TracingItemDao mAsyncTaskDao;

        insertAsyncTask(TracingItemDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final TracingItem... params) {
            mAsyncTaskDao.insertAll(params[0]);
            return null;
        }
    }
}
