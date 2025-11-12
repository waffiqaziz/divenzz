package com.waffiq.divenzz.ui.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.search.SearchView
import com.waffiq.divenzz.R.string.no_results_found_for
import com.waffiq.divenzz.R.string.please_enter_a_search_query
import com.waffiq.divenzz.core.data.remote.response.EventResponse
import com.waffiq.divenzz.databinding.FragmentSearchBinding
import com.waffiq.divenzz.ui.adapter.EventAdapter
import com.waffiq.divenzz.ui.adapter.SuggestionAdapter
import com.waffiq.divenzz.ui.viewmodel.ViewModelFactory
import com.waffiq.divenzz.utils.Helpers.openDetailPage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

  private var _binding: FragmentSearchBinding? = null
  private val binding get() = _binding ?: error("FragmentSearchBinding is null")

  private lateinit var viewModel: SearchViewModel

  private lateinit var eventAdapter: EventAdapter
  private lateinit var suggestionAdapter: SuggestionAdapter

  private var searchJob: Job? = null

  private val suggestions = listOf(
    "android",
    "ai",
    "backend",
    "devcoach",
    "flutter",
    "frontend",
  )

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    _binding = FragmentSearchBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val factory = ViewModelFactory.getInstance(requireActivity())
    viewModel = ViewModelProvider(this, factory)[SearchViewModel::class.java]

    setupAdapters()
    setupRecyclerViews()
    setupSearchView()
    observeViewModel()
    btnTryAgain()

    // double click search icon in bottom nav to open search view
    requireActivity().supportFragmentManager.setFragmentResultListener(
      "open_search_view",
      viewLifecycleOwner
    ) { _, _ ->
      openSearchView()
    }
  }

  private fun setupAdapters() {
    eventAdapter = EventAdapter(::onClick)

    suggestionAdapter = SuggestionAdapter(suggestions) { suggestion ->
      performSearch(suggestion)
      binding.searchView.hide()
    }
  }

  private fun setupSearchView() {
    binding.searchBar.setOnClickListener {
      binding.searchView.show()
    }

    binding.searchView.editText.doOnTextChanged { text, _, _, _ ->
      searchJob?.cancel()

      if (text.isNullOrBlank()) {
        updateSuggestions(suggestions)
        return@doOnTextChanged
      }

      searchJob = viewLifecycleOwner.lifecycleScope.launch {
        delay(300)
        filterSuggestions(text.toString())
      }
    }

    // handle search submit
    binding.searchView.editText.setOnEditorActionListener { textView, _, _ ->
      val query = textView.text.toString()
      if (query.isNotBlank()) {
        performSearch(query)
        binding.searchView.hide()
        true
      } else {
        false
      }
    }

    // handle back button in SearchView
    binding.searchView.addTransitionListener { _, _, newState ->
      if (newState == SearchView.TransitionState.HIDDEN) {
        binding.searchView.editText.text?.clear()
      }
    }
  }

  private fun setupRecyclerViews() {
    binding.rvSuggestions.apply {
      layoutManager = LinearLayoutManager(requireContext())
      adapter = suggestionAdapter
    }

    binding.rvSearchResults.apply {
      layoutManager = LinearLayoutManager(requireContext())
      adapter = eventAdapter
    }
  }

  private fun observeViewModel() {
    viewModel.uiState.observe(viewLifecycleOwner) { state ->
      when (state) {
        is SearchUiState.Initial -> {
          binding.apply {
            progressIndicator.isVisible = false
            searchTextContainer.isVisible = true
            rvSearchResults.isVisible = false
            emptyState.root.isVisible = false
            error.root.isVisible = false
          }
        }

        is SearchUiState.Loading -> {
          binding.apply {
            progressIndicator.isVisible = true
            searchTextContainer.isVisible = false
            rvSearchResults.isVisible = false
            emptyState.root.isVisible = false
            error.root.isVisible = false
          }
        }

        is SearchUiState.Success -> {
          binding.apply {
            progressIndicator.isVisible = false
            searchTextContainer.isVisible = false
            rvSearchResults.isVisible = true
            emptyState.root.isVisible = false
            error.root.isVisible = false
          }
          eventAdapter.setEvent(state.events)
        }

        is SearchUiState.Empty -> {
          binding.apply {
            progressIndicator.isVisible = false
            searchTextContainer.isVisible = false
            rvSearchResults.isVisible = false
            emptyState.root.isVisible = true
            error.root.isVisible = false
            emptyState.tvEmptyStateDescription.text =
              getString(no_results_found_for, state.query)
          }
        }

        is SearchUiState.Error -> {
          binding.apply {
            progressIndicator.isVisible = false
            searchTextContainer.isVisible = false
            rvSearchResults.isVisible = false
            emptyState.root.isVisible = false
            error.root.isVisible = true
          }
        }
      }
    }
  }

  private fun filterSuggestions(query: String) {
    val filtered = suggestions.filter {
      it.contains(query, ignoreCase = true)
    }
    updateSuggestions(filtered)
  }

  private fun updateSuggestions(items: List<String>) {
    suggestionAdapter.updateSuggestions(items)
  }

  private fun performSearch(query: String) {
    binding.apply {
      emptyState.root.isVisible = false
      rvSearchResults.isVisible = false
      searchBar.textView.text = query
    }

    viewModel.search(query)
  }

  private fun onClick(event: EventResponse) {
    requireActivity().openDetailPage(event.id)
  }

  private fun btnTryAgain() {
    binding.error.btnTryAgain.setOnClickListener {
      val query = binding.searchBar.text.toString()
      if (query.isNotBlank()) {
        performSearch(query)
        binding.searchView.hide()
      } else {
        Toast.makeText(
          requireActivity(),
          getString(please_enter_a_search_query), Toast.LENGTH_SHORT
        ).show()
      }
    }
  }

  private fun openSearchView() {
    binding.apply {
      searchView.show()

      searchView.editText.requestFocus()
      searchView.editText.postDelayed({
        val imm =
          requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(searchView.editText, InputMethodManager.SHOW_IMPLICIT)
      }, 100)
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    searchJob?.cancel()
    _binding = null
  }
}