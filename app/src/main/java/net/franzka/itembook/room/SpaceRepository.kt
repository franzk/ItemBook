package net.franzka.itembook.room

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import net.franzka.itembook.models.Space
import net.franzka.itembook.models.SpaceWithItems

class SpaceRepository(private val spaceDao: SpaceDao) {

    companion object {
        private const val TAG = "SpaceRepository"
    }

    val allSpaces: Flow<List<SpaceWithItems>> = spaceDao.getAll()

    suspend fun loadSpaceWithItems(spaceId: Long): SpaceWithItems {
        return spaceDao.loadSpaceWithItems(spaceId)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(space: Space): Long {
        return spaceDao.insert(space)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(space: Space) {
        spaceDao.update(space)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(space: Space) {
        spaceDao.delete(space)
    }

}