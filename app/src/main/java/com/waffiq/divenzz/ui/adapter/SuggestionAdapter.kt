package com.waffiq.divenzz.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class SuggestionAdapter(
  private var items: List<String>,
  private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<SuggestionAdapter.ViewHolder>() {

  fun updateSuggestions(newItems: List<String>) {
    val diffCallback = DiffCallback(this.items, newItems)
    val diffResult = DiffUtil.calculateDiff(diffCallback)
    diffResult.dispatchUpdatesTo(this)
  }

  class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val textView: TextView = view.findViewById(android.R.id.text1)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = LayoutInflater.from(parent.context)
      .inflate(android.R.layout.simple_list_item_1, parent, false)
    return ViewHolder(view)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val item = items[position]
    holder.textView.text = item
    holder.itemView.setOnClickListener { onItemClick(item) }
  }

  override fun getItemCount() = items.size

  inner class DiffCallback(
    private val oldList: List<String>,
    private val newList: List<String>,
  ) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
      oldList[oldItemPosition] == newList[newItemPosition]

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
      oldList[oldItemPosition] == newList[newItemPosition]
  }
}
