package com.carrot.gallery.viewer

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.SeekBar
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
import com.carrot.gallery.core.domain.ImageCons
import com.carrot.gallery.databinding.FragmentImageViewerBinding
import dagger.hilt.android.AndroidEntryPoint


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
        initView()
        initViewModel()
        viewModel.onViewCreated(imageId)
    }

    private fun initView() {
        binding.imageViewerView.setOnPhotoTapListener { _, _, _ ->
            viewModel.onSingleTabImageEvent()
        }

        binding.bottomBarGrayscaleSwitch.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                viewModel.onChangeGrayscaleEffect(isChecked)
            }
        })

        binding.bottomBarBlurSeekbar.max = ImageCons.BLUR_FILTER_MAX_VALUE
        binding.bottomBarBlurSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.onChangeBlurEffect(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

    }

    private fun initViewModel() {
        viewModel.image.observe(viewLifecycleOwner, { image ->
            binding.image = image
            binding.executePendingBindings()
        })

        viewModel.imageUrl.observe(viewLifecycleOwner) { url ->
            viewModel.onStartLoadImageToView()

            Glide.with(binding.imageViewerView.context)
                .load(url)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        viewModel.onFailureLoadImageToView()
                        return true
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        viewModel.onSuccessLoadImageToView()
                        return false
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .transition(DrawableTransitionOptions.withCrossFade(100))
                .into(binding.imageViewerView)
        }


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