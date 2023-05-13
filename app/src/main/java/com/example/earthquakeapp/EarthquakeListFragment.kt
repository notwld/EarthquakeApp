package com.example.earthquakeapp

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.earthquakeapp.model.Earthquake
import com.example.earthquakeapp.model.EarthquakeRecyclerViewAdapter
import com.example.earthquakeapp.utils.DummyData


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

        view.findViewById<Button>(R.id.addBtn).setOnClickListener {
            val editTxt = view.findViewById<EditText>(R.id.newQuake)
            val text = editTxt.text.toString()
            val magnitudeString = text.split(" ")[1].replace(
                "[^\\d.]".toRegex(),
                ""
            )
            if (magnitudeString.isNotEmpty()) {
                try {
                    val magnitude = magnitudeString.toDouble()
                    val newEarthquake = Earthquake(
                        (mEarthquakes.size + 1).toString(),
                        DummyData.now,
                        text.split(magnitudeString)[0].toString(),
                        null,
                        magnitude,
                        null
                    )
                    mEarthquakes.add(newEarthquake)
                    mEarthquakeAdapter.notifyItemInserted(mEarthquakes.size - 1)
                    editTxt.setText("")
                } catch (e: NumberFormatException) {
                    Toast.makeText(
                        context,
                        "Invalid magnitude: $magnitudeString",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                Toast.makeText(context, "Please enter a magnitude", Toast.LENGTH_LONG).show()
            }
        }

        return view

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



