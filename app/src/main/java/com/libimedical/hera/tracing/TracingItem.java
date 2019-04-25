package com.libimedical.hera.tracing;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.media.MediaMetadataRetriever;
import android.support.annotation.NonNull;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    public static String getTimeFromFile(String fileName) {
        File file = new File(fileName);
        Date lastModified = new Date(file.lastModified());
        DateFormat df = new SimpleDateFormat("h:mm a");
        return df.format(lastModified);
    }

    public static String getDateFromFile(String fileName) {
        File file = new File(fileName);
        Date lastModified = new Date(file.lastModified());
        DateFormat df = new SimpleDateFormat("MMM d yyyy");
        return df.format(lastModified);
    }

    public static String getLengthFromFile(String fileName) {
        MediaMetadataRetriever metaRetriever;
        metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(fileName);
        int milliseconds = Integer.valueOf(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        int seconds = milliseconds / 1000;
        metaRetriever.release();
        String length = String.format("%02d:%02d", seconds / 100, seconds % 100);
        return length;
    }

}
