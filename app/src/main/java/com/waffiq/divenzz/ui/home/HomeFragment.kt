package com.waffiq.divenzz.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.waffiq.divenzz.core.data.remote.response.EventResponse
import com.waffiq.divenzz.databinding.FragmentHomeBinding
import com.waffiq.divenzz.ui.adapter.EventAdapter
import com.waffiq.divenzz.ui.adapter.SmallEventAdapter
import com.waffiq.divenzz.ui.past.PastEventViewModel
import com.waffiq.divenzz.ui.upcoming.UpcomingEventViewModel
import com.waffiq.divenzz.utils.Helpers.openDetailPage

class HomeFragment : Fragment() {

  private var _binding: FragmentHomeBinding? = null
  private val binding get() = _binding!!

  private val viewModelUpcoming by viewModels<UpcomingEventViewModel>()
  private val viewModelPast by viewModels<PastEventViewModel>()

  private lateinit var eventAdapterVertical: SmallEventAdapter
  private lateinit var eventAdapterHorizontal: EventAdapter

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    _binding = FragmentHomeBinding.inflate(inflater, container, false)
    val root: View = binding.root

    setupRecyclerView()
    getNotes()
    btnAction()

    return root
  }

  private fun setupRecyclerView() {
    eventAdapterVertical = SmallEventAdapter(::onClick)
    eventAdapterHorizontal = EventAdapter(::onClick)

    binding.rvEventsVertical.layoutManager =
      LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    binding.rvEventsHorizontal.layoutManager =
      LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    LinearSnapHelper().attachToRecyclerView(binding.rvEventsHorizontal)

    binding.rvEventsVertical.adapter = eventAdapterVertical
    binding.rvEventsHorizontal.adapter = eventAdapterHorizontal
  }

  private fun onClick(event: EventResponse) {
    requireActivity().openDetailPage(event.id)
  }

  private fun getNotes() {
    // observe loading states
    viewModelUpcoming.isLoading.observe(viewLifecycleOwner) {
      binding.loading.progressCircular.isVisible = it
      binding.tvPastEvents.isVisible = !it
      binding.tvUpcomingEvents.isVisible = !it
    }

    // observe error states
    viewModelUpcoming.snackBarText.observe(viewLifecycleOwner) {
      if(it.isNotEmpty()){ // if there is an error
        binding.tvPastEvents.isVisible = false
        binding.tvUpcomingEvents.isVisible = false
        binding.error.root.isVisible = true
        binding.rvEventsVertical.isVisible = false
        binding.rvEventsHorizontal.isVisible = false
      } else { // if there is no error
        binding.tvPastEvents.isVisible = true
        binding.tvUpcomingEvents.isVisible = true
        binding.error.root.isVisible = false
        binding.rvEventsVertical.isVisible = true
        binding.rvEventsHorizontal.isVisible = true
      }
      binding.error.tvErrorMessage.text = it
    }

    // observe data
    viewModelUpcoming.events.observe(viewLifecycleOwner) {
      val item = it.take(5)
      eventAdapterHorizontal.setEvent(item)
    }
    viewModelPast.events.observe(viewLifecycleOwner) {
      eventAdapterVertical.setEvent(it)
    }
  }

  private fun btnAction(){
    binding.error.btnTryAgain.setOnClickListener {
      viewModelPast.getPastEvent()
      viewModelUpcoming.getUpcomingEvent()
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}