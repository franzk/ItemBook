package net.franzka.itembook.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.franzka.itembook.models.Tag

@Dao
interface TagDao {

    @Query("SELECT DISTINCT tagName FROM Tag")
    fun getAllTags(): Flow<List<String>>

    @Query("SELECT * FROM Tag WHERE itemParentId = :itemId")
    fun getItemTags(itemId: Long): Flow<List<Tag>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(tag: Tag)

    @Query("DELETE FROM Tag WHERE itemParentId = :itemId")
    fun deleteItemTags(itemId: Long)

}
