package com.waffiq.divenzz.ui.past

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.waffiq.divenzz.core.data.remote.response.EventResponse
import com.waffiq.divenzz.databinding.FragmentPastEventBinding
import com.waffiq.divenzz.ui.adapter.EventAdapter
import com.waffiq.divenzz.utils.Helpers.getThemeColor
import com.waffiq.divenzz.utils.Helpers.openDetailPage
import com.waffiq.divenzz.utils.Helpers.setStatusBarColor

class PastEventFragment : Fragment() {

  private var _binding: FragmentPastEventBinding? = null
  private val binding get() = _binding!!

  private val viewModel by viewModels<PastEventViewModel>()

  private lateinit var eventAdapter: EventAdapter

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    _binding = FragmentPastEventBinding.inflate(inflater, container, false)
    val root: View = binding.root

    setupRecyclerView()
    getNotes()
    btnAction()

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

  private fun getNotes() {
    // observe loading states
    viewModel.isLoading.observe(viewLifecycleOwner) {
      binding.loading.progressCircular.isVisible = it
    }

    // observe error states
    viewModel.snackBarText.observe(viewLifecycleOwner) {
      if (it.isNotEmpty()) {
        binding.error.root.isVisible = true
        binding.rvEvents.isVisible = false
      } else {
        binding.error.root.isVisible = false
        binding.rvEvents.isVisible = true
      }
      binding.error.tvErrorMessage.text = it
    }

    // observe data
    viewModel.events.observe(viewLifecycleOwner) {
      eventAdapter.setEvent(it)
    }
  }

  private fun btnAction() {
    binding.error.btnTryAgain.setOnClickListener {
      viewModel.getPastEvent()
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}