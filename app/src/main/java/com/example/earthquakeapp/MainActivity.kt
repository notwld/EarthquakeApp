package com.example.earthquakeapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.earthquakeapp.utils.DummyData

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



        mEarthquakeListFragment!!.setEarthquakes(DummyData.getQuakes())
    }
}