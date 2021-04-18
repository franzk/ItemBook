package net.franzka.itembook.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import net.franzka.itembook.models.Space
import net.franzka.itembook.models.SpaceWithItems

@Dao
interface SpaceDao {

    @Query("SELECT * FROM space")
    fun getAll(): Flow<List<SpaceWithItems>>

    @Transaction
    @Query("SELECT * FROM space WHERE spaceId = :spaceId")
    suspend fun loadSpaceWithItems(spaceId: Long): SpaceWithItems

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(space: Space): Long

    @Update
    suspend fun update(space: Space)

    @Delete
    suspend fun delete(space: Space)


}