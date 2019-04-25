package com.libimedical.hera.tracing;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {TracingItem.class}, version = 1, exportSchema = false)
public abstract class TraceDatabase extends RoomDatabase {
    public abstract TracingItemDao tracingItemDao();

    private static volatile TraceDatabase INSTANCE;

    static TraceDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (TraceDatabase.class) {
                if (INSTANCE == null) {
                    // Create database here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            TraceDatabase.class, "traceitem")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback();
    // TODO: I should probably load the database async on startup here:

    // TODO: After further thought, maybe not. It works if it's a local database for now
    // See https://codelabs.developers.google.com/codelabs/android-room-with-a-view/#11

}
