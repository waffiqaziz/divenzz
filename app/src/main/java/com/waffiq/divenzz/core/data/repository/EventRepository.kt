package com.waffiq.divenzz.core.data.repository

import android.app.Application
import com.waffiq.divenzz.core.data.database.EventDao
import com.waffiq.divenzz.core.data.database.EventDatabase
import com.waffiq.divenzz.core.data.database.EventEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class EventRepository(application: Application) {

  private val mEventsDao: EventDao

  init {
    val db = EventDatabase.getDatabase(application)
    mEventsDao = db.eventDao()
  }

  fun getAllEvents(): Flow<List<EventEntity>> =
    mEventsDao.getAllEvents().flowOn(Dispatchers.IO)

  suspend fun insert(event: EventEntity) {
    mEventsDao.insert(event)
  }

  suspend fun delete(event: EventEntity) {
    mEventsDao.deleteByEventId(event.eventId)
  }

  suspend fun isFavorite(eventId: Int): Boolean =
    mEventsDao.isFavorite(eventId)
}
