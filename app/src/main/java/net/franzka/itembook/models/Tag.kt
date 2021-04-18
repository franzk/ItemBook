package net.franzka.itembook.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
        foreignKeys = [
            ForeignKey(
                    entity = Item::class,
                    parentColumns = ["itemId"],
                    childColumns = ["itemParentId"],
                    onDelete = ForeignKey.CASCADE)
        ]
)
data class Tag(

    @PrimaryKey(autoGenerate = true)
    val tagId: Long?,

    val itemParentId: Long,

    var tagName: String

)

