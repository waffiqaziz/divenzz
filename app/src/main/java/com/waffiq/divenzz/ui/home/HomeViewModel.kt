package com.waffiq.divenzz.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.waffiq.divenzz.core.data.remote.response.EventResponse
import com.waffiq.divenzz.core.data.remote.response.EventsItem
import com.waffiq.divenzz.core.data.remote.retrofit.EventApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {

  private val _events = MutableLiveData<List<EventsItem>>()
  val events: LiveData<List<EventsItem>> = _events

  private val _isLoading = MutableLiveData<Boolean>()
  val isLoading: LiveData<Boolean> = _isLoading

  private val _snackBarText = MutableLiveData<String>()
  val snackBarText: LiveData<String> = _snackBarText

  init {
    getAllEvent()
  }

  fun getAllEvent() {
    _isLoading.value = true
    val client = EventApiConfig.getApiService().getAllEvent()
    client.enqueue(object : Callback<EventResponse> {
      override fun onResponse(
        call: Call<EventResponse>,
        response: Response<EventResponse>,
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
          _snackBarText.value = FAILED
        }
      }

      override fun onFailure(call: Call<EventResponse>, t: Throwable) {
        _isLoading.value = false
        Log.e(TAG, "onFailure: ${t.message}")
        _snackBarText.value = FAILED
      }
    })
  }

  companion object {
    private const val TAG = "HomeViewModel"
    private const val FAILED = "Connection Failed"
  }
}