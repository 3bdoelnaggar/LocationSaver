package elnaggar.locationsaver

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.here.android.mpa.common.GeoCoordinate
import com.here.android.mpa.common.OnEngineInitListener
import com.here.android.mpa.mapping.Map
import com.here.android.mpa.mapping.MapFragment
import kotlinx.android.synthetic.main.activity_here_map.*

class HereMapActivity : AppCompatActivity(), OnEngineInitListener, LocationController.LocationSubscriber {
    override fun onGetLocation(location: Location) {
                    map?.setCenter(GeoCoordinate(location.latitude,
                    location.longitude), Map.Animation.NONE)
        map?.setZoomLevel(map!!.maxZoomLevel)
        locationController.removeListener()
    }

    override fun onError(type: Int) {
    }

    private var map: Map? = null

    override fun onEngineInitializationCompleted(p0: OnEngineInitListener.Error?) {
        if (p0 == OnEngineInitListener.Error.NONE) {
            map = mapFragment.map
            locationController.requestLocation()
// Set the map center to Vancouver, Canada.

        } else {
            System.out.println("ERROR: Cannot initialize MapFragment");
        }
    }


    private lateinit var mapFragment: MapFragment


    private lateinit var locationController: LocationController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationController=LocationController(this,this)
        setContentView(R.layout.activity_here_map)
      mapFragment = fragmentManager.findFragmentById(R.id.mapfragment) as MapFragment
        // initialize the Map Fragment and
        // retrieve the map that is associated to the fragment
        mapFragment.init(this)
        btn_done.setOnClickListener {
            val data =Intent()
            val location=Location(LocationManager.GPS_PROVIDER)
            val coordinate = map?.center
            location.latitude= coordinate!!.latitude
            location.longitude=coordinate.longitude
            data.putExtra("location",location)
            setResult(Activity.RESULT_OK,data)
            finish()
        }

    }
}
