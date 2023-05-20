package com.example.earthquakeapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.example.earthquakeapp.PreferencesActivity.Companion.PREF_UPDATE_FREQ
import com.example.earthquakeapp.model.EarthquakeRecyclerViewAdapter
import com.example.earthquakeapp.model.EarthquakeViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class EarthquakeListFragment : Fragment() {
    private var mRecyclerView: RecyclerView? = null
    private var mSwipeToRefreshView: SwipeRefreshLayout? = null
    private var mMinimumMagnitude = 0
    private var autoUpdateInterval: Long = 0
    private val mHandler = Handler(Looper.getMainLooper())
    private var autoUpdateRunnable: Runnable? = null
    private var updateFrequency: Long = 60
    private val eqViewModel: EarthquakeViewModel by viewModels()
    private var job: Job? = null
    private fun getUpdateFrequency(): Long {
        val sharedPreferences = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        return sharedPreferences.getLong("updateFrequency", 60) // Default to 60 seconds if not found
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("EarthquakeList", "got here 1")
        val prefs = context?.let { PreferenceManager.getDefaultSharedPreferences(it) }
        if (prefs != null) {
            autoUpdateInterval = prefs.getString(PREF_UPDATE_FREQ, "60")!!.toLong()
        }
    }
    private fun updateFromPreferences() {
        val prefs: SharedPreferences? = context?.let {
            PreferenceManager.getDefaultSharedPreferences(
                it
            )
        }
        if (prefs != null) {
            mMinimumMagnitude = prefs.getString(PreferencesActivity.PREF_MIN_MAG, "3")!!.toInt()
        }
    }
    private fun startAutomaticUpdates() {
        updateFrequency = getUpdateFrequency()

        autoUpdateRunnable = object : Runnable {
            override fun run() {
                updateEarthquakes()
                mHandler.postDelayed(this, updateFrequency * 1000)
            }
        }
        mHandler.postDelayed(autoUpdateRunnable!!, updateFrequency * 1000)
    }
    private fun stopAutomaticUpdates() {
        autoUpdateRunnable?.let {
            mHandler.removeCallbacks(it)
            autoUpdateRunnable = null
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        val autoUpdateRunnable = Runnable {
            updateEarthquakes()
            autoUpdateRunnable?.let { mHandler.postDelayed(it, autoUpdateInterval * 60 * 1000) }
        }
        mHandler.postDelayed(autoUpdateRunnable, autoUpdateInterval * 60 * 1000)
    }


    protected fun updateEarthquakes() {
        viewLifecycleOwner.lifecycleScope.launch {
            setEarthquakes()
        }
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
    override fun onDestroy() {
        super.onDestroy()
        stopAutomaticUpdates()
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
