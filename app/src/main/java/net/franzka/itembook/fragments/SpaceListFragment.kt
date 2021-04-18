package net.franzka.itembook.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import net.franzka.itembook.ItembookApplication
import net.franzka.itembook.R
import net.franzka.itembook.viewmodels.SpaceViewModel
import net.franzka.itembook.viewmodels.SpaceViewModelFactory
import net.franzka.itembook.adapters.SpaceAdapter
import net.franzka.itembook.adapters.SpaceClickListener
import net.franzka.itembook.databinding.FragmentSpaceListBinding
import net.franzka.itembook.models.Space
import net.franzka.itembook.utils.Constants


class SpaceListFragment : Fragment() {

    companion object {
        private const val TAG = "SpaceListFragment"
    }

    private lateinit var binding: FragmentSpaceListBinding

    private val spaceViewModel: SpaceViewModel by viewModels {
        SpaceViewModelFactory((activity?.application as ItembookApplication).spaceRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_space_list, container, false)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            spaceListFragment = this@SpaceListFragment
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spaceAdapter = SpaceAdapter(SpaceClickListener { space -> openSpace(space) })
        spaceAdapter.setHasStableIds(true)

        spaceViewModel.allSpaces.observe(viewLifecycleOwner, { spacesWithItems ->
            spacesWithItems?.let { spaceAdapter.submitList(it)}
        })

        binding.recyclerviewSpaces.apply {
            adapter = spaceAdapter
            animation = null
            layoutManager = GridLayoutManager(requireContext(), Constants.SPACE_COL_COUNT)
        }

    }

    private fun openSpace(space: Space) {
        val directions = SpaceListFragmentDirections.actionSpaceListFragmentToSpaceItemListFragment(space)
        findNavController().navigate(directions)
    }

    fun addSpace() {
        val directions = SpaceListFragmentDirections.actionSpaceListFragmentToSpaceFormFragment(SpaceFormFragment.Companion.Mode.CREATE)
        findNavController().navigate(directions)
    }



}