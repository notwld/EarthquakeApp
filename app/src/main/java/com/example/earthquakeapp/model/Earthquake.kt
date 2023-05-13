package com.example.earthquakeapp.model

import android.location.Location
import java.text.SimpleDateFormat
import java.util.*

class Earthquake(
    id: String?, date: Date?, details: String?, location: Location?,
    magnitude: Double, link: String?
) {
    private var mId: String? = id
    private var mDate: Date? = date ?: Date()
    private var mDetails: String? = details
    private var mLocation: Location? = location
    private var mMagnitude = magnitude
    private var mLink: String? = link



init {
            mId = id
            mDate = date
            mDetails = details
            mLocation = location
            mMagnitude = magnitude
            mLink = link
        }
        fun getId(): String? {
            return mId
        }

        fun getDate(): Date? { return mDate }
        fun getDetails(): String? { return mDetails }
        fun getLocation(): Location? { return mLocation }
        fun getMagnitude(): Double { return mMagnitude }
        fun getLink(): String? { return mLink }

        override fun toString(): String {
            val sdf = SimpleDateFormat("HH.mm", Locale.US)
            val dateString: String? = mDate?.let { sdf.format(it) }
            return "$dateString: $mMagnitude $mDetails"
        }
        override fun equals(other: Any?): Boolean {
            return if (other is Earthquake) other.getId().contentEquals(mId) else false }
}
