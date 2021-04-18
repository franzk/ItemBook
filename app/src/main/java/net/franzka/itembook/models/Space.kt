package net.franzka.itembook.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Space(

    @PrimaryKey(autoGenerate = true)
    val spaceId: Long?,

    var name: String,

    var imagePath: String

) : Parcelable