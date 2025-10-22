package com.waffiq.divenzz.ui.upcoming

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.waffiq.divenzz.R.string.no_upcoming_event_found
import com.waffiq.divenzz.core.data.remote.response.EventResponse
import com.waffiq.divenzz.databinding.FragmentUpcomingEventBinding
import com.waffiq.divenzz.ui.adapter.EventAdapter
import com.waffiq.divenzz.ui.state.EventUiState
import com.waffiq.divenzz.ui.viewmodel.ViewModelFactory
import com.waffiq.divenzz.utils.Helpers.openDetailPage

class UpcomingEventFragment : Fragment() {

  private var _binding: FragmentUpcomingEventBinding? = null
  private val binding get() = _binding!!

  private lateinit var viewModel : UpcomingEventViewModel

  private lateinit var eventAdapter: EventAdapter

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    _binding = FragmentUpcomingEventBinding.inflate(inflater, container, false)
    val root: View = binding.root

    val factory = ViewModelFactory.getInstance(requireActivity())
    viewModel = ViewModelProvider(this, factory)[UpcomingEventViewModel::class.java]

    setupRecyclerView()
    observePastEvents()
    setupErrorRetry()

    return root
  }

  private fun setupRecyclerView() {
    eventAdapter = EventAdapter(::onClick)
    binding.rvEvents.layoutManager =
      LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    binding.rvEvents.adapter = eventAdapter
  }

  private fun onClick(event: EventResponse) {
    requireActivity().openDetailPage(event.id)
  }

  private fun observePastEvents() {
    viewModel.uiState.observe(viewLifecycleOwner) { state ->
      when (state) {
        is EventUiState.Loading -> {
          binding.loading.progressCircular.isVisible = true
          binding.error.root.isVisible = false
          binding.rvEvents.isVisible = false
        }

        is EventUiState.Success -> {
          binding.loading.progressCircular.isVisible = false
          binding.error.root.isVisible = false
          binding.rvEvents.isVisible = true
          eventAdapter.setEvent(state.events)
        }

        is EventUiState.Error -> {
          binding.loading.progressCircular.isVisible = false
          binding.error.root.isVisible = true
          binding.rvEvents.isVisible = false
        }

        is EventUiState.Empty -> {
          binding.loading.progressCircular.isVisible = false
          binding.error.root.isVisible = true
          binding.rvEvents.isVisible = false
          binding.error.tvErrorMessage.text = getString(no_upcoming_event_found)
        }
      }
    }
  }

  private fun setupErrorRetry() {
    binding.error.btnTryAgain.setOnClickListener {
      viewModel.getUpcomingEvents()
    }
  }


  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}