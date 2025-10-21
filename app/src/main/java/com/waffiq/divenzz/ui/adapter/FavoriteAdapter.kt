package com.waffiq.divenzz.ui.adapter

import android.R.anim.fade_in
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.waffiq.divenzz.R.drawable.ic_image_error_wide
import com.waffiq.divenzz.R.drawable.ic_image_placeholder
import com.waffiq.divenzz.core.data.database.EventEntity
import com.waffiq.divenzz.databinding.ItemEventBinding

class FavoriteAdapter(
  private val onClick: (Int) -> Unit,
) :
  RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {

  private val listItemDB = ArrayList<EventEntity>()

  fun setFavorite(itemFavorite: List<EventEntity>) {
    val diffCallback = DiffCallback(this.listItemDB, itemFavorite)
    val diffResult = DiffUtil.calculateDiff(diffCallback)

    this.listItemDB.clear()
    this.listItemDB.addAll(itemFavorite)
    diffResult.dispatchUpdatesTo(this)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return ViewHolder(binding)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.bind(listItemDB[position])
    holder.itemView.startAnimation(
      AnimationUtils.loadAnimation(holder.itemView.context, fade_in)
    )
  }

  override fun getItemCount(): Int = listItemDB.size

  inner class ViewHolder(private var binding: ItemEventBinding) :
    RecyclerView.ViewHolder(binding.root) {
    lateinit var data: EventEntity

    fun bind(fav: EventEntity) {
      data = fav
      binding.tvEventName.text = fav.eventName

      Glide.with(binding.root.context)
        .load(fav.imageUrl)
        .placeholder(ic_image_placeholder)
        .error(ic_image_error_wide)
        .transition(withCrossFade())
        .into(binding.ivPicture)

      binding.root.setOnClickListener {
        onClick(data.eventId)
      }
    }
  }

  class DiffCallback(
    private val oldList: List<EventEntity>,
    private val newList: List<EventEntity>,
  ) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
      oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
      oldList[oldItemPosition].id == newList[newItemPosition].id
  }
}
