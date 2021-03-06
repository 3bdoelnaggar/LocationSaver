package elnaggar.locationsaver

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase


@Database(entities = [(Location::class)], version = Constants.DATABASE_VERSION)
abstract class AppDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
}
