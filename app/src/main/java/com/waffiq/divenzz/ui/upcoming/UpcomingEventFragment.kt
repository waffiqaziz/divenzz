package com.waffiq.divenzz.ui.upcoming

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.waffiq.divenzz.core.data.remote.response.EventsItem
import com.waffiq.divenzz.databinding.FragmentUpcomingEventBinding
import com.waffiq.divenzz.ui.adapter.EventAdapter

class UpcomingEventFragment : Fragment() {

  private var _binding: FragmentUpcomingEventBinding? = null
  private val binding get() = _binding!!

  private val viewModel by viewModels<UpcomingEventViewModel>()

  private lateinit var eventAdapter: EventAdapter

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    _binding = FragmentUpcomingEventBinding.inflate(inflater, container, false)
    val root: View = binding.root

    setupRecyclerView()
    getNotes()

    return root
  }

  private fun setupRecyclerView() {
    eventAdapter = EventAdapter(::onClick)
    binding.rvEvents.layoutManager =
      LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    binding.rvEvents.adapter = eventAdapter
  }

  private fun onClick(event: EventsItem) {
    Toast.makeText(context, event.name, Toast.LENGTH_SHORT).show()
  }

  private fun getNotes() {
    viewModel.events.observe(viewLifecycleOwner) {
      eventAdapter.setEvent(it)
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}