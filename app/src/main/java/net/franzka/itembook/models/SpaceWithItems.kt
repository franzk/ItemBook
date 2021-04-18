package net.franzka.itembook.models

import androidx.room.Embedded
import androidx.room.Relation

data class SpaceWithItems(

    @Embedded val space: Space,

    @Relation(
        parentColumn = "spaceId",
        entityColumn = "spaceParentId"
    )
    val items: List<Item>

)
