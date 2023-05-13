package com.example.earthquakeapp.utils

import com.example.earthquakeapp.model.Earthquake
import java.util.*
import kotlin.collections.ArrayList

object DummyData {
    val now: Date = Calendar.getInstance().getTime()
    fun getQuakes(): MutableList<Earthquake?> {
        var dummyQuakes: MutableList<Earthquake?> = ArrayList(0)
        dummyQuakes.add(Earthquake("0", now, "San Jose", null, 7.3, null))
        dummyQuakes.add(Earthquake("1", now, "LA", null, 6.5, null))
        return dummyQuakes
    }
}