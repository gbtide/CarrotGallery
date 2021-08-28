package com.carrot.gallery.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.carrot.gallery.MainViewModel
import com.carrot.gallery.R
import com.carrot.gallery.databinding.FragmentGalleryBinding
import com.carrot.gallery.viewer.ImageViewerFragment
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.internal.toImmutableList
import timber.log.Timber


private const val ARG_SOME = "ARG_SOME"

@AndroidEntryPoint
class GalleryFragment : Fragment() {
//    private var galleryContentsType: GalleryContentsType? = null

    private lateinit var binding: FragmentGalleryBinding
    private var galleryAdapter: GalleryAdapter? = null

    private val viewModel: GalleryViewModel by viewModels()
    private val mainActivityViewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
//            galleryContentsType = it.getSerializable(ARG_SOME)
//                .convertTo(
//                GalleryContentsType::class.java,
//                GalleryContentsType.GALLERY
//            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
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
        viewModel.goToImageViewerAction.observe(viewLifecycleOwner, { image ->
            val bundle = bundleOf(ImageViewerFragment.IMAGE_ID to image.id)
            findNavController().navigate(R.id.to_image_viewer, bundle)
        })

        viewModel.imageList.observe(viewLifecycleOwner, { images ->
            Timber.d("### result : %s", images)
            showGallery(binding.recyclerviewGallery, images)

        })

        binding.viewModel = viewModel
    }

    private fun showGallery(recyclerView: RecyclerView, list: List<Any>?) {
        if (galleryAdapter == null) {
            val imageViewBinder = GalleryImageViewBinder(viewModel)
            val viewBinders: HashMap<Class<out Any>, GalleryItemViewBinder<Any, RecyclerView.ViewHolder>> = HashMap()
            viewBinders[imageViewBinder.modelClass] = imageViewBinder as GalleryItemBinder
            galleryAdapter = GalleryAdapter(viewBinders)
        }
        if (recyclerView.adapter == null) {
            recyclerView.also {
                it.adapter = galleryAdapter

                (it.itemAnimator as DefaultItemAnimator).run {
                    supportsChangeAnimations = false
                    addDuration = 160L
                    moveDuration = 160L
                    changeDuration = 160L
                    removeDuration = 120L
                }

                it.layoutManager = GridLayoutManager(context, GalleryCons.COLUMN_COUNT)
            }
        }
        (recyclerView.adapter as GalleryAdapter).submitList(list?.toImmutableList() ?: emptyList())

        // After submitting the list to the adapter, the recycler view starts measuring and drawing
        // so let's wait for the layout to be drawn before reporting fully drawn.
        recyclerView.doOnLayout {
            // reportFullyDrawn() prints `I/ActivityTaskManager: Fully drawn {activity} {time}`
            // to logcat. The framework ensures that the statement is printed only once for the
            // activity, so there is no need to add dedupping logic to the app.
            activity?.reportFullyDrawn()
        }
    }

}