package com.example.earthquakeapp.utils

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.earthquakeapp.R

class PrefFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?
    ) {
        setPreferencesFromResource(R.xml.userpreferences,null)
    }
}

