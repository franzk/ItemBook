package net.franzka.itembook.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import net.franzka.itembook.databinding.SpaceListItemBinding
import net.franzka.itembook.models.Space
import net.franzka.itembook.models.SpaceWithItems

class SpaceAdapter(private val clickListener: SpaceClickListener) : ListAdapter<SpaceWithItems, SpaceAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).space.spaceId!!
    }

    class ViewHolder private constructor(val binding: SpaceListItemBinding, private val parent: ViewGroup) : RecyclerView.ViewHolder(binding.root) {

        fun bind(spaceWithItems: SpaceWithItems, clickListener: SpaceClickListener) {
            binding.apply {
                this.spaceWithItems = spaceWithItems
                this.clickListener = clickListener
                executePendingBindings()
            }

            if (spaceWithItems.space.imagePath.isNotEmpty()) {
                Glide.with(parent).load(spaceWithItems.space.imagePath).dontAnimate()
                    .centerCrop().into(binding.imageSpace)
            }

        }


        companion object {
            fun from(parent: ViewGroup) : ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = SpaceListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding, parent)
            }
        }

    }

    class DiffCallback : DiffUtil.ItemCallback<SpaceWithItems>() {

        override fun areItemsTheSame(oldItem: SpaceWithItems, newItem: SpaceWithItems): Boolean {
            return oldItem.space.spaceId == newItem.space.spaceId
        }


        override fun areContentsTheSame(oldItem: SpaceWithItems, newItem: SpaceWithItems): Boolean {
            return oldItem == newItem
        }

    }

}


class SpaceClickListener(val clickListener: (space: Space) -> Unit) {
    fun onClick(space: Space) = clickListener(space)
}
