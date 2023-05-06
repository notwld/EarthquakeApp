package com.example.earthquakeapp

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.earthquakeapp.model.Earthquake
import com.example.earthquakeapp.model.EarthquakeRecyclerViewAdapter


class EarthquakeListFragment : Fragment() {
    private var mRecyclerView: RecyclerView? = null
    private var mEarthquakes = ArrayList<Earthquake>()
    private val mEarthquakeAdapter = EarthquakeRecyclerViewAdapter(mEarthquakes)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {

        val view = inflater.inflate(R.layout.fragment_earthquake_list, container, false)
        mRecyclerView = view.findViewById<View>(R.id.list) as RecyclerView?
        return vie

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context: Context = view.context
        mRecyclerView!!.layoutManager = LinearLayoutManager(context)
        mRecyclerView!!.adapter = mEarthquakeAdapter
    }

    fun setEarthquakes(earthquakes: List<Earthquake?>) {
        for (earthquake in earthquakes) {
            if (!mEarthquakes.contains(earthquake)) {
                mEarthquakes.add(earthquake!!)
                mEarthquakeAdapter.notifyItemInserted(mEarthquakes.indexOf(earthquake))
            }
        }
    }
}

