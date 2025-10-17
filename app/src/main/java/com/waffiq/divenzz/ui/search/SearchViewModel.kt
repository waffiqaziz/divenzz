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

  private val _isEmpty = MutableLiveData<Boolean>()
  val isEmpty: LiveData<Boolean> = _isEmpty

  private val _errorMessage = MutableLiveData<String>()
  val errorMessage: LiveData<String> = _errorMessage

  fun search(query: String) {
    _errorMessage.value = ""
    _isLoading.value = true
    _isEmpty.value = false

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
            _isEmpty.value = responseBody.listEvents.isEmpty()
            _events.value = responseBody.listEvents.filterNotNull()
          }
        } else {
          Log.e(TAG, "onFailure: ${response.message()}")
          _errorMessage.value = response.message() ?: FAILED
        }
      }

      override fun onFailure(call: Call<ListEventResponse>, t: Throwable) {
        _isLoading.value = false
        Log.e(TAG, "onFailure: ${t.message}")
        _errorMessage.value = FAILED
      }
    })
  }

  companion object {
    private const val TAG = "SearchViewModel"
    private const val FAILED = "Error: Check your connection and try again"
  }
}