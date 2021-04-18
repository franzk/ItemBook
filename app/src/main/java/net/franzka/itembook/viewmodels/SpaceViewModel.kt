package net.franzka.itembook.viewmodels

import android.widget.ImageView
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import net.franzka.itembook.models.Item
import net.franzka.itembook.models.Space
import net.franzka.itembook.models.SpaceWithItems
import net.franzka.itembook.room.SpaceRepository
import net.franzka.itembook.utils.SaveFile

class SpaceViewModel(private val repository: SpaceRepository) : ViewModel() {

    companion object {
        private const val TAG = "SpaceViewModel"
    }

    private var _spaceId: Long? = null
    val spaceId: Long? get() = _spaceId
    fun setSpaceId(spaceId: Long?) { this._spaceId = spaceId }

    val name = MutableLiveData<String>()
    val imagePath = MutableLiveData<String>()
    val items = MutableLiveData<List<Item>>()

    val allSpaces: LiveData<List<SpaceWithItems>> = repository.allSpaces.asLiveData() // pour SpaceListFragment

    val dbUpdated = MutableLiveData<Boolean>()

    lateinit var localFilesDir: String

    fun loadSpaceWithItems() {
        _spaceId?.let {
            viewModelScope.launch {
                val swi = repository.loadSpaceWithItems(it)
                setSpaceData(swi.space)
                items.value = swi.items
            }
        }
    }

    fun insert(imageSpace: ImageView?) = viewModelScope.launch {
        dbUpdated.value = false
        imageSpace?.let { imagePath.value = SaveFile.savePic(it, localFilesDir) }
        _spaceId = repository.insert(getSpace())
        dbUpdated.value = true
    }

    fun update(imageSpace: ImageView?) = viewModelScope.launch {
        dbUpdated.value = false
        imageSpace?.let { imagePath.value = SaveFile.savePic(it, localFilesDir) }
        repository.update(getSpace())
        dbUpdated.value = true
    }

    fun delete() = viewModelScope.launch {
        repository.delete(getSpace())
    }

    fun setSpaceData(space: Space) {
        _spaceId = space.spaceId
        name.value = space.name
        imagePath.value = space.imagePath
    }

    fun getSpace(): Space {
        return Space(_spaceId, name.value ?: "", imagePath.value ?: "")
    }

}



class SpaceViewModelFactory(private val repository: SpaceRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SpaceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SpaceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}