package com.carrot.gallery.gallery

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.carrot.gallery.MainViewModel
import com.carrot.gallery.R
import com.carrot.gallery.core.image.ImageUrlMaker
import com.carrot.gallery.databinding.FragmentGalleryBinding
import com.carrot.gallery.viewer.ImageViewerFragment
import com.carrot.gallery.widget.GridLoadMoreListener
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.internal.toImmutableList
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class GalleryFragment : Fragment() {
    private lateinit var binding: FragmentGalleryBinding
    private val viewModel: GalleryViewModel by viewModels()
    private val mainActivityViewModel: MainViewModel by activityViewModels()
    private var galleryAdapter: GalleryAdapter? = null

    @Inject
    lateinit var imageUrlMaker: ImageUrlMaker

    /**
     * TODO 네비게이션 BACK 을 쓰면 layoutManagerState를 항상 관리해줘야하나..
     * 그렇지 않을 것 같다. 찾아보자.
     */
    private var layoutManagerState: Parcelable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGalleryBinding.inflate(inflater, container, false)
            .apply {
                lifecycleOwner = viewLifecycleOwner
            }
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        initViewModel()
    }

    private fun initViewModel() {
        viewModel.observeSingleEvent(viewLifecycleOwner) {
            when (it) {
                is GallerySingleEventType.GoToImageViewer -> {
                    val bundle = bundleOf(ImageViewerFragment.IMAGE_ID to it.image.id)
                    findNavController().navigate(R.id.to_image_viewer, bundle)
                }
            }
        }

        viewModel.images.observe(viewLifecycleOwner, { images ->
            addToGallery(binding.galleryRecyclerview, images)
        })

        binding.viewModel = viewModel
    }

    private fun addToGallery(recyclerView: RecyclerView, list: List<Any>?) {
        if (galleryAdapter == null) {
            val imageViewBinder = GalleryImageViewBinder(viewModel, imageUrlMaker)
            val viewBinders: HashMap<Class<out Any>, GalleryItemViewBinder<Any, RecyclerView.ViewHolder>> = HashMap()
            @Suppress("UNCHECKED_CAST")
            viewBinders[imageViewBinder.modelClass] = imageViewBinder as GalleryItemBinder
            galleryAdapter = GalleryAdapter(viewBinders)
        }
        if (recyclerView.adapter == null) {
            recyclerView.apply {
                adapter = galleryAdapter
                layoutManager = GridLayoutManager(context, GalleryCons.COLUMN_COUNT)
                addOnScrollListener(object : GridLoadMoreListener() {
                    override fun onLoadMore() {
                        viewModel.onReceiveLoadMoreSignal()
                    }
                })
            }
            layoutManagerState?.let {
                binding.galleryRecyclerview.layoutManager?.onRestoreInstanceState(layoutManagerState)
            }

        }
        (recyclerView.adapter as GalleryAdapter).submitList(list?.toImmutableList() ?: emptyList())

        // 체크!
        // After submitting the list to the adapter, the recycler view starts measuring and drawing
        // so let's wait for the layout to be drawn before reporting fully drawn.
        recyclerView.doOnLayout {
            // reportFullyDrawn() prints `I/ActivityTaskManager: Fully drawn {activity} {time}`
            // to logcat. The framework ensures that the statement is printed only once for the
            // activity, so there is no need to add dedupping logic to the app.
            activity?.reportFullyDrawn()
        }
    }

    override fun onStop() {
        super.onStop()
        layoutManagerState = binding.galleryRecyclerview.layoutManager?.onSaveInstanceState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}