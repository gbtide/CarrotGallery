package com.carrot.gallery.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.carrot.gallery.MainViewModel
import com.carrot.gallery.R
import com.carrot.gallery.databinding.FragmentImageListBinding
import com.carrot.gallery.viewer.ImageViewerFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


private const val ARG_SOME = "ARG_SOME"

@AndroidEntryPoint
class ImageListFragment : Fragment() {
//    private var galleryContentsType: GalleryContentsType? = null

    private lateinit var binding: FragmentImageListBinding
    private lateinit var imageListAdapter: ImageListAdapter

    private val viewModel: ImageListViewModel by viewModels()
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
        binding = FragmentImageListBinding.inflate(inflater, container, false)
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

        imageListAdapter = ImageListAdapter(viewModel)

        binding.recyclerviewGallery.apply {
            adapter = imageListAdapter

            // getItemAnimator 의 초기값은 DefaultItemAnimator
            // memo. run vs apply
            (itemAnimator as DefaultItemAnimator).run {
                supportsChangeAnimations = false
                addDuration = 160L
                moveDuration = 160L
                changeDuration = 160L
                removeDuration = 120L
            }

            layoutManager = GridLayoutManager(context, ImageListAdapter.COLUMN_COUNT)
        }
    }

    private fun initViewModel() {
        viewModel.goToImageViewerAction.observe(viewLifecycleOwner, { image ->
            val bundle = bundleOf(ImageViewerFragment.IMAGE_ID to image.id)
            findNavController().navigate(R.id.to_image_viewer, bundle)
        })

        viewModel.imageResult.observe(viewLifecycleOwner, { image ->
            Timber.d("### result : %s", image)
        })

        viewModel.imagesResult.observe(viewLifecycleOwner, { images ->
            Timber.d("### result : %s", images)
        })

        binding.viewModel = viewModel

    }

}