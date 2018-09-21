package elnaggar.locationsaver

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey


/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 * TODO: Replace all uses of this class before publishing your app.
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
