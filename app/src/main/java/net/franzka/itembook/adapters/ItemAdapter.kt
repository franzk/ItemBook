package net.franzka.itembook.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import net.franzka.itembook.R
import net.franzka.itembook.databinding.ItemListItemBinding
import net.franzka.itembook.models.Item

class ItemAdapter(private val clickListener: ItemClickListener) : ListAdapter<Item, ItemAdapter.ViewHolder>(DiffCallback()) {

    companion object {
        private const val TAG = "ItemAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).itemId!!
    }

    class ViewHolder private constructor(val binding: ItemListItemBinding, private val parent: ViewGroup) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item, clickListener: ItemClickListener) {
            binding.apply {
                this.item = item
                this.clickListener = clickListener
                executePendingBindings()
            }

            if (item.imagePath.isNotEmpty()) {
                Glide.with(parent).load(item.imagePath).dontAnimate()
                        .centerCrop().into(binding.imageItem)
            }
        }

        companion object {
            fun from(parent: ViewGroup) : ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding, parent)
            }
        }

    }

    class DiffCallback : DiffUtil.ItemCallback<Item>() {

        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.itemId == newItem.itemId
        }


        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }

    }

}


class ItemClickListener(val clickListener: (item: Item) -> Unit) {
    fun onClick(item: Item) = clickListener(item)
}