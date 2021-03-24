package com.example.weather

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Placeholder
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

const val TAG = "MyLogger"
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val myGApiKey = "AIzaSyC7KXPQfRuR-s9wwZ5m409O_xjsdfQzkXc";
        val myWApiKey = "f39b493a5b2376f0fd5beda26b8acc6b";
        var PlaceName: String
        var PlaceId: String

        Places.initialize(applicationContext, myGApiKey)
        var placesClient: PlacesClient = Places.createClient(this)

        val autocompleteFragment =
                supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                        as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: ${place.name}, ${place.id}")
                PlaceName = place.name.toString()
                PlaceId = place.id.toString()
                findViewById<TextView>(R.id.textView1).text = PlaceName
                weatherTask(PlaceName).execute()
                randomCompliment().execute()
               // findViewById<TextView>(R.id.textView2).text ="15"

            }

            override fun onError(status: Status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: $status")
            }

        })


    }
    inner class randomCompliment(): AsyncTask<String,Void,String>() {
        override fun onPreExecute() {
            super.onPreExecute()
        }
        override fun doInBackground(vararg params: String?): String? {
            var response: String?
            try {
                response = URL("https://random-compliment.herokuapp.com/random").readText(
                        Charsets.UTF_8
                )
            } catch (e: Exception) {
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                /* Extracting JSON returns from the API */
                val jsonObj = JSONObject(result)
                val text = jsonObj.getString("text")


                findViewById<TextView>(R.id.RandomCompliment).text = text

                 } catch (e: Exception) {
            }


        }
    }

    inner class weatherTask(PlaceName: String) : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
        }

        val myWApiKey = "f39b493a5b2376f0fd5beda26b8acc6b";
        var PlaceName1: String = PlaceName
        override fun doInBackground(vararg params: String?): String? {
            var response: String?
            try {
                response = URL("https://api.openweathermap.org/data/2.5/weather?q=${PlaceName1}&units=metric&appid=${myWApiKey}").readText(
                        Charsets.UTF_8
                )
            } catch (e: Exception) {
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                /* Extracting JSON returns from the API */
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val timezone = jsonObj.getInt("timezone")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                val updatedAt: Long = jsonObj.getLong("dt")
                val updatedAtText = "Updated at: " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(Date(updatedAt * 1000))
                val temp = main.getString("temp") + "°C"
                val tempMin =  main.getString("temp_min") + "°C"+"\nMin Temp"
                val tempMax = main.getString("temp_max") + "°C" + "\nMax Temp"
                val pressure = main.getString("pressure") + " mm Hg"
                val humidity = main.getString("humidity") + "°\nhumidity"

                val sunrise: Long = sys.getLong("sunrise")
                val sunset: Long = sys.getLong("sunset")
                val windSpeed = wind.getString("speed") + "m/s\nwind speed"
                val weatherDescription = weather.getString("description")

                val address = jsonObj.getString("name") + ", " + sys.getString("country")
                findViewById<TextView>(R.id.textView2).text = temp
                findViewById<TextView>(R.id.describe).text = weatherDescription
                findViewById<TextView>(R.id.MinTemp).text = tempMin
                findViewById<TextView>(R.id.MaxTemp).text = tempMax
                findViewById<TextView>(R.id.Wind).text = windSpeed
                findViewById<TextView>(R.id.humidity).text = humidity
                findViewById<TextView>(R.id.Rain).text ="sunrise at \n"+ SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date((sunrise + timezone)*1000)) +"\nsunset at \n" +  SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date((sunset + timezone)*1000))
            } catch (e: Exception) {
            }


        }
    }
}