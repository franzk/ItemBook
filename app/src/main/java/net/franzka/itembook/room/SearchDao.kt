package net.franzka.itembook.room

import androidx.room.Dao
import androidx.room.Query
import net.franzka.itembook.models.SearchResult
import net.franzka.itembook.utils.Constants

@Dao
interface SearchDao {

    @Query(
    "SELECT id, type, name, imagePath, IFNULL(group_concat(Tag.tagName), '') AS tags FROM " +
                " (SELECT Item.itemId AS id, '${Constants.ITEM}' AS type, Item.name, Item.imagePath " +
                " FROM Tag " +
                "    LEFT JOIN Item ON Item.itemId = Tag.itemParentId " +
                "WHERE Tag.tagName LIKE :query " +

            " UNION " +

                " SELECT itemId AS id, '${Constants.ITEM}' AS type, name, imagePath " +
                 " FROM Item " +
                 " WHERE name LIKE :query " +

            " UNION " +

            " SELECT spaceId AS id, '${Constants.SPACE}' AS type, name, imagePath " +
            "FROM Space " +
            "WHERE name LIKE :query) " +

        "LEFT JOIN Tag ON (Tag.itemParentId = id AND type='${Constants.ITEM}') " +
        " GROUP BY id, type"

    )

    suspend fun getSearchResults(query: String) : List<SearchResult>

}