package com.example.earthquakeapp.model

import android.app.Application
import android.content.ContentValues.TAG
import android.content.Context
import android.location.Location
import android.util.JsonReader
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.earthquakeapp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import org.xml.sax.SAXException
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.net.URLConnection
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import kotlin.collections.ArrayList

class EarthquakeViewModel (application: Application) : AndroidViewModel(application) {
    private var mListener: OnListFragmentInteractionListener? = null
    private val mEarthquakes = ArrayList<Earthquake>()

    init {
        //coroutine
        viewModelScope.launch {
        }
    }
    protected fun updateEarthquakes() {
        mListener?.onListFragmentRefreshRequested();
    }


    interface OnListFragmentInteractionListener {
        fun onListFragmentRefreshRequested()
    }
    // Traverse the array of earthquakes.
    @Throws(IOException::class)
    private fun readEarthquakeArray(reader: JsonReader): List<Earthquake?> {
        val earthquakes: MutableList<Earthquake?> = ArrayList()
        // The earthquake details are stored in an array.
        reader.beginArray()
        while (reader.hasNext()) {
// Traverse the array, parsing each earthquake.
            earthquakes.add(readEarthquake(reader))
        }
        reader.endArray()
        return earthquakes
    }

    // Parse each earthquake object within the earthquake array.
    @Throws(IOException::class)
    fun readEarthquake(reader: JsonReader): Earthquake {
        var id: String? = null
        var location: Location? = null
        var earthquakeProperties: Earthquake? = null
        reader.beginObject()
        while (reader.hasNext()) {
            val name = reader.nextName()
            if (name == "id") {
// The ID is stored as a value.
                id = reader.nextString()
            } else if (name == "geometry") {
// The location is stored as a geometry object
// that must be parsed.
                location = readLocation(reader)
            } else if (name == "properties") {
// Most of the earthquake details are stored as a
// properties object that must be parsed.
                earthquakeProperties = readEarthquakeProperties(reader)
            } else {
                reader.skipValue()
            }
        }
        reader.endObject()
        // Construct a new Earthquake based on the parsed details.
        return Earthquake(
            id,
            earthquakeProperties!!.getDate(),
            earthquakeProperties.getDetails(),
            location,
            earthquakeProperties.getMagnitude(),
            earthquakeProperties.getLink()
        )
    }

    // Parse the properties object for each earthquake object
    // within the earthquake array.
    @Throws(IOException::class)
    fun readEarthquakeProperties(reader: JsonReader): Earthquake {
        var date: Date? = null
        var details: String? = null
        var magnitude = -1.0
        var link: String? = null
        reader.beginObject()
        while (reader.hasNext()) {
            val name = reader.nextName()
            if (name == "time") {
                val time = reader.nextLong()
                date = Date(time)
            } else if (name == "place") {
                details = reader.nextString()
            } else if (name == "url") {
                link = reader.nextString()
            } else if (name == "mag") {
                magnitude = reader.nextDouble()
            } else {
                reader.skipValue()
            }
        }
        reader.endObject()
        return Earthquake(null, date, details, null, magnitude, link)
    }

    // Parse the coordinates object to obtain a location.
    @Throws(IOException::class)
    private fun readLocation(reader: JsonReader): Location? {
        var location: Location? = null
        reader.beginObject()
        while (reader.hasNext()) {
            val name = reader.nextName()
            if (name == "coordinates") {
// The location coordinates are stored within an
// array of doubles.
                val coords = readDoublesArray(reader)
                location = Location("dummy")
                location.latitude = coords[0]
                location.longitude = coords[1]
            } else {
                reader.skipValue()
            }
        }
        reader.endObject()
        return location
    }

    // Parse an array of doubles.
    @Throws(IOException::class)
    fun readDoublesArray(reader: JsonReader): List<Double> {
        val doubles: MutableList<Double> = ArrayList()
        reader.beginArray()
        while (reader.hasNext()) {
            doubles.add(reader.nextDouble())
        }
        reader.endArray()
        return doubles
    }



    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ViewModel instance about to be destroyed")
    }
    suspend fun loadEarthquakes() : ArrayList<Earthquake?> {
        val earthquakes: ArrayList<Earthquake?> = ArrayList(0)
        val url: URL
        try {
            val quakeFeed: String = getApplication<Application>().resources.getString(R.string.earthquake_feed)
            url = URL(quakeFeed)
            val connection: URLConnection = withContext(Dispatchers.IO) {
                url.openConnection()
            }
            val httpConnection: HttpURLConnection = connection as HttpURLConnection
            val responseCode: Int =
                withContext(Dispatchers.IO) {
                    httpConnection.responseCode
                }
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val `in`: InputStream =
                    withContext(Dispatchers.IO) {
                        httpConnection.inputStream
                    }
                val dbf: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
                val db: DocumentBuilder = dbf.newDocumentBuilder()
                // Parse the earthquake feed.
                val dom: Document =
                    withContext(Dispatchers.IO) {
                        db.parse(`in`)
                    }
                val docEle: Element = dom.getDocumentElement()
                // Get a list of each earthquake entry.
                val nl: NodeList = docEle.getElementsByTagName("entry")
                if (nl.getLength() > 0) {
                    for (i in 0 until nl.getLength()) {
                        val entry: Element = nl.item(i) as Element
                        val id: Element =
                            entry.getElementsByTagName("id").item(0) as Element
                        val title: Element =
                            entry.getElementsByTagName("title").item(0) as Element
                        val g: Element = entry.getElementsByTagName("georss:point")
                            .item(0) as Element
                        val `when`: Element =
                            entry.getElementsByTagName("updated").item(0) as Element
                        val link: Element =
                            entry.getElementsByTagName("link").item(0) as Element
                        val idString: String = id.getFirstChild().getNodeValue()
                        var details: String = title.getFirstChild().getNodeValue()
                        val hostname = "http://earthquake.usgs.gov"
                        val linkString = hostname + link.getAttribute("href")
                        val point: String = g.getFirstChild().getNodeValue()
                        val dt: String = `when`.getFirstChild().getNodeValue()
                        val sdf = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'")
                        var qdate: Date = GregorianCalendar(0, 0, 0).getTime()
                        try {
                            qdate = sdf.parse(dt)
                        } catch (e: ParseException) {
                            Log.e(TAG, "Date parsing exception.", e)
                        }
                        val location =
                            point.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        val l = Location("dummyGPS")
                        l.setLatitude(location[0].toDouble())
                        l.setLongitude(location[1].toDouble())
                        val magnitudeString =
                            details.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()[1]
                        val end = magnitudeString.length - 1
                        val magnitude = magnitudeString.substring(0, end).toDouble()
                        details = if (details.contains("-")) details.split("-".toRegex())
                            .dropLastWhile { it.isEmpty() }
                            .toTypedArray()[1].trim { it <= ' ' } else ""
                        val earthquake = Earthquake(
                            idString,
                            qdate,
                            details, l,
                            magnitude,
                            linkString
                        )
                        // Add the new earthquake to our result array.
                        earthquakes.add(earthquake)
                    }
                }
            }
            httpConnection.disconnect()
        } catch (e: MalformedURLException) {
            Log.e(TAG, "MalformedURLException", e)
        } catch (e: IOException) {
            Log.e(TAG, "IOException", e)
        } catch (e: ParserConfigurationException) {
            Log.e(TAG, "Parser Configuration Exception", e)
        } catch (e: SAXException) {
            Log.e(TAG, "SAX Exception", e)
        }
        // Return our result array.
        return earthquakes
    }

}

