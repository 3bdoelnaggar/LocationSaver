package elnaggar.locationsaver

import android.app.Activity
import android.arch.persistence.room.Room
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.location.Location
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
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_location_dialog.view.*


private const val LOG_TAG = "ElnaggarApp"
const val LOCATION_REQUEST = 123

class MainActivity : AppCompatActivity(), LocationFragment.OnListFragmentInteractionListener, SingletonLocationController.LocationSubscriber {
    override fun onLocationStateChanged(isOne: Boolean) {
        when (isOne) {
            true -> Snackbar.make(fab, "LOCATIONS_IS_ON", Snackbar.LENGTH_SHORT).show()
            false -> Snackbar.make(fab, "LOCATIONS_IS_OFF", Snackbar.LENGTH_SHORT).show()

        }
    }

    override fun onGetLocation(location: Location) {
        this.location = location

    }

    override fun onError(type: Int) {
        when (type) {
            GPS_NOT_ENABLED -> Snackbar.make(fab, "GPS NOT ENABLED", Snackbar.LENGTH_SHORT).show()
            LOCATION_NOT_AVAILABLE -> Snackbar.make(fab, "LOCATIONS_NOT_AVAILABLE", Snackbar.LENGTH_SHORT).show()
        }

    }


    override fun onListFragmentInteraction(item: elnaggar.locationsaver.Location?) {
        val clipboardManager = getSystemService(Activity.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.primaryClip = ClipData.newPlainText("location", item.toString())
    }

    private var location: Location? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        mLocationController = SingletonLocationController.getInstance()
        Log.d("instanceid", "" + System.identityHashCode(mLocationController))

        mLocationController?.requestLocation(this, this)
        fab.setOnClickListener {
            createAddLocationDialog()
        }
    }

    private fun createAddLocationDialog() {
        val view = LayoutInflater.from(this@MainActivity).inflate(R.layout.add_location_dialog, null)
        val name: EditText = view.et_locationName
        val lat: TextView = view.tv_lat
        val long: TextView = view.tv_long
        if (location != null) {
            lat.text = this.location!!.latitude.toString()
            long.text = this.location!!.longitude.toString()
        } else {
            return
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.mi_googleMap -> {
                startActivityForResult(Intent(this, MapsActivity::class.java), LOCATION_REQUEST)
            }
            R.id.mi_hereMap -> {
                startActivityForResult(Intent(this, HereMapActivity::class.java), LOCATION_REQUEST)
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == LOCATION_REQUEST) {
            location = data?.getParcelableExtra("location")
            createAddLocationDialog()
        }
    }

    private var mLocationController: SingletonLocationController? = null

    override fun onStop() {
        mLocationController?.removeListener()
        super.onStop()


    }


    fun getDatabase(): AppDatabase {
        return Room.databaseBuilder(this@MainActivity
                , AppDatabase::class.java, "locations")
                .allowMainThreadQueries().build()
    }
}
