package com.carrot.gallery.ui.viewer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.carrot.gallery.SharedViewModel
import com.carrot.gallery.core.domain.ImageCons
import com.carrot.gallery.core.image.ImageUrlMaker
import com.carrot.gallery.databinding.FragmentImageViewerBinding
import com.carrot.gallery.ui.BaseAdapter
import com.carrot.gallery.ui.ItemBinder
import com.carrot.gallery.ui.ItemClass
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.internal.toImmutableList
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kyunghoon on 2021-01
 */
@AndroidEntryPoint
class ImageViewerFragment : Fragment() {
    private var position: Int = 0

    private lateinit var binding: FragmentImageViewerBinding
    private val viewModel: ImageViewerViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var imageViewerAdapter: BaseAdapter? = null
    private var imageViewBinder: ImageViewerSimpleImageItemBinder? = null

    @Inject
    lateinit var imageUrlMaker: ImageUrlMaker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            position = ImageViewerFragmentArgs.fromBundle(it).position
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
        viewModel.onViewCreated(position)
    }

    private fun initView() {
        binding.imageViewerViewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                viewModel.onChangePage(position)
            }
        })
        binding.bottomBarGrayscaleSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onChangeGrayscaleEffect(isChecked)
        }

        binding.bottomBarBlurSeekbar.max = ImageCons.BLUR_FILTER_MAX_VALUE
        binding.bottomBarBlurSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                viewModel.onChangeBlurEffect(binding.bottomBarBlurSeekbar.progress)
            }
        })

    }

    private fun initViewModel() {
        sharedViewModel.sharedList.observe(viewLifecycleOwner, { images ->
            viewModel.onInitImages(images)
            sharedViewModel.sharedList.removeObservers(viewLifecycleOwner)
        })

        viewModel.images.observe(viewLifecycleOwner, { images ->
            sharedViewModel.onUpdateListAtImageViewer(images)
        })

        viewModel.imageViewDataList.observe(viewLifecycleOwner, { images ->
            initViewPager(binding.imageViewerViewPager, images)
        })

        viewModel.observeSingleEvent(viewLifecycleOwner) {
            when (it) {
                is ImageViewerSingleEventType.ClickCloseButton -> {
                    findNavController().popBackStack()
                }
                is ImageViewerSingleEventType.ClickMoreButton -> {
                    // TODO custom tab 변경
                    // https://developer.chrome.com/docs/android/custom-tabs/integration-guide/
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.url)))
                }
                is ImageViewerSingleEventType.ReloadImage -> {
                    binding.imageViewerViewPager.adapter?.notifyItemChanged(it.page)
                }
            }
        }

        binding.viewModel = viewModel
    }

    private fun initViewPager(viewPager: ViewPager2, list: List<ImageViewerViewData>?) {
        addToViewPager(viewPager, list)
        viewPager.setCurrentItem(position, false)
    }

    @Suppress("UNCHECKED_CAST")
    private fun addToViewPager(viewPager: ViewPager2, list: List<ImageViewerViewData>?) {
        if (imageViewerAdapter == null) {
            val viewBinders = HashMap<ItemClass, ItemBinder>()
            imageViewBinder = ImageViewerSimpleImageItemBinder(viewModel, imageUrlMaker)
            viewBinders[imageViewBinder!!.modelClass] = imageViewBinder as ItemBinder
            imageViewerAdapter = BaseAdapter(viewBinders)
        }
        if (viewPager.adapter == null) {
            viewPager.apply {
                adapter = imageViewerAdapter
            }
        }
        (viewPager.adapter as BaseAdapter).submitList(list?.toImmutableList() ?: emptyList())
    }

}