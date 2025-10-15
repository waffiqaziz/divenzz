package com.waffiq.divenzz.ui.detail

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.waffiq.divenzz.R.drawable.ic_image_error_wide
import com.waffiq.divenzz.R.drawable.ic_image_placeholder
import com.waffiq.divenzz.databinding.ActivityDetailBinding
import com.waffiq.divenzz.utils.Helpers.convertToReadableDateTimeCompat
import io.noties.markwon.Markwon
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.glide.GlideImagesPlugin
import kotlin.properties.Delegates

class DetailActivity : AppCompatActivity() {

  private lateinit var binding: ActivityDetailBinding
  private var eventId by Delegates.notNull<Int>()

  private val viewModel by viewModels<DetailEventViewModel>()

  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)

    binding = ActivityDetailBinding.inflate(layoutInflater)
    setContentView(binding.root)

    binding.btnBack.setOnClickListener {
      onBackPressedDispatcher.onBackPressed()
    }

    if (getDataExtra()) {
      getDetailEvent(eventId)

      binding.swipeRefresh.setOnRefreshListener {
        getDetailEvent(eventId)
        binding.swipeRefresh.isRefreshing = false
      }
    }
  }

  private fun getDataExtra(): Boolean {
    if (!intent.hasExtra(EVENT_ID)) {
      finish()
      return false
    }

    eventId = intent.getIntExtra(EVENT_ID, -1)
    return true
  }

  private fun getDetailEvent(eventId: Int) {
    viewModel.getDetailEvent(eventId)

    // observe loading states
    viewModel.isLoading.observe(this) {
      binding.loading.progressCircular.isVisible = it
      binding.container.isVisible = !it
      binding.layoutDetail.isVisible = !it
      binding.btnOpenLink.isVisible = !it
    }

    // observe error states
    viewModel.snackBarText.observe(this) {
      val isError = it.isNotEmpty()
      binding.container.isVisible = !isError
      binding.btnOpenLink.isVisible = !isError
      binding.error.root.isVisible = isError
      binding.error.tvErrorMessage.text = it
    }

    // observe event data
    viewModel.event.observe(this) { event ->
      if (event != null) {

        Glide.with(binding.ivPicture)
          .load(event.mediaCover)
          .placeholder(ic_image_placeholder)
          .transition(withCrossFade())
          .error(ic_image_error_wide)
          .into(binding.ivPicture)

        val quota = "${event.registrants} / ${event.quota}"
        val date = convertToReadableDateTimeCompat(event.beginTime, event.endTime)
        val owner = event.ownerName + ", "

        binding.tvOwnerName.text = owner
        binding.tvEventType.text = event.category
        binding.tvEventName.text = event.name
        binding.tvDateDay.text = date.first
        binding.tvTime.text = date.second
        binding.tvPlace.text = event.cityName
        binding.tvQuota.text = quota


        val decodedHtml = event.description?.replace("\\u003C", "<")
          ?.replace("\\u003E", ">")
          ?.replace("\\u0026", "&").toString()

        val markwon = Markwon.builder(this)
          .usePlugin(HtmlPlugin.create())
          .usePlugin(GlideImagesPlugin.create(this))
          .build()

        markwon.setMarkdown(binding.tvDescription, decodedHtml)


        binding.btnOpenLink.setOnClickListener {
          startActivity(Intent(Intent.ACTION_VIEW, event.link?.toUri()))
        }
      }
    }
  }

  override fun onSupportNavigateUp(): Boolean {
    onBackPressedDispatcher.onBackPressed()
    return true
  }

  companion object {
    const val EVENT_ID = "EVENT_ID"
  }
}
