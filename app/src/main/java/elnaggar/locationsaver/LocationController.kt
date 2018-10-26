package elnaggar.locationsaver

import android.annotation.SuppressLint
import android.content.Context
import android.location.GnssStatus
import android.location.GpsStatus
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices

const val CONNECTION_TO_GOOGLE_ADI_SUSPENDED = 3
const val CONNECTION_TO_GOOGLE_ADI_FAILED = 4
const val GPS_NOT_ENABLED: Int = 2


class LocationController(private val locationSubscriber: LocationSubscriber, val context: Context) : GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, GpsStatus.Listener {
    override fun onGpsStatusChanged(event: Int) {
        when (event) {
            GpsStatus.GPS_EVENT_STARTED -> mGPSEnabled = true
            GpsStatus.GPS_EVENT_STOPPED -> mGPSEnabled = false
        }

    }

    @SuppressLint("MissingPermission")
    override fun onConnected(p0: Bundle?) {

        val mLocationsRequest = LocationRequest.create()
        mLocationsRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationsRequest!!.interval = 1000
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationsRequest, this)
    }


    override fun onConnectionSuspended(p0: Int) {
        locationSubscriber.onError(CONNECTION_TO_GOOGLE_ADI_SUSPENDED)

    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        locationSubscriber.onError(CONNECTION_TO_GOOGLE_ADI_FAILED)

    }

    override fun onLocationChanged(p0: Location?) {
        if (p0 != null)
            locationSubscriber.onGetLocation(p0)
    }

    private var mGoogleApiClient: GoogleApiClient? = null

    private var mGPSEnabled: Boolean = false


    @SuppressLint("MissingPermission")
    fun requestLocation() {
        val locationServices = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mGPSEnabled = locationServices.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (mGPSEnabled) {
            mGoogleApiClient = GoogleApiClient.Builder(context)
                    .addApi(LocationServices.API).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build()
            mGoogleApiClient?.connect()
            locationServices.removeGpsStatusListener(this)
        } else {
            locationSubscriber.onError(GPS_NOT_ENABLED)
        }

    }

    fun disconnect() {
        mGoogleApiClient?.disconnect()
    }

    fun removeListener() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)

    }


    interface LocationSubscriber {
        fun onGetLocation(location: Location)
        fun onError(type: Int)
    }

}