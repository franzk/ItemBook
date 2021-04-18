package net.franzka.itembook.models

import android.os.Parcelable
import androidx.room.*
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
        foreignKeys = [
            ForeignKey(
                    entity = Space::class,
                    parentColumns = ["spaceId"],
                    childColumns = ["spaceParentId"],
                    onDelete = ForeignKey.CASCADE)
        ]
)
data class Item(

    @PrimaryKey(autoGenerate = true)
    val itemId: Long?,

    var spaceParentId: Long,

    var name: String,

    var imagePath: String,

    var quantity: Int

) : Parcelable

