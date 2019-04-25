package com.libimedical.hera.tracing;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface TracingItemDao {

    @Query("SELECT * FROM tracingitem")
    LiveData<List<TracingItem>> getAll();

    // @Query("SELECT * FROM tracingitem WHERE file IN (:)")
    // List<TracingItem> loadAllByIds(int[] userIds);

    /*
    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
            "last_name LIKE :last LIMIT 1")
    User findByName(String first, String last);

    */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(TracingItem... tracingItem);
    /*

    @Delete
    void delete(User user);
    */
}
