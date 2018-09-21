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
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import elnaggar.locationsaver.dummy.DummyContent

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_location_dialog.*
import kotlinx.android.synthetic.main.add_location_dialog.view.*

class MainActivity : AppCompatActivity(), LocationFragment.OnListFragmentInteractionListener, LocationListener {
    private var location: Location? = null

    override fun onLocationChanged(location: Location?) {
        this.location = location
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String?) {
    }

    override fun onProviderDisabled(provider: String?) {
    }

    override fun onListFragmentInteraction(item: DummyContent.Location?) {
        val clipboardManager = getSystemService(Activity.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.primaryClip = ClipData.newPlainText("location", item.toString())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10L, 0f, this@MainActivity)

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
                val location = DummyContent.Location()
                location.title = name.text.toString()
                location.latitude = lat.text.toString()
                location.longitude = long.text.toString()
                val database = getDatabase()
                database.locationDao().insert(location)
                (supportFragmentManager.fragments[0] as LocationFragment).itemAdded()
                dialog.dismiss()

            }
            dialog.show()


        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun getDatabase(): AppDatabase {
        return Room.databaseBuilder(this@MainActivity
                , AppDatabase::class.java, "locations")
                .allowMainThreadQueries().build()
    }
}
