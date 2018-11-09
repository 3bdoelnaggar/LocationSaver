package elnaggar.locationsaver

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnCameraIdleListener, LocationController.LocationSubscriber, GoogleMap.OnMapClickListener, SingletonLocationController.LocationSubscriber {
    private var mLocation: Location? = null

    override fun onMapClick(p0: LatLng?) {
        p0?.let {
            addMarker(it)
            mLocation = Location(LocationManager.GPS_PROVIDER)
            mLocation!!.longitude = p0.longitude
            mLocation!!.latitude = p0.latitude
        }

    }


    override fun onCameraIdle() {

    }
    val ALEXANDRIA_LOCATION = LatLng(31.2001, 29.9187)


    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        btn_confirmLocation.setOnClickListener {
            if (mLocation != null) {
                val data = Intent()
                data.putExtra("location", mLocation)
                setResult(Activity.RESULT_OK, data)
                finish()
            }
        }
    }


    private var lc: SingletonLocationController? = null

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnCameraIdleListener(this)
        lc = SingletonLocationController.getInstance()
        lc?.requestLocation(this, this)
        mMap.setOnMapClickListener(this)
        moveCameraToMyLocation(ALEXANDRIA_LOCATION)

    }

    private val markers: ArrayList<Marker> = java.util.ArrayList()

    override fun onGetLocation(location: Location) {
        // Add a marker in Sydney and move the camera
        moveCameraToMyLocation(location)
        lc?.removeListener()


    }

    private fun moveCameraToMyLocation(location: Location?) {
        if (location != null) {
            val latLng = LatLng(location.latitude, location.longitude)
            val cameraPosition = CameraPosition.Builder()
                    .target(latLng)
                    .zoom(17f)
                    .build()
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            addMarker(latLng)

        }
    }
    private fun moveCameraToMyLocation(location: LatLng?) {
        if (location != null) {
            val latLng = LatLng(location.latitude, location.longitude)
            val cameraPosition = CameraPosition.Builder()
                    .target(latLng)
                    .zoom(17f)
                    .build()
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            addMarker(latLng)

        }
    }

    private fun addMarker(latLng: LatLng) {
        for (x in markers) {
            x.remove()
            markers.remove(x)
        }
        markers.add(mMap.addMarker(MarkerOptions().position(latLng)))
    }

    override fun onError(type: Int) {
        when (type) {
            GPS_NOT_ENABLED -> Snackbar.make(btn_confirmLocation, "GPS NOT ENABLED", Snackbar.LENGTH_SHORT).show()
            LOCATION_NOT_AVAILABLE -> Snackbar.make(btn_confirmLocation, "LOCATIONS_NOT_AVAILABLE", Snackbar.LENGTH_SHORT).show()
        }
    }
}