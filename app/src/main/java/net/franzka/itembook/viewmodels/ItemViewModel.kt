package net.franzka.itembook.viewmodels

import android.widget.ImageView
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import net.franzka.itembook.models.Item
import net.franzka.itembook.models.Tag
import net.franzka.itembook.room.ItemRepository
import net.franzka.itembook.room.TagRepository
import net.franzka.itembook.utils.SaveFile

class ItemViewModel(private val itemRepository: ItemRepository, private val tagRepository: TagRepository) : ViewModel() {

    companion object  {
        private const val TAG = "ItemViewModel"
    }

    private var _itemId: Long? = null
    val itemId: Long? get() = _itemId
    fun setItemId(itemId: Long?) { this._itemId = itemId }

    private var spaceParentId: Long? = null

    val name = MutableLiveData<String>()
    val imagePath = MutableLiveData<String>()
    val quantity = MutableLiveData<Int>()
    private val tags = MutableLiveData<List<Tag>>()

    val allTags: LiveData<List<String>> = tagRepository.allTags.asLiveData()

    val dbUpdated = MutableLiveData<Boolean>()

    lateinit var localFilesDir: String

    fun loadItemWithTags() {
        _itemId?.let {
            viewModelScope.launch {
                val iwt = itemRepository.loadItemWithTags(it)
                setItemData(iwt.item)
                tags.value = iwt.tags
            }
        }
    }

    fun getItemTags(): LiveData<List<Tag>> {
        return tagRepository.getItemTags(_itemId ?: -1).asLiveData()
    }

    fun insert(tags: List<String>, imageItem: ImageView?) = viewModelScope.launch {
        dbUpdated.value = false
        imageItem?.let { imagePath.value = SaveFile.savePic(it, localFilesDir) }
        itemRepository.insert(getItem(), tags)
        dbUpdated.value = true
    }

    fun update(tags: List<String>, imageItem: ImageView?) = viewModelScope.launch {
        dbUpdated.value = false
        imageItem?.let { imagePath.value = SaveFile.savePic(it, localFilesDir) }
        itemRepository.update(getItem(), tags)
        dbUpdated.value = true
    }

    fun delete() = viewModelScope.launch {
        itemRepository.delete(getItem())
    }

    fun setItemData(item: Item) {
        _itemId = item.itemId
        spaceParentId = item.spaceParentId
        name.value = item.name
        imagePath.value = item.imagePath
        quantity.value = item.quantity
    }

    fun getItem(): Item {
        return Item(_itemId, spaceParentId ?: -1,
            name.value ?: "", imagePath.value ?: "",quantity.value ?: 0)
    }

    fun addToItemQuantity(i: Int) {
        quantity.value = quantity.value?.plus(i)
    }

}

class ItemViewModelFactory(private val itemRepository: ItemRepository, private val tagRepository: TagRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ItemViewModel(itemRepository, tagRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
