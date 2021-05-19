package net.franzka.itembook.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import net.franzka.itembook.databinding.SearchResultListItemBinding
import net.franzka.itembook.models.SearchResult
import net.franzka.itembook.utils.Utils

class SearchAdapter(private val clickListener: SearchResultClickListener) : ListAdapter<SearchResult, SearchAdapter.ViewHolder>(DiffCallback()) {

    companion object {
        private const val TAG = "SearchAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).id
    }

    class ViewHolder private constructor(val binding: SearchResultListItemBinding, private val parent: ViewGroup) : RecyclerView.ViewHolder(binding.root) {

        fun bind(searchResult: SearchResult, clickListener: SearchResultClickListener) {
            binding.apply {
                this.searchResult = searchResult
                this.clickListener = clickListener
                executePendingBindings()
            }

            if (searchResult.imagePath.isNotEmpty()) {
                Glide.with(parent).load(searchResult.imagePath).dontAnimate()
                        .centerCrop().into(binding.imageResult)
            }

            binding.chipGroupTags.removeAllViews()

            if (searchResult.tags.isNotEmpty()) {
                val tags = searchResult.tags.split(",")

                tags.forEach {
                    Utils.addChipToChipGroup(
                        binding.chipGroupTags,
                        it,
                        binding.chipGroupTags.context
                    ) { }
                }
            }

        }

        companion object {
            fun from(parent: ViewGroup) : ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = SearchResultListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding, parent)
            }
        }

    }

    class DiffCallback : DiffUtil.ItemCallback<SearchResult>() {

        override fun areItemsTheSame(oldItem: SearchResult, newItem: SearchResult): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SearchResult, newItem: SearchResult): Boolean {
            return oldItem == newItem
        }


    }

}


class SearchResultClickListener(val clickListener: (searchResult: SearchResult) -> Unit) {
    fun onClick(searchResult: SearchResult) = clickListener(searchResult)
}