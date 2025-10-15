package com.waffiq.divenzz.ui.adapter

import android.R.drawable.ic_menu_gallery
import android.R.drawable.stat_notify_error
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.waffiq.divenzz.core.data.remote.response.EventResponse
import com.waffiq.divenzz.databinding.ItemSmallEventBinding

class SmallEventAdapter(
  private val onClick: (EventResponse) -> Unit,
) : RecyclerView.Adapter<SmallEventAdapter.ViewHolder>() {

  private val listEvent = ArrayList<EventResponse>()
  private lateinit var binding: ItemSmallEventBinding

  fun setEvent(eventItem: List<EventResponse>) {
    val limitedList = eventItem.take(5)
    val diffCallback = DiffCallback(this.listEvent, limitedList)
    val diffResult = DiffUtil.calculateDiff(diffCallback)

    this.listEvent.clear()
    this.listEvent.addAll(limitedList)
    diffResult.dispatchUpdatesTo(this)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    binding = ItemSmallEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return ViewHolder(binding)
  }

  inner class ViewHolder(private var binding: ItemSmallEventBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun onBind(event: EventResponse) {
      Glide.with(binding.root.context)
        .load(event.mediaCover)
        .placeholder(ic_menu_gallery)
        .error(stat_notify_error)
        .transform(CenterCrop())
        .transition(withCrossFade())
        .into(binding.ivEventImage)
      binding.tvEventName.text = event.name

      binding.root.setOnClickListener {
        onClick(event)
      }
    }
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.onBind(listEvent[position])
  }

  override fun getItemCount() = listEvent.size

  inner class DiffCallback(
    private val oldList: List<EventResponse>,
    private val newList: List<EventResponse>,
  ) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
      oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
      oldList[oldItemPosition].id == newList[newItemPosition].id
  }
}