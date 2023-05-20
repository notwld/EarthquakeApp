package com.example.earthquakeapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.earthquakeapp.model.EarthquakeRecyclerViewAdapter
import com.example.earthquakeapp.model.EarthquakeViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class EarthquakeListFragment : Fragment() {
    private var mRecyclerView: RecyclerView? = null
    private var mSwipeToRefreshView: SwipeRefreshLayout? = null
    private var mMinimumMagnitude = 0

    private val eqViewModel: EarthquakeViewModel by viewModels()
    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("EarthquakeList", "got here 1")

    }
    private fun updateFromPreferences() {
        val prefs: SharedPreferences? = getContext()?.let {
            PreferenceManager.getDefaultSharedPreferences(
                it
            )
        }
        if (prefs != null) {
            mMinimumMagnitude = prefs.getString(PreferencesActivity.PREF_MIN_MAG, "3")!!.toInt()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_earthquake_list, container, false)
        mSwipeToRefreshView = view.findViewById(R.id.swipe);
        mRecyclerView = view.findViewById<View>(R.id.list) as RecyclerView?
        Log.d("EarthquakeList", "got here 2")
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the Recycler View adapter
        val context: Context = view.context
        mRecyclerView!!.layoutManager = LinearLayoutManager(context)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                updateEarthquakes()
            }
        }
        mSwipeToRefreshView!!.setOnRefreshListener {
            updateEarthquakes()
            mSwipeToRefreshView!!.isRefreshing = false
        }
    }


    protected fun updateEarthquakes() {
        job = viewLifecycleOwner.lifecycleScope.launch {
            val earthquakes = eqViewModel.loadEarthquakes()
            mRecyclerView?.adapter = EarthquakeRecyclerViewAdapter(earthquakes) }
    }

    override fun onStart() {
        super.onStart()
        job = viewLifecycleOwner.lifecycleScope.launch {
            val earthquakes = eqViewModel.loadEarthquakes()
            mRecyclerView?.adapter = EarthquakeRecyclerViewAdapter(earthquakes) }
    }
    override fun onStop() {
        super.onStop()
        job?.cancel()
    }
    protected suspend fun setEarthquakes(){
        val earthquakes = eqViewModel.loadEarthquakes()

        if (earthquakes.size > 0)
            for (i in earthquakes.size - 1 downTo 0) {
                if (earthquakes.get(i)?.getMagnitude()!! < mMinimumMagnitude) {
                    earthquakes.removeAt(i)
                }
            }
        mRecyclerView?.adapter = EarthquakeRecyclerViewAdapter(earthquakes)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val mPrefListener =
            SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
                if (PreferencesActivity.PREF_MIN_MAG == key) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        setEarthquakes()
                    }
                }
            }
        val prefs = getContext()?.let { PreferenceManager.getDefaultSharedPreferences(it) };
        if (prefs != null) {
            prefs.registerOnSharedPreferenceChangeListener(mPrefListener)
        };

    }

}
