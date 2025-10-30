package com.waffiq.divenzz.ui.past

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.waffiq.divenzz.R.string.no_past_events_found
import com.waffiq.divenzz.core.data.remote.response.EventResponse
import com.waffiq.divenzz.databinding.FragmentPastEventBinding
import com.waffiq.divenzz.ui.adapter.EventAdapter
import com.waffiq.divenzz.ui.state.EventUiState
import com.waffiq.divenzz.ui.viewmodel.ViewModelFactory
import com.waffiq.divenzz.utils.Helpers.openDetailPage

class PastEventFragment : Fragment() {

  private var _binding: FragmentPastEventBinding? = null
  private val binding get() = _binding ?: error("FragmentPastEventBinding is null")

  private lateinit var viewModel: PastEventViewModel

  private lateinit var eventAdapter: EventAdapter

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    _binding = FragmentPastEventBinding.inflate(inflater, container, false)
    val root: View = binding.root

    val factory = ViewModelFactory.getInstance(requireActivity())
    viewModel = ViewModelProvider(this, factory)[PastEventViewModel::class.java]

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
          binding.apply {
            loading.progressCircular.isVisible = true
            error.root.isVisible = false
            rvEvents.isVisible = false
          }
        }

        is EventUiState.Success -> {
          binding.apply {
            loading.progressCircular.isVisible = false
            error.root.isVisible = false
            rvEvents.isVisible = true
          }
          eventAdapter.setEvent(state.events)
        }

        is EventUiState.Error -> {
          binding.apply {
            loading.progressCircular.isVisible = false
            error.root.isVisible = true
            rvEvents.isVisible = false
          }
        }

        is EventUiState.Empty -> {
          binding.apply {
            loading.progressCircular.isVisible = false
            error.root.isVisible = true
            rvEvents.isVisible = false
            error.tvErrorMessage.text = getString(no_past_events_found)
          }
        }
      }
    }
  }

  private fun setupErrorRetry() {
    binding.error.btnTryAgain.setOnClickListener {
      viewModel.getPastEvents()
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}