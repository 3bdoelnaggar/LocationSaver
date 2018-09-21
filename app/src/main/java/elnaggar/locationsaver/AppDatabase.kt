package elnaggar.locationsaver

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import elnaggar.alexandriarestaurants.db.Constants

import elnaggar.locationsaver.dummy.DummyContent

@Database(entities = [(DummyContent.Location::class)], version = Constants.DATABASE_VERSION)
abstract class AppDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
}
