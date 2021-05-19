package net.franzka.itembook.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.franzka.itembook.models.SearchResult
import net.franzka.itembook.room.SearchRepository

class SearchViewModel(private val searchRepository: SearchRepository) : ViewModel() {

    companion object {
        private const val TAG = "SearchViewModel"
    }

    val searchQueryResults = MutableLiveData<List<SearchResult>>()

    fun searchQuery(query: String) {
        viewModelScope.launch {
            searchQueryResults.value = searchRepository.getSearchQueryResults(query)
        }
    }

}


class SearchViewModelFactory(private val searchRepository: SearchRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(searchRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}