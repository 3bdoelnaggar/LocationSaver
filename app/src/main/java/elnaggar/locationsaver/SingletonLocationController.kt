package elnaggar.locationsaver

import android.annotation.SuppressLint
import android.content.Context
import android.location.GpsStatus
import android.location.Location
import android.location.LocationManager
import com.google.android.gms.location.*


class SingletonLocationController private constructor() : GpsStatus.Listener, LocationCallback() {
    override fun onGpsStatusChanged(event: Int) {
        when (event) {
            GpsStatus.GPS_EVENT_STARTED -> mGPSEnabled = true
            GpsStatus.GPS_EVENT_STOPPED -> mGPSEnabled = false
        }

    }

    override fun onLocationResult(p0: LocationResult?) {
        p0?.let {
            for (location in it.locations) {
                mLocationSubscriber?.onGetLocation(location)
            }

        }
    }


    override fun onLocationAvailability(p0: LocationAvailability?) {
       if(p0?.isLocationAvailable!=true){
           mLocationSubscriber?.onError(LOCATION_NOT_AVAILABLE)
       }
    }

    private var mGPSEnabled: Boolean = false
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private var mLocationSubscriber: LocationSubscriber? = null
    private var mLocationServices: LocationManager? = null


    @SuppressLint("MissingPermission")
    fun requestLocation(context: Context, locationSubscriber: LocationSubscriber) {
        val mLocationsRequest = LocationRequest.create()
        mLocationsRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationsRequest.interval = 1000
        mLocationSubscriber = locationSubscriber
        if (mFusedLocationProviderClient == null)
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        if (mLocationServices == null) {
            mLocationServices = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
        mGPSEnabled = mLocationServices!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (mGPSEnabled) {
            mFusedLocationProviderClient?.requestLocationUpdates(mLocationsRequest, this,null)
        } else {
            locationSubscriber.onError(GPS_NOT_ENABLED)
        }

    }


    fun removeListener() {
        mFusedLocationProviderClient?.removeLocationUpdates(this)

    }

    companion object {
        private var locationController: SingletonLocationController? = null
        fun getInstance(): SingletonLocationController? {
            if (locationController == null) {
                locationController = SingletonLocationController()
            }
            return locationController
        }
    }


    interface LocationSubscriber {
        fun onGetLocation(location: Location)
        fun onError(type: Int)
    }

}