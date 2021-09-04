package com.carrot.gallery.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.carrot.gallery.SharedViewModel
import com.carrot.gallery.core.image.ImageUrlMaker
import com.carrot.gallery.core.util.ScreenUtility
import com.carrot.gallery.databinding.FragmentGalleryBinding
import com.carrot.gallery.ui.BaseAdapter
import com.carrot.gallery.ui.ItemBinder
import com.carrot.gallery.ui.ItemClass
import com.carrot.gallery.util.observeOnce
import com.carrot.gallery.widget.GridLoadMoreListener
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.internal.toImmutableList
import javax.inject.Inject


@AndroidEntryPoint
class GalleryFragment : Fragment() {
    private lateinit var binding: FragmentGalleryBinding
    private val viewModel: GalleryViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var galleryAdapter: BaseAdapter? = null

    @Inject
    lateinit var imageUrlMaker: ImageUrlMaker


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
        initView()
        initViewModel()
    }

    private fun initView() {
        @Suppress("UNCHECKED_CAST")
        val viewBinders = HashMap<ItemClass, ItemBinder>()
        val imageViewBinder = GallerySimpleImageItemBinder(viewModel, imageUrlMaker)
        viewBinders[imageViewBinder.modelClass] = imageViewBinder as ItemBinder
        galleryAdapter = BaseAdapter(viewBinders)
        binding.galleryRecyclerview.apply {
            adapter = galleryAdapter
            layoutManager = GridLayoutManager(context, GalleryCons.COLUMN_COUNT)
            addOnScrollListener(object : GridLoadMoreListener() {
                override fun onLoadMore() {
                    viewModel.onReceiveLoadMoreSignal()
                }
            })
        }
    }

    private fun initViewModel() {
        viewModel.images.observe(viewLifecycleOwner, { images ->
            sharedViewModel.onUpdateImagesAtGallery(images)
        })

        viewModel.imageViewDataList.observe(viewLifecycleOwner, { images ->
            addToGallery(binding.galleryRecyclerview, images)
            binding.refreshLayout.isRefreshing = false
        })

        viewModel.errorViewShown.observe(viewLifecycleOwner, {
            binding.refreshLayout.isRefreshing = false
        })

        viewModel.emptyViewShown.observe(viewLifecycleOwner, {
            binding.refreshLayout.isRefreshing = false
        })

        viewModel.observeSingleEvent(viewLifecycleOwner) {
            when (it) {
                is GallerySingleEventType.GoToSimpleImageViewer -> {
                    val direction = GalleryFragmentDirections.toImageViewer(it.position)
                    findNavController().navigate(direction)
                }
            }
        }

        binding.viewModel = viewModel

        sharedViewModel.selectedPageFromImageViewer.observeOnce(viewLifecycleOwner, { position ->
            position?.let {
                (binding.galleryRecyclerview.layoutManager as? GridLayoutManager)
                    ?.scrollToPositionWithOffset(position, (ScreenUtility.getScreenHeight(context) * 2 / 5f).toInt())
            }
        })
    }

    @Suppress("UNCHECKED_CAST")
    private fun addToGallery(recyclerView: RecyclerView, list: List<GalleryImageItemViewData>?) {
        (recyclerView.adapter as BaseAdapter).submitList(list?.toImmutableList() ?: emptyList())

        /**
         * [ 2021.09.04 테스트 결과 (Galaxy S9) ]
         * RecyclerView 가 submit 이후 레이아웃 최종 그릴 때까지
         * 1. cold start : 1.9s ~ 2.1s
         * 2. hot start : 500ms ~ 600ms
         *
         * [ 참고 ]
         * Android vitals 기준 위험 경계
         * 1. cold start : 5s 이상
         * 2. hot start : 1.5s 이상
         */
        recyclerView.doOnLayout {
            activity?.reportFullyDrawn()
        }
    }


}