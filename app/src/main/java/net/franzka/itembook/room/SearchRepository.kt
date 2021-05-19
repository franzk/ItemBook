package net.franzka.itembook.room

import net.franzka.itembook.models.SearchResult

class SearchRepository(private val searchDao: SearchDao) {

    suspend fun getSearchQueryResults(query: String): List<SearchResult> {
        return searchDao.getSearchResults(query)
    }

}