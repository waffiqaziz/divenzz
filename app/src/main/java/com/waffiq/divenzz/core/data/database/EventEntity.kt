package com.waffiq.divenzz.core.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class EventEntity(

  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "id")
  var id: Int = 0,

  @ColumnInfo(name = "event_id")
  var eventId: Int = 0,

  @ColumnInfo(name = "event_name")
  var eventName: String? = null,

  @ColumnInfo(name = "image_url")
  var imageUrl: String? = null,
)