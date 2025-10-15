package com.waffiq.divenzz.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.waffiq.divenzz.core.data.remote.response.DetailEventResponse
import com.waffiq.divenzz.core.data.remote.response.EventResponse
import com.waffiq.divenzz.core.data.remote.retrofit.EventApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailEventViewModel : ViewModel() {

  private val _event = MutableLiveData<EventResponse>()
  val event: LiveData<EventResponse> = _event

  private val _isLoading = MutableLiveData<Boolean>()
  val isLoading: LiveData<Boolean> = _isLoading

  private val _snackBarText = MutableLiveData<String>()
  val snackBarText: LiveData<String> = _snackBarText

  fun getDetailEvent(eventId: Int) {
    _isLoading.value = true
    _snackBarText.value = ""

    val client = EventApiConfig.Companion.getApiService().getEventDetail(eventId)
    client.enqueue(object : Callback<DetailEventResponse> {
      override fun onResponse(
        call: Call<DetailEventResponse>,
        response: Response<DetailEventResponse>
      ) {
        _isLoading.value = false
        if (response.isSuccessful) {
          val responseBody = response.body()
          if (responseBody != null && responseBody.event != null) {
            _event.value = responseBody.event
          }
        } else {
          Log.e(TAG, "onFailure: ${response.message()}")
          _snackBarText.value = response.message() ?: FAILED
        }
      }

      override fun onFailure(call: Call<DetailEventResponse>, t: Throwable) {
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