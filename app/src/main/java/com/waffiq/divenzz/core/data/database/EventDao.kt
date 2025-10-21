package com.waffiq.divenzz.core.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun insert(event: EventEntity)

  @Query("DELETE FROM EventEntity WHERE event_id = :eventId")
  suspend fun deleteByEventId(eventId: Int)

  @Query("SELECT * from EventEntity ORDER BY id ASC")
  fun getAllEvents(): Flow<List<EventEntity>>

  @Query("SELECT EXISTS(SELECT * FROM EventEntity WHERE event_id = :eventId)")
  suspend fun isFavorite(eventId: Int): Boolean
}
