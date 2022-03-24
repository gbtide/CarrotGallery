package com.carrot.gallery.ui

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import com.carrot.gallery.R
import com.carrot.gallery.databinding.FragmentSecondBinding
import timber.log.Timber

/**
 * Created by kyunghoon on 2022-03-21
 */
class TestSecondFragment : Fragment() {
    private lateinit var binding: FragmentSecondBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.sample_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                Toast.makeText(context, "click settings!", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_done -> {
                Toast.makeText(context, "click action done!", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Timber.d("onCreateView $this")
        binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.d("onViewCreated $this")
        super.onViewCreated(view, savedInstanceState)
        binding.buttonOh.setOnClickListener {
            setFragmentResult(
                "requestKey", bundleOf(
                    "bundleKey" to "Something"
                )
            )
        }
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