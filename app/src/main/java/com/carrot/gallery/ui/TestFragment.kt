package com.carrot.gallery.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.carrot.gallery.databinding.FragmentFirstBinding
import timber.log.Timber

/**
 * Created by kyunghoon on 2022-03-21
 */
class TestFragment : Fragment() {
    private lateinit var binding: FragmentFirstBinding

    private val activityViewModel by activityViewModels<TestViewModel>()
    private val viewModel by viewModels<TestSubViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener("requestKey") { requestKey, bundle ->
            val result = bundle.getString("bundleKey")
            Toast.makeText(context, "FragmentResult : BundleKey is $result", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Timber.d("onCreateView $this")
        binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.d("onViewCreated $this")
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        Timber.d("onDestroyView $this")
        super.onDestroyView()
    }

    override fun onDestroy() {
        Timber.d("onDestroy $this")
        super.onDestroy()
    }
}