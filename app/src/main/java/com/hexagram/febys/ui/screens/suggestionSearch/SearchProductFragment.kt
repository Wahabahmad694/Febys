package com.hexagram.febys.ui.screens.suggestionSearch

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.hexagram.febys.NavGraphDirections
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentSearchProductBinding
import com.hexagram.febys.ui.screens.product.listing.ProductListingViewModel
import com.hexagram.febys.utils.*
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
    private var isFirstSearchVisible: Boolean? = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSearchProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        uiListener()
        setMyJobsPagingAdapter()
        setObserver()
        setOnEditTextChange()
    }

    private fun initUi() {
        binding.etSearch.isFocusedByDefault = true
        binding.etSearch.isCursorVisible = true
        val imgr: InputMethodManager =
            activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    private fun setObserver() {
        productListingViewModel.getTotalProducts().observe(viewLifecycleOwner) {
            showEmptyView(it)
        }
    }

    private fun setOnEditTextChange() {
        binding.etSearch.addTextChangedListener {
            val search = it?.toString() ?: ""
            isFirstSearchVisible = search.isEmpty()
            binding.ivClear.isVisible = binding.etSearch.text.isNotEmpty()
        }
    }

    private fun showEmptyView(it: Long?) {
        Log.d("TAG_SEARCH", "showEmptyView: $it")
        if (it == -1L) {
            binding.searchTypeView.root.show()
            binding.emptyView.root.hide()
        } else if (it == 0L && isFirstSearchVisible == false) {
            binding.searchTypeView.root.hide()
            binding.emptyView.root.show()
        } else if (it == 0L && isFirstSearchVisible == true) {
            binding.searchTypeView.root.show()
            binding.emptyView.root.hide()
        } else {
            binding.searchTypeView.root.hide()
            binding.emptyView.root.hide()
        }
    }

    private fun setMyJobsPagingAdapter() {

        suggestedProductsAdapter = SearchSuggestionAdapter {
            val productId = it.productId
            val gotoProductDetail =
                NavGraphDirections.actionToProductDetail(productId, "")
            navigateTo(gotoProductDetail)

        }
        binding.rvProductSearch.adapter = suggestedProductsAdapter

    }


    private fun uiListener() {

        binding.ivBack.setOnClickListener {
            goBack()
        }

        binding.ivSearch.setOnClickListener {
            onSearchClick()
        }
        binding.ivClear.setOnClickListener {
            binding.etSearch.setText("")
            binding.searchTypeView.root.show()
            binding.emptyView.root.hide()
        }

        startSearch()
    }

    private fun onSearchClick() {
        hideKeyboard()
        val query = binding.etSearch.text.toString()
        doSearch(query)

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
                        } else {
                            getProducts(result)

                        }
                    }
            }

        }
    }

    private fun getProducts(search: String? = "") {
        job?.cancel()
        job = lifecycleScope.launch {
            productListingViewModel.searchProductsListing(search).collectLatest { pagingData ->
                suggestedProductsAdapter.submitData(pagingData)
            }
        }
    }

}