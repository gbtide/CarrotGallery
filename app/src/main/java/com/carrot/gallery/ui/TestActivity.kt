package com.carrot.gallery.ui

import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.carrot.gallery.R
import com.carrot.gallery.databinding.ActivityTestBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by kyunghoon on 2022-03-21
 */
class TestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTestBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                // For behavior compatibility,
                // the reordering flag is not enabled by default. It is required, however ...
                setReorderingAllowed(true)
                add(R.id.nav_host_test_fragment, TestFragment::class.java, null, "tag1")
            }

            lifecycleScope.launch {
                delay(500)
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace(R.id.nav_host_test_fragment, TestSecondFragment::class.java, null, "tag2")
                    addToBackStack(null)
                }
            }
        }

        lifecycleScope.launch {
            delay(1000)
            val fragment = supportFragmentManager.findFragmentByTag("tag2")
            val fragmentById = supportFragmentManager.findFragmentById(R.id.nav_host_test_fragment)
            Toast.makeText(this@TestActivity, "find by tag - $fragment, find by id - $fragmentById", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.sample_menu_2, menu)
        return super.onCreateOptionsMenu(menu)
    }


}