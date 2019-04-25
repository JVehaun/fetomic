package com.libimedical.hera.tracing;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * An item representing a single tracing and recording.
 */
@Entity
public class TracingItem {
    @ColumnInfo(name = "record_id")
    public final String recordId;
    @PrimaryKey
    @NonNull
    public final String fileName;
    @ColumnInfo(name = "notes")
    @NonNull
    public final String notes;

    public TracingItem(String recordId, String fileName, String notes) {
        this.recordId = recordId;
        this.fileName = fileName;
        this.notes = notes;
    }

    @Override
    public String toString() {
        return recordId + ": " + notes;
    }
}
