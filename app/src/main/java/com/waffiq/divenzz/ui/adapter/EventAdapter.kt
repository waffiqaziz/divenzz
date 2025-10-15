package com.waffiq.divenzz.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.waffiq.divenzz.R.drawable.ic_image_error_wide
import com.waffiq.divenzz.R.drawable.ic_image_placeholder
import com.waffiq.divenzz.core.data.remote.response.EventResponse
import com.waffiq.divenzz.databinding.ItemEventBinding

class EventAdapter(
  private val onClick: (EventResponse) -> Unit,
) : RecyclerView.Adapter<EventAdapter.ViewHolder>() {

  private val listEvent = ArrayList<EventResponse>()
  private lateinit var binding: ItemEventBinding

  fun setEvent(eventItem: List<EventResponse>) {
    val diffCallback = DiffCallback(this.listEvent, eventItem)
    val diffResult = DiffUtil.calculateDiff(diffCallback)

    this.listEvent.clear()
    this.listEvent.addAll(eventItem)
    diffResult.dispatchUpdatesTo(this)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return ViewHolder(binding)
  }

  inner class ViewHolder(private var binding: ItemEventBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun onBind(event: EventResponse) {
      Glide.with(binding.root.context)
        .load(event.mediaCover)
        .placeholder(ic_image_placeholder)
        .error(ic_image_error_wide)
        .transition(withCrossFade())
        .into(binding.ivPicture)
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