package com.example.earthquakeapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.earthquakeapp.model.Earthquake
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private val TAG_LIST_FRAGMENT = "TAG_LIST_FRAGMENT"
    var mEarthquakeListFragment: EarthquakeListFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fm: FragmentManager = supportFragmentManager
        if (savedInstanceState == null) {
            val ft: FragmentTransaction = fm.beginTransaction()
            mEarthquakeListFragment = EarthquakeListFragment()
            ft.add( R.id.main_activity_frame, mEarthquakeListFragment!!, TAG_LIST_FRAGMENT )
            ft.commitNow() } else { mEarthquakeListFragment = fm.findFragmentByTag(TAG_LIST_FRAGMENT) as EarthquakeListFragment? }

        val now: Date = Calendar.getInstance().getTime()
        val dummyQuakes: MutableList<Earthquake?> = ArrayList(0)
        dummyQuakes.add(Earthquake("0", now, "San Jose", null, 7.3, null))
        dummyQuakes.add(Earthquake("1", now, "LA", null, 6.5, null))
        mEarthquakeListFragment!!.setEarthquakes(dummyQuakes)
    }
}