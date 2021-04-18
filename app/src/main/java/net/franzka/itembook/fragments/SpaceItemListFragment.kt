package net.franzka.itembook.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import net.franzka.itembook.ItembookApplication
import net.franzka.itembook.R
import net.franzka.itembook.adapters.ItemAdapter
import net.franzka.itembook.adapters.ItemClickListener
import net.franzka.itembook.databinding.FragmentSpaceItemListBinding
import net.franzka.itembook.models.Item
import net.franzka.itembook.utils.Constants
import net.franzka.itembook.viewmodels.SpaceViewModel
import net.franzka.itembook.viewmodels.SpaceViewModelFactory

class SpaceItemListFragment : Fragment() {

    companion object {
        private const val TAG = "SpaceItemListFragment"
    }

    private lateinit var binding: FragmentSpaceItemListBinding

    private val spaceViewModel: SpaceViewModel by viewModels {
        SpaceViewModelFactory((activity?.application as ItembookApplication).spaceRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val args = SpaceItemListFragmentArgs.fromBundle(it)
            spaceViewModel.setSpaceId(args.space.spaceId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_space_item_list, container, false)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            fragmentSpaceItemList = this@SpaceItemListFragment
            viewModel = spaceViewModel
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemAdapter = ItemAdapter(ItemClickListener { item -> openItem(item) })
        itemAdapter.setHasStableIds(true)

        spaceViewModel.apply {

            // load from database
            loadSpaceWithItems()

            // items
            items.observe(viewLifecycleOwner, {
                itemAdapter.submitList(it)
            })

            // image
            imagePath.observe(viewLifecycleOwner, {
                if (it.isNotEmpty()) {
                    Glide.with(this@SpaceItemListFragment).load(it).dontAnimate()
                        .centerCrop().into(binding.imageSpace)
                } else {
                    binding.imageSpace.setImageResource(R.drawable.ic_cube)
                }
            })
        }

        binding.recyclerviewItems.apply {
            animation = null
            adapter = itemAdapter
            layoutManager = GridLayoutManager(requireContext(), Constants.ITEMS_COL_COUNT)
        }
    }

    private fun openItem(item: Item) {
        val directions = SpaceItemListFragmentDirections.
                            actionSpaceItemListFragmentToItemDetailFragment(item)
        findNavController().navigate(directions)
    }

    fun createItem() {
        spaceViewModel.spaceId?.let {
            val directions =
                SpaceItemListFragmentDirections.actionSpaceItemListFragmentToItemFormFragment(
                    ItemFormFragment.Companion.Mode.CREATE,
                    Item(null, it, "", "", 0)
                )
            findNavController().navigate(directions)
        }
    }

    fun editSpace() {
        val directions = SpaceItemListFragmentDirections.actionSpaceItemListFragmentToSpaceFormFragment(
            SpaceFormFragment.Companion.Mode.EDIT,
            spaceViewModel.getSpace()
        )
        findNavController().navigate(directions)
    }

    fun delete() {
        spaceViewModel.delete()
        findNavController().navigateUp()
    }

}