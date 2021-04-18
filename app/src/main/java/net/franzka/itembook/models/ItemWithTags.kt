package net.franzka.itembook.models

import androidx.room.Embedded
import androidx.room.Relation

data class ItemWithTags(

        @Embedded var item: Item,

        @Relation(
                parentColumn = "itemId",
                entityColumn = "itemParentId"
        )
        val tags: List<Tag>

)