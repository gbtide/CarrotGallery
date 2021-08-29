package com.carrot.gallery.viewer

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.carrot.gallery.core.image.ThumbnailUrlMaker
import com.carrot.gallery.databinding.FragmentImageViewerBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import javax.inject.Inject


/**
 * Created by kyunghoon on 2021-01
 */
@AndroidEntryPoint
class ImageViewerFragment : Fragment() {

    companion object {
        const val IMAGE_ID = "imageId"
    }

    private var imageId: Long = -1L
    private lateinit var binding: FragmentImageViewerBinding
    private val viewModel: ImageViewerViewModel by viewModels()

    @Inject
    lateinit var thumbnailUrlMaker: ThumbnailUrlMaker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imageId = it.getSerializable(IMAGE_ID) as Long
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentImageViewerBinding.inflate(inflater, container, false)
            .apply {
                lifecycleOwner = viewLifecycleOwner
            }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        viewModel.onViewCreated(imageId)
    }

    private fun initViewModel() {
        viewModel.image.observe(viewLifecycleOwner, { image ->
            try {
                viewModel.onStartImageLoad()
                val newUrl = thumbnailUrlMaker.addParamToUrl(
                    requireContext(),
                    image.downloadUrl,
                    image.width,
                    image.height
                )
                Timber.d("### detail > resizedUrl : " + newUrl + "_" + image.width + image.height)
                Glide.with(requireContext())
                    .load(newUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .transition(DrawableTransitionOptions.withCrossFade(100))
                    .listener(object : RequestListener<Drawable?> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable?>?, isFirstResource: Boolean
                        ): Boolean {
                            viewModel.onImageLoadFailed()
                            return true
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable?>?, dataSource: DataSource?, isFirstResource: Boolean
                        ): Boolean {
                            viewModel.onImageResourceReady()
                            return false
                        }
                    })
                    .into(binding.imageViewerView)

            } catch (e: Throwable) {
                Timber.e(e, "Failed to load image")
            }

        })

        viewModel.observeSingleEvent(viewLifecycleOwner) {
            when (it) {
                is ImageViewerSingleEventType.ClickCloseButton -> {
                    findNavController().popBackStack()
                }
            }
        }

        binding.viewModel = viewModel
    }

}