package com.example.earthquakeapp.model

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.earthquakeapp.databinding.ListItemEarthquakeBinding
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EarthquakeRecyclerViewAdapter(private val mEarthquakes: ArrayList<Earthquake?>) : RecyclerView.Adapter<EarthquakeRecyclerViewAdapter.ViewHolder>()
{
    private val TIME_FORMAT: SimpleDateFormat = SimpleDateFormat("HH:mm", Locale.US)
    private val MAGNITUDE_FORMAT: NumberFormat = DecimalFormat("0.0")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemEarthquakeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val earthquake = mEarthquakes[position]
        holder.binding?.setEarthquake(earthquake);
        holder.binding?.executePendingBindings();
    }


    override fun getItemCount(): Int { return mEarthquakes.size }

    inner class ViewHolder(binding: ListItemEarthquakeBinding) : RecyclerView.ViewHolder(binding.getRoot()) {
        val binding: ListItemEarthquakeBinding? = binding
        init {
            binding.setTimeformat(TIME_FORMAT)
            binding.setMagnitudeformat(MAGNITUDE_FORMAT)
        }

        override fun toString(): String {
            val earthquake = binding?.getEarthquake()
            val date = earthquake?.getDate()
            val dateString = if (date != null) TIME_FORMAT.format(date) else "Unknown"
            return super.toString() + " '" + (binding?.details?.text ?: "") + "' " + dateString
        }
    }


}

