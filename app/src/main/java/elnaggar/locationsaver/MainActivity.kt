package elnaggar.locationsaver

import android.app.Activity
import android.arch.persistence.room.Room
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.LocationSource

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_location_dialog.view.*


private const val LOG_TAG = "ElnaggarApp"

class MainActivity : AppCompatActivity(), LocationFragment.OnListFragmentInteractionListener, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,com.google.android.gms.location.LocationListener {
    override fun onConnected(p0: Bundle?) {
        mLocationsRequest=LocationRequest.create()
        mLocationsRequest!!.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        mLocationsRequest!!.interval=1000
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationsRequest,this)

    }

    override fun onConnectionSuspended(p0: Int) {
        Log.d(LOG_TAG,"onConnectionSuspended")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.e(LOG_TAG,"onConnectionFailed")
    }


    private var mLocationsRequest: LocationRequest? = null
    private var mGoogleApiClient: GoogleApiClient? = null


    override fun onListFragmentInteraction(item: elnaggar.locationsaver.Location?) {
        val clipboardManager = getSystemService(Activity.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.primaryClip = ClipData.newPlainText("location", item.toString())
    }

    private var location: Location? = null

    override fun onLocationChanged(location: Location?) {
        this.location = location
        Toast.makeText(this, "We Got Locations", Toast.LENGTH_SHORT).show()
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String?) {
    }

    override fun onProviderDisabled(provider: String?) {
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build()
        fab.setOnClickListener { _ ->
            val view = LayoutInflater.from(this@MainActivity).inflate(R.layout.add_location_dialog, null)
            val name: EditText = view.et_locationName
            val lat: TextView = view.tv_lat
            val long: TextView = view.tv_long
            if (location != null) {
                lat.text = this.location!!.latitude.toString()
                long.text = this.location!!.longitude.toString()
            }
            val addLocation: Button = view.btn_addLocation


            val dialog = AlertDialog.Builder(this@MainActivity).setView(view).create()
            addLocation.setOnClickListener {
                val database = getDatabase()
                if (name.text.toString() == "") {
                    Toast.makeText(this, "Location Is Empty", Toast.LENGTH_SHORT).show()
                } else {
                    val location = Location()
                    location.title = name.text.toString()
                    location.latitude = lat.text.toString()
                    location.longitude = long.text.toString()
                    database.locationDao().insert(location)
                    (supportFragmentManager.fragments[0] as LocationFragment).itemAdded()
                    dialog.dismiss()
                }


            }
            dialog.show()


        }
    }

    override fun onStart() {
        super.onStart()
        if (mGoogleApiClient != null)
            mGoogleApiClient!!.connect()
    }

    override fun onStop() {
        if (mGoogleApiClient != null)
            mGoogleApiClient!!.disconnect()
        super.onStop()


    }


    fun getDatabase(): AppDatabase {
        return Room.databaseBuilder(this@MainActivity
                , AppDatabase::class.java, "locations")
                .allowMainThreadQueries().build()
    }
}
