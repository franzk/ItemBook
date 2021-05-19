package net.franzka.itembook.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import net.franzka.itembook.ItembookApplication
import net.franzka.itembook.R
import net.franzka.itembook.databinding.FragmentItemDetailBinding
import net.franzka.itembook.utils.Utils
import net.franzka.itembook.viewmodels.ItemViewModel
import net.franzka.itembook.viewmodels.ItemViewModelFactory

class ItemDetailFragment : Fragment() {

    companion object {
        private const val TAG = "ItemDetailFragment"
    }

    private lateinit var binding: FragmentItemDetailBinding

    private val itemViewModel: ItemViewModel by viewModels {
        ItemViewModelFactory(
            (activity?.application as ItembookApplication).itemRepository,
            (activity?.application as ItembookApplication).tagRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val args = ItemDetailFragmentArgs.fromBundle(it)
            itemViewModel.setItemId(args.item.itemId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_item_detail, container, false)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = itemViewModel
            fragmentItemDetail = this@ItemDetailFragment
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        itemViewModel.apply {

            // load item from database
            loadItemWithTags()

            // image
            imagePath.observe(viewLifecycleOwner, {
                if (it.isNotEmpty()) {
                    Glide.with(this@ItemDetailFragment).load(it).dontAnimate()
                        .centerCrop().into(binding.imageItem)
                } else {
                    binding.imageItem.setImageResource(R.drawable.ic_item)
                }
            })

            //tags
            populateTags()
        }
    }

    private fun populateTags() {

        binding.chipGroupTags.removeAllViews()
        itemViewModel.getItemTags().observe(viewLifecycleOwner, { tags ->
            tags.forEach { tag ->
                Utils.addChipToChipGroup(binding.chipGroupTags,
                    tag.tagName,
                    requireContext(),
                    chipClickListener)
            }
        })

    }


    private val chipClickListener = View.OnClickListener {
        val directions = ItemDetailFragmentDirections.actionItemDetailFragmentToSearchFragment(
            it.tag as String?
        )
        findNavController().navigate(directions)
    }


    fun editItem() {
        val directions = ItemDetailFragmentDirections.actionItemDetailFragmentToItemFormFragment(
            ItemFormFragment.Companion.Mode.EDIT, itemViewModel.getItem())
        findNavController().navigate(directions)
    }

    fun delete() {
        itemViewModel.delete()
        findNavController().navigateUp()
    }

}