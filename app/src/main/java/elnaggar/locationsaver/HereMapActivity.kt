package elnaggar.locationsaver

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.model.LatLng
import com.here.android.mpa.common.GeoCoordinate
import com.here.android.mpa.common.OnEngineInitListener
import com.here.android.mpa.mapping.Map
import com.here.android.mpa.mapping.MapFragment
import kotlinx.android.synthetic.main.activity_here_map.*

class HereMapActivity : AppCompatActivity(), OnEngineInitListener, SingletonLocationController.LocationSubscriber {
    override fun onGetLocation(location: Location) {
        moveCameraToPosition(location)
        locationController?.removeListener()
    }

    private fun moveCameraToPosition(location: Location) {
        map?.setCenter(GeoCoordinate(location.latitude,
                location.longitude), Map.Animation.NONE)
        map?.zoomLevel = map!!.maxZoomLevel-1
    }
    private fun moveCameraToPosition(location: LatLng) {
        map?.setCenter(GeoCoordinate(location.latitude,
                location.longitude), Map.Animation.NONE)
        map?.zoomLevel = map!!.maxZoomLevel
    }
    override fun onError(type: Int) {
    }

    override fun onStop() {
        locationController?.removeListener()
        super.onStop()
    }

    private var map: Map? = null

    override fun onEngineInitializationCompleted(p0: OnEngineInitListener.Error?) {
        if (p0 == OnEngineInitListener.Error.NONE) {
            map = mapFragment.map
            moveCameraToPosition(ALEXANDRIA_LOCATION)
            locationController?.requestLocation(this,this)

        } else {
            System.out.println("ERROR: Cannot initialize MapFragment")
        }
    }
    private val ALEXANDRIA_LOCATION = LatLng(31.2001, 29.9187)



    private lateinit var mapFragment: MapFragment


    private  var locationController: SingletonLocationController?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationController = SingletonLocationController.getInstance()
        setContentView(R.layout.activity_here_map)
        mapFragment = fragmentManager.findFragmentById(R.id.mapfragment) as MapFragment
        // initialize the Map Fragment and
        // retrieve the map that is associated to the fragment
        mapFragment.init(this)
        btn_done.setOnClickListener {
            val data = Intent()
            val location = Location(LocationManager.GPS_PROVIDER)
            val coordinate = map?.center
            location.latitude = coordinate!!.latitude
            location.longitude = coordinate.longitude
            data.putExtra("location", location)
            setResult(Activity.RESULT_OK, data)
            finish()
        }

    }
}
