package com.waffiq.divenzz.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat.Type
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.waffiq.divenzz.R.drawable.ic_favorite
import com.waffiq.divenzz.R.drawable.ic_favorite_filled
import com.waffiq.divenzz.R.drawable.ic_image_error_wide
import com.waffiq.divenzz.R.drawable.ic_image_placeholder
import com.waffiq.divenzz.R.plurals.quota_left
import com.waffiq.divenzz.R.string.added_to_favorite
import com.waffiq.divenzz.R.string.removed_from_favorite
import com.waffiq.divenzz.core.data.database.EventEntity
import com.waffiq.divenzz.core.data.datastore.SettingPreferences
import com.waffiq.divenzz.core.data.datastore.dataStore
import com.waffiq.divenzz.core.data.remote.response.EventResponse
import com.waffiq.divenzz.databinding.ActivityDetailBinding
import com.waffiq.divenzz.ui.favorite.FavoriteViewModel
import com.waffiq.divenzz.ui.viewmodel.ViewModelFactory
import com.waffiq.divenzz.utils.Helpers.convertToReadableDateTimeCompat
import com.waffiq.divenzz.utils.Helpers.handleOverHeightAppBar
import io.noties.markwon.Markwon
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.glide.GlideImagesPlugin
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class DetailActivity : AppCompatActivity() {

  private lateinit var binding: ActivityDetailBinding
  private var eventId by Delegates.notNull<Int>()

  private val viewModel by viewModels<DetailEventViewModel>()
  private lateinit var favViewModel: FavoriteViewModel

  private var isFavorite = false

  private lateinit var event: EventResponse

  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)

    binding = ActivityDetailBinding.inflate(layoutInflater)
    setContentView(binding.root)

    binding.appBarLayout.handleOverHeightAppBar()
    ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
      val systemBars = insets.getInsets(Type.systemBars())
      binding.splitFab.updateLayoutParams<ViewGroup.MarginLayoutParams> {
        leftMargin = systemBars.left
        bottomMargin = systemBars.bottom + 32
        rightMargin = systemBars.right + 32
      }
      insets
    }
    ViewCompat.requestApplyInsets(binding.root)

    val pref = SettingPreferences.getInstance(this.dataStore)
    favViewModel = ViewModelProvider(
      this,
      ViewModelFactory(this.application, pref)
    )[FavoriteViewModel::class.java]

    if (getDataExtra()) {
      getDetailEvent(eventId)

      binding.swipeRefresh.setOnRefreshListener {
        getDetailEvent(eventId)
        binding.swipeRefresh.isRefreshing = false
      }
      observeFavoriteState(eventId)
    }
    btnAction()
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
      binding.btnRegister.isVisible = !it
    }

    // observe error states
    viewModel.snackBarText.observe(this) {
      val isError = it.isNotEmpty()
      binding.container.isVisible = !isError
      binding.btnRegister.isVisible = !isError
      binding.error.root.isVisible = isError
      binding.error.tvErrorMessage.text = it
      binding.splitFab.isVisible = !isError
    }

    // observe event data
    viewModel.event.observe(this) { event ->
      if (event != null) {
        this.event = event

        Glide.with(binding.ivPicture)
          .load(event.mediaCover)
          .placeholder(ic_image_placeholder)
          .transition(withCrossFade())
          .error(ic_image_error_wide)
          .into(binding.ivPicture)

        val left = (event.quota ?: 0) - (event.registrants ?: 0)
        val quota = resources.getQuantityString(
          quota_left,
          left,
          event.registrants ?: 0,
          event.quota ?: 0,
          left
        )
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

        binding.btnRegister.setOnClickListener {
          startActivity(Intent(Intent.ACTION_VIEW, event.link?.toUri()))
        }
      }
    }
  }

  private fun btnAction() {
    binding.btnBack.setOnClickListener {
      onBackPressedDispatcher.onBackPressed()
    }

    binding.btnFavorite.setOnClickListener {
      val eventEntity = EventEntity(
        eventName = event.name,
        eventId = event.id,
        imageUrl = event.mediaCover
      )
      if (isFavorite) {
        favViewModel.delete(eventEntity)
        Toast.makeText(this, getString(removed_from_favorite), Toast.LENGTH_SHORT).show()
      } else {
        favViewModel.insert(eventEntity)
        Toast.makeText(this, getString(added_to_favorite), Toast.LENGTH_SHORT).show()
      }
      isFavorite = !isFavorite

      val iconRes = if (isFavorite) ic_favorite_filled else ic_favorite
      binding.ivFavoriteIcon.setImageResource(iconRes)
    }
  }

  private fun observeFavoriteState(eventId: Int) {
    lifecycleScope.launch {
      favViewModel.isFavorite(eventId).collect {
        isFavorite = it
        val iconRes = if (isFavorite) ic_favorite_filled else ic_favorite
        binding.ivFavoriteIcon.setImageResource(iconRes)
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
