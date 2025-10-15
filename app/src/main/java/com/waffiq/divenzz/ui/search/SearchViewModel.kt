package com.waffiq.divenzz.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.waffiq.divenzz.core.data.remote.response.EventResponse
import com.waffiq.divenzz.core.data.remote.response.ListEventResponse
import com.waffiq.divenzz.core.data.remote.retrofit.EventApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchViewModel : ViewModel() {

  private val _events = MutableLiveData<List<EventResponse>>()
  val events: LiveData<List<EventResponse>> = _events

  private val _isLoading = MutableLiveData<Boolean>()
  val isLoading: LiveData<Boolean> = _isLoading

  private val _snackBarText = MutableLiveData<String>()
  val snackBarText: LiveData<String> = _snackBarText

  fun getUpcomingEvent(query: String) {
    _snackBarText.value = ""
    _isLoading.value = true

    val client = EventApiConfig.Companion.getApiService().search(query)
    client.enqueue(object : Callback<ListEventResponse> {
      override fun onResponse(
        call: Call<ListEventResponse>,
        response: Response<ListEventResponse>,
      ) {
        _isLoading.value = false
        if (response.isSuccessful) {
          val responseBody = response.body()
          if (responseBody != null && responseBody.listEvents != null) {
            if (responseBody.listEvents.isEmpty()) {
              _snackBarText.value = "No Events Found"
            }
            _events.value = responseBody.listEvents.filterNotNull()
          }
        } else {
          Log.e(TAG, "onFailure: ${response.message()}")
          _snackBarText.value = response.message() ?: FAILED
        }
      }

      override fun onFailure(call: Call<ListEventResponse>, t: Throwable) {
        _isLoading.value = false
        Log.e(TAG, "onFailure: ${t.message}")
        _snackBarText.value = FAILED
      }
    })
  }

  companion object {
    private const val TAG = "SearchViewModel"
    private const val FAILED = "Error: Check your connection and try again"
  }
}