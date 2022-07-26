package com.hexagram.febys.ui.screens.searchProductFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.hexagram.febys.NavGraphDirections
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentSearchProductBinding
import com.hexagram.febys.ui.screens.product.listing.ProductListingViewModel
import com.hexagram.febys.utils.getQueryTextChangeStateFlow
import com.hexagram.febys.utils.goBack
import com.hexagram.febys.utils.hideKeyboard
import com.hexagram.febys.utils.navigateTo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchProductFragment : BaseFragment() {
    private lateinit var binding: FragmentSearchProductBinding
    private val productListingViewModel: ProductListingViewModel by viewModels()
    private var job: Job? = null
    private lateinit var suggestedProductsAdapter: SearchSuggestionAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSearchProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uiListener()
        setMyJobsPagingAdapter()
    }

    private fun setMyJobsPagingAdapter() {

        suggestedProductsAdapter = SearchSuggestionAdapter {
            val productId = it.productId
            val gotoProductDetail =
                NavGraphDirections.actionToProductDetail(productId, "")
            navigateTo(gotoProductDetail)

        }

        binding.rvProductSearch.adapter = suggestedProductsAdapter

        suggestedProductsAdapter.addLoadStateListener {

//            handleProgressPlaceholder(it)
        }
    }


    private fun uiListener() {

        binding.ivBack.setOnClickListener {
            goBack()
        }

        binding.ivSearch.setOnClickListener {
            onSearchClick()
        }
        binding.ivClear.setOnClickListener { binding.etSearch.setText("") }

        binding.etSearch.addTextChangedListener {
            binding.ivClear.isVisible = binding.etSearch.text.isNotEmpty()
        }
        startSearch()
    }

    private fun onSearchClick() {
        hideKeyboard()
        val query = binding.etSearch.text.toString()
        if (query.isNotEmpty()) {
            doSearch(query)
        }
    }

    private fun doSearch(query: String) {
        val gotoSearch =
            SearchProductFragmentDirections.actionSearchProductFragmentToSearchProductListingFragment(
                query
            )
        navigateTo(gotoSearch)
    }


    private fun startSearch() {

        lifecycleScope.launchWhenStarted {

            binding.apply {

                etSearch.getQueryTextChangeStateFlow()
                    .debounce(50)
                    .distinctUntilChanged()
                    .flowOn(Dispatchers.Main)
                    .collect { result ->
                        if (result == null) {
                            getProducts(binding.etSearch.text.toString())
                        }
                        else{
                            getProducts(result)

                        }
                    }
            }

        }
    }

    private fun getProducts(search: String = "") {
        job?.cancel()
        job = lifecycleScope.launch {
            productListingViewModel.searchProductsListing(search).collectLatest { pagingData ->
                suggestedProductsAdapter.submitData(pagingData)
            }
        }
    }

}