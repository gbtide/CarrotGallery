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
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.carrot.gallery.ui.SharedViewModel
import com.carrot.gallery.core.domain.ImageCons
import com.carrot.gallery.core.image.ImageUrlMaker
import com.carrot.gallery.core.util.toImmutableList
import com.carrot.gallery.databinding.FragmentImageViewerBinding
import com.carrot.gallery.ui.BaseAdapter
import com.carrot.gallery.ui.ItemBinder
import com.carrot.gallery.ui.ItemClass
import com.carrot.gallery.util.observeOnce
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kyunghoon on 2021-01
 */
@AndroidEntryPoint
class ImageViewerFragment : Fragment() {

    companion object {
        // [ config change 대응 ]
        // 고민 : ARG_KEY_POSITION 를 안쓰고 nav_graph.xml 의 android:name 을 쓰고 싶었으나,
        // @string 으로는 해결이 안되는 것 같습니다. 고민 고민..
        const val ARG_KEY_POSITION = "position"
    }

    private val args : ImageViewerFragmentArgs by navArgs()

    private lateinit var binding: FragmentImageViewerBinding
    private val viewModel: ImageViewerViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var imageViewerAdapter: BaseAdapter? = null

    @Inject
    lateinit var imageUrlMaker: ImageUrlMaker


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
    }

    private fun initView() {
        binding.imageViewerViewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                viewModel.onPageSelected(position)
                sharedViewModel.onPageSelectedAtImageViewer(position)
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
        sharedViewModel.galleryImagesFromGallery.observeOnce(viewLifecycleOwner, { images ->
            viewModel.onReceiveImagesFromGallery(images)
        })

        viewModel.imageViewDataList.observe(viewLifecycleOwner, { images ->
            addToViewPager(binding.imageViewerViewPager, images)
        })

        viewModel.observeSingleEvent(viewLifecycleOwner) {
            when (it) {
                is ImageViewerSingleEventType.ClickCloseButton -> {
                    findNavController().popBackStack()
                }
                is ImageViewerSingleEventType.ClickMoreButton -> {
                    // TODO 시간되면 custom tab 으로 변경
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

    @Suppress("UNCHECKED_CAST")
    private fun addToViewPager(viewPager: ViewPager2, list: List<ImageViewerViewData>?) {
        val firstInit = imageViewerAdapter == null
        if (firstInit) {
            val viewBinders = HashMap<ItemClass, ItemBinder>()
            val imageViewBinder = ImageViewerSimpleImageItemBinder(viewModel, imageUrlMaker)
            viewBinders[imageViewBinder.modelClass] = imageViewBinder as ItemBinder
            imageViewerAdapter = BaseAdapter(viewBinders)
            viewPager.apply {
                adapter = imageViewerAdapter
            }
        }
        (viewPager.adapter as BaseAdapter).submitList(list?.toImmutableList() ?: emptyList())

        if (firstInit) {
            viewPager.setCurrentItem(args.position, false)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        arguments?.let {
            it.putInt(ARG_KEY_POSITION, binding.imageViewerViewPager.currentItem)
        }
    }

}