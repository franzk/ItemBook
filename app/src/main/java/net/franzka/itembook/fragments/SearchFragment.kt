package net.franzka.itembook.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import net.franzka.itembook.ItembookApplication
import net.franzka.itembook.R
import net.franzka.itembook.adapters.SearchAdapter
import net.franzka.itembook.adapters.SearchResultClickListener
import net.franzka.itembook.databinding.FragmentSearchBinding
import net.franzka.itembook.models.Item
import net.franzka.itembook.models.SearchResult
import net.franzka.itembook.models.Space
import net.franzka.itembook.utils.Constants
import net.franzka.itembook.viewmodels.SearchViewModel
import net.franzka.itembook.viewmodels.SearchViewModelFactory

class SearchFragment : Fragment() {

    companion object {
        private const val TAG = "SearchFragment"
    }

    private lateinit var binding: FragmentSearchBinding

    private var queryArgument: String? = null


    private val searchViewModel: SearchViewModel by viewModels {
        SearchViewModelFactory((activity?.application as ItembookApplication).searchRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        arguments?.let { it ->
            val args = SearchFragmentArgs.fromBundle(it)
            queryArgument = args.query
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchAdapter = SearchAdapter(SearchResultClickListener { searchResult -> openSearchResult(searchResult) })

        searchViewModel.apply {

            searchQueryResults.observe(viewLifecycleOwner, {
                searchAdapter.submitList(it)
            })

        }

        binding.recyclerviewSearchResults.apply {
            animation = null
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

    }



    private fun openSearchResult(searchResult: SearchResult) {
        when(searchResult.type) {
            Constants.ITEM -> {
                val directions = SearchFragmentDirections.actionSearchFragmentToItemDetailFragment(
                    Item(searchResult.id, -1, "", "", 0))
                findNavController().navigate(directions)
            }
            Constants.SPACE -> {
                val directions = SearchFragmentDirections.actionSearchFragmentToSpaceItemListFragment(
                    Space(searchResult.id, "", ""))
                findNavController().navigate(directions)
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        val menuItem = menu.findItem(R.id.searchFragment)
        val searchView = menuItem.actionView as SearchView


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {

                val queryString = queryArgument ?: query
                queryArgument?.let {
                    queryArgument = null
                }
                val q = queryString ?: ""
                val s = "%$q%"
                searchViewModel.searchQuery(s)
                return true
            }
        })



        menuItem.setOnActionExpandListener( object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                return findNavController().navigateUp()
            }
        })
    }


}