package elnaggar.locationsaver


import android.arch.persistence.room.*
import elnaggar.locationsaver.dummy.DummyContent

@Dao
interface LocationDao {

    @get:Query("SELECT * FROM location")
    val all: List<DummyContent.Location>


    @Delete
    fun delete(location: DummyContent.Location)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(location: DummyContent.Location)


}
