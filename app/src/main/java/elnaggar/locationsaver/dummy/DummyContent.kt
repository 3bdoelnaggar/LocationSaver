package elnaggar.locationsaver.dummy

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.ArrayList
import java.util.HashMap

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 * TODO: Replace all uses of this class before publishing your app.
 */
object DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    val ITEMS: MutableList<Location> = ArrayList()

    /**
     * A map of sample (dummy) items, by ID.
     */
    private val ITEM_MAP: MutableMap<String, Location> = HashMap()

    private const val COUNT = 25

    init {
        // Add some sample items.

    }


    private fun makeDetails(position: Int): String {
        val builder = StringBuilder()
        builder.append("Details about Item: ").append(position)
        for (i in 0 until position) {
            builder.append("\nMore details information here.")
        }
        return builder.toString()
    }

    /**
     * A dummy item representing a piece of content.
     */
    @Entity(tableName = "location")
    data class Location(
            @PrimaryKey(autoGenerate = true)
            var id: Int? = null
            , var title: String? = null, var latitude: String? = null, var longitude: String? = null) {
        override fun toString(): String {
            return "latitude $latitude longitude $longitude"
        }
    }
}
