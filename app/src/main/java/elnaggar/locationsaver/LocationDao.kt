package elnaggar.locationsaver


import android.arch.persistence.room.*

@Dao
interface LocationDao {

    @get:Query("SELECT * FROM location")
    val all: List<Location>


    @Delete
    fun delete(location:Location)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(location: Location)


}
