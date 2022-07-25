package com.hexagram.febys.ui.screens.searchProductFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentSearchProductBinding
import com.hexagram.febys.models.api.request.SearchRequest
import com.hexagram.febys.ui.screens.product.listing.ProductListingViewModel
import com.hexagram.febys.utils.goBack
import com.hexagram.febys.utils.onSearch
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SearchProductFragment : BaseFragment() {
    private lateinit var binding: FragmentSearchProductBinding
    private val productListingViewModel: ProductListingViewModel by viewModels()
    private var job: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSearchProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uiListener()
    }

    private fun uiListener() {
        binding.ivBack.setOnClickListener {
            goBack()
        }

        binding.ivClear.setOnClickListener { binding.etSearch.setText("") }

        binding.etSearch.addTextChangedListener {
            binding.ivClear.isVisible = binding.etSearch.text.isNotEmpty()
        }

        startSearch()

//        private fun doSearch(query: String) {
//            val gotoSearch =
//                SearchFragmentDirections.actionSearchFragmentToSearchProductListingFragment(
//                    query
//                )
//            navigateTo(gotoSearch)
//        }
    }

    fun startSearch() {


        binding.etSearch.onSearch {

            lifecycleScope.launchWhenCreated {

                productListingViewModel.searchProductsSuggestionListing(
                    true,
                    SearchRequest(searchStr = binding.etSearch.text.toString())
                )
                {
                    Log.e("search_tag", it.toString())

                }.collect {
                    Log.e("search_tag", it.toString())

                }
            }


        }

//
//        job?.cancel()

//        job = lifecycleScope.launchWhenStarted {
//
//            binding.apply {
//
//                etSearch.getQueryTextChangeStateFlow()
//                    .debounce(300)
//                    .distinctUntilChanged()
//                    .flowOn(Dispatchers.Main)
//                    .collect { result ->
//
//                        productListingViewModel.searchProductsSuggestionListing(
//                            true,
//                            SearchRequest(searchStr = result)
//                        )
//                        {
//                            Log.e("search_tag", it.toString())
//
//                        }.collect {
//                            Log.e("search_tag", it.toString())
//
//                        }
//                    }
//
//
//            }
//
//
//        }
    }

}