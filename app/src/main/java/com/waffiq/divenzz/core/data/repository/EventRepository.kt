package com.waffiq.divenzz.core.data.repository

import android.util.Log
import com.waffiq.divenzz.core.data.database.EventDao
import com.waffiq.divenzz.core.data.database.EventEntity
import com.waffiq.divenzz.core.data.remote.response.EventResponse
import com.waffiq.divenzz.core.data.remote.retrofit.EventApiService
import com.waffiq.divenzz.utils.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class EventRepository private constructor(
  private val apiService: EventApiService,
  private val mEventsDao: EventDao,
) {

  // region REMOTE
  fun getPastEvents(): Flow<NetworkResult<List<EventResponse>>> {
    return flow {
      emit(NetworkResult.Loading)
      try {
        val response = apiService.getAllEvent(active = 0)

        if (response.error == true) {
          Log.e(TAG, "GetEvents Fail: ${response.message}")
          emit(NetworkResult.Error(response.message ?: "An unknown error occurred"))
          return@flow
        }

        val events = response.listEvents?.filterNotNull().orEmpty()

        if (events.isEmpty()) {
          Log.e(TAG, "GetPastEvents Empty or Null")
          emit(NetworkResult.Error("No events found"))
        } else {
          emit(NetworkResult.Success(events))
        }
      } catch (e: Exception) {
        Log.e(TAG, "GetPastEvents Exception: ${e.message}", e)
        emit(NetworkResult.Error(e.message ?: "Network error occurred"))
      }
    }.flowOn(Dispatchers.IO)
  }

  fun searchEvents(query: String): Flow<NetworkResult<List<EventResponse>>> {
    return flow {
      emit(NetworkResult.Loading)
      try {
        val response = apiService.search(query)

        if (response.error == true) {
          Log.e(TAG, "SearchEvents Fail: ${response.message}")
          emit(NetworkResult.Error(response.message ?: "An unknown error occurred"))
          return@flow
        }

        val events = response.listEvents?.filterNotNull().orEmpty()

        if (events.isEmpty()) {
          Log.e(TAG, "SearchEvents Empty or Null")
          emit(NetworkResult.Success(emptyList()))
        } else {
          emit(NetworkResult.Success(events))
        }
      } catch (e: Exception) {
        Log.e(TAG, "SearchEvents Exception: ${e.message}", e)
        emit(NetworkResult.Error(e.message ?: "Network error occurred"))
      }
    }.flowOn(Dispatchers.IO)
  }

  fun getUpcomingEvents(): Flow<NetworkResult<List<EventResponse>>> {
    return flow {
      emit(NetworkResult.Loading)
      try {
        val response = apiService.getAllEvent(active = 1)

        if (response.error == true) {
          Log.e(TAG, "GetUpcomingEvents Fail: ${response.message}")
          emit(NetworkResult.Error(response.message ?: "An unknown error occurred"))
          return@flow
        }

        val events = response.listEvents?.filterNotNull().orEmpty()

        if (events.isEmpty()) {
          Log.e(TAG, "GetUpcomingEvents Empty or Null")
          emit(NetworkResult.Error("No events found"))
        } else {
          emit(NetworkResult.Success(events))
        }
      } catch (e: Exception) {
        Log.e(TAG, "GetUpcomingEvents Exception: ${e.message}", e)
        emit(NetworkResult.Error(e.message ?: "Network error occurred"))
      }
    }.flowOn(Dispatchers.IO)
  }

  fun getDetailEvent(eventId: Int): Flow<NetworkResult<EventResponse>> {
    return flow {
      emit(NetworkResult.Loading)
      try {
        val response = apiService.getDetailEvent(eventId)

        if (response.error == true) {
          Log.e(TAG, "GetDetailEvent Fail: ${response.message}")
          emit(NetworkResult.Error(response.message ?: "An unknown error occurred"))
          return@flow
        }

        val event = response.event

        if (event == null) {
          Log.e(TAG, "GetDetailEvent Null")
          emit(NetworkResult.Error("Event not found"))
        } else {
          emit(NetworkResult.Success(event))
        }
      } catch (e: Exception) {
        Log.e(TAG, "GetDetailEvent Exception: ${e.message}", e)
        emit(NetworkResult.Error(e.message ?: "Network error occurred"))
      }
    }.flowOn(Dispatchers.IO)
  }
  // endregion REMOTE

  // region LOCAL
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
  // endregion LOCAL

  companion object {
    private const val TAG = "EventRepository"

    @Volatile
    private var instance: EventRepository? = null

    fun getInstance(
      apiService: EventApiService,
      mEventsDao: EventDao,
    ): EventRepository =
      instance ?: synchronized(this) {
        instance ?: EventRepository(apiService, mEventsDao)
      }.also { instance = it }
  }
}
