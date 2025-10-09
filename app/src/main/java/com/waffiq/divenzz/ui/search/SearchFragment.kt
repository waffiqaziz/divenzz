package com.waffiq.divenzz.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.waffiq.divenzz.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

  private var _binding: FragmentSearchBinding? = null
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    val viewModel = ViewModelProvider(this)[SearchViewModel::class.java]

    _binding = FragmentSearchBinding.inflate(inflater, container, false)
    val root: View = binding.root

    val textView: TextView = binding.textSearch
    viewModel.text.observe(viewLifecycleOwner) {
      textView.text = it
    }
    return root
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}