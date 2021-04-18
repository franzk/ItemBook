package net.franzka.itembook.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import net.franzka.itembook.models.Item
import net.franzka.itembook.models.ItemWithTags

@Dao
interface ItemDao {

    @Query("SELECT * FROM item")
    fun getAll(): Flow<List<Item>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Item): Long

    @Update
    suspend fun update(item: Item)

    @Delete
    suspend fun delete(item: Item)

    @Transaction
    @Query("SELECT * FROM item WHERE itemId = :itemId")
    suspend fun loadItemWithTags(itemId: Long): ItemWithTags

}