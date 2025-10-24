package com.waffiq.divenzz.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.waffiq.divenzz.R.dimen.bottom_nav_height
import com.waffiq.divenzz.R.dimen.short_margin
import com.waffiq.divenzz.R.string.no_past_events_found
import com.waffiq.divenzz.core.data.remote.response.EventResponse
import com.waffiq.divenzz.databinding.FragmentHomeBinding
import com.waffiq.divenzz.ui.adapter.EventAdapter
import com.waffiq.divenzz.ui.adapter.SmallEventAdapter
import com.waffiq.divenzz.ui.past.PastEventViewModel
import com.waffiq.divenzz.ui.state.EventUiState
import com.waffiq.divenzz.ui.upcoming.UpcomingEventViewModel
import com.waffiq.divenzz.ui.viewmodel.ViewModelFactory
import com.waffiq.divenzz.utils.Helpers.openDetailPage
import com.waffiq.divenzz.utils.Helpers.openFavoritePage

class HomeFragment : Fragment() {

  private var _binding: FragmentHomeBinding? = null
  private val binding get() = _binding!!

  private lateinit var viewModelUpcoming: UpcomingEventViewModel
  private lateinit var viewModelPast: PastEventViewModel

  private lateinit var eventAdapterVertical: SmallEventAdapter
  private lateinit var eventAdapterHorizontal: EventAdapter

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    _binding = FragmentHomeBinding.inflate(inflater, container, false)
    val root: View = binding.root

    ViewCompat.setOnApplyWindowInsetsListener(binding.btnOpenFavorite) { view, insets ->
      val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
      val navBarHeight = systemBars.bottom
      val bottomNavHeight = resources.getDimensionPixelSize(bottom_nav_height)

      (view.layoutParams as CoordinatorLayout.LayoutParams).bottomMargin =
        navBarHeight + bottomNavHeight + resources.getDimensionPixelSize(short_margin)

      view.requestLayout()
      insets
    }

    val factory = ViewModelFactory.getInstance(requireActivity())
    viewModelUpcoming = ViewModelProvider(this, factory)[UpcomingEventViewModel::class.java]
    viewModelPast = ViewModelProvider(this, factory)[PastEventViewModel::class.java]

    setupRecyclerView()
    getEvents()
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

  private fun getEvents() {
    // observe loading states
    viewModelUpcoming.uiState.observe(viewLifecycleOwner) { state ->
      when (state) {
        is EventUiState.Loading -> {
          binding.divider1.isVisible = false
          binding.divider2.isVisible = false
          binding.tvUpcomingEvents.isVisible = false
          binding.tvPastEvents.isVisible = false
          binding.loading.progressCircular.isVisible = true
          binding.error.root.isVisible = false
          binding.rvEventsHorizontal.isVisible = false
        }

        is EventUiState.Success -> {
          binding.divider1.isVisible = true
          binding.divider2.isVisible = true
          binding.tvUpcomingEvents.isVisible = true
          binding.tvPastEvents.isVisible = true
          binding.loading.progressCircular.isVisible = false
          binding.error.root.isVisible = false
          binding.rvEventsHorizontal.isVisible = true
          eventAdapterHorizontal.setEvent(state.events)
        }

        is EventUiState.Error -> {
          binding.divider1.isVisible = false
          binding.divider2.isVisible = false
          binding.tvUpcomingEvents.isVisible = false
          binding.tvPastEvents.isVisible = false
          binding.loading.progressCircular.isVisible = false
          binding.error.root.isVisible = true
          binding.rvEventsHorizontal.isVisible = false
        }

        is EventUiState.Empty -> {
          binding.tvUpcomingEvents.isVisible = false
          binding.tvPastEvents.isVisible = false
          binding.loading.progressCircular.isVisible = false
          binding.error.root.isVisible = true
          binding.rvEventsHorizontal.isVisible = false
          binding.error.tvErrorMessage.text = getString(no_past_events_found)
        }
      }
    }
    viewModelPast.uiState.observe(viewLifecycleOwner) { state ->
      when (state) {
        is EventUiState.Loading -> {}
        is EventUiState.Success -> {
          binding.rvEventsVertical.isVisible = true
          eventAdapterVertical.setEvent(state.events)
        }

        is EventUiState.Error -> {}
        is EventUiState.Empty -> {}
      }
    }
  }

  private fun btnAction() {
    binding.error.btnTryAgain.setOnClickListener {
      viewModelPast.getPastEvents()
      viewModelUpcoming.getUpcomingEvents()
    }

    binding.btnOpenFavorite.setOnClickListener {
      requireActivity().openFavoritePage()
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}