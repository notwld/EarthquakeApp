package com.example.earthquakeapp.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.earthquakeapp.R

class EarthquakeRecyclerViewAdapter(private val mEarthquakes: List<Earthquake>) : RecyclerView.Adapter<EarthquakeRecyclerViewAdapter.ViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate( R.layout.list_item_earthquake, parent, false )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.earthquake = mEarthquakes[position]
        holder.detailsView.text = mEarthquakes[position].toString()
    }

    override fun getItemCount(): Int { return mEarthquakes.size }
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val parentView: View
        val detailsView: TextView
        var earthquake: Earthquake? = null
        init {
            parentView = view
            detailsView = view.findViewById(R.id.list_item_earthquake_details)
        }
        override fun toString(): String { return super.toString() + " '" + detailsView.text + "'" }
    }
}
