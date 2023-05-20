package com.example.earthquakeapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.example.earthquakeapp.model.EarthquakeViewModel
import com.example.earthquakeapp.utils.DummyData
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val MENU_PREFERENCES: Int = Menu.FIRST + 1
    private val SHOW_PREFERENCES = 1
    private val TAG_LIST_FRAGMENT = "TAG_LIST_FRAGMENT"
    var mEarthquakeListFragment: EarthquakeListFragment? = null
    private val eqViewModel: EarthquakeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("Main", "Start")
        val fm: FragmentManager = supportFragmentManager
        if (savedInstanceState == null) {
            val ft: FragmentTransaction = fm.beginTransaction()
            mEarthquakeListFragment = EarthquakeListFragment()
            ft.add(
                R.id.main_activity_frame,
                mEarthquakeListFragment!!, TAG_LIST_FRAGMENT
            )
            ft.commitNow()
        } else {
            mEarthquakeListFragment = fm.findFragmentByTag(TAG_LIST_FRAGMENT) as EarthquakeListFragment?
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menu.add(0, MENU_PREFERENCES, Menu.NONE, R.string.menu_settings)
        return true
    }

    fun onListFragmentRefreshRequested() {
        updateEarthquakes();
    }
    private fun updateEarthquakes() {
        // Request the View Model update the earthquakes from the USGS feed.
        lifecycleScope.launch {
            eqViewModel.loadEarthquakes()

        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        when (item.getItemId()) {
            MENU_PREFERENCES -> {
                val intent = Intent(this, PreferencesActivity::class.java)
                startActivityForResult(intent, SHOW_PREFERENCES)
                return true
            }
        }
        return false
    }

}

