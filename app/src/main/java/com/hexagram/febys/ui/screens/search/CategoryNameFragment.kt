package com.hexagram.febys.ui.screens.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentCategoryNameBinding
import com.hexagram.febys.models.api.category.Category
import com.hexagram.febys.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoryNameFragment : BaseFragment() {
    private lateinit var binding: FragmentCategoryNameBinding
    private val searchViewModel: SearchViewModel by hiltNavGraphViewModels(R.id.nav_graph)
    private val args: CategoryNameFragmentArgs by navArgs()

    private var isFirstPage = false

    private val pagerAdapter = CategoryNamePagerAdapter()

    // without pagination
    private val simpleAdapter = CategoryNameAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isFirstPage = arguments?.getBoolean(KEY_FIRST_PAGE) ?: false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        uiListeners()
    }

    private fun initUi() {
        binding.rvCategoryName.setHasFixedSize(true)
        binding.rvCategoryName.addItemDecoration(
            DividerItemDecoration(
                binding.rvCategoryName.context,
                (binding.rvCategoryName.layoutManager as LinearLayoutManager).orientation
            )
        )

        binding.showTopBar = !isFirstPage
        if (isFirstPage) {
            setupCategoryPagerAdapter()
        } else {
            binding.categoryName = "${args.parentName}${args.category.name}"
            setupCategorySimpleAdapter(args.category.children)
        }
    }

    private fun uiListeners() {
        binding.ivBack.setOnClickListener {
            goBack()
        }

        simpleAdapter.interaction = object : CategoryNameAdapter.Interaction {
            override fun onItemSelected(position: Int, item: Category) {
                if (item.hasChild) {
                    openChild(item)
                } else {
                    openProductListing(item.name, item.id)
                }
            }
        }

        pagerAdapter.interaction = object : CategoryNamePagerAdapter.Interaction {
            override fun onItemSelected(position: Int, item: Category) {
                if (item.hasChild) {
                    openChild(item)
                } else {
                    openProductListing(item.name, item.id)
                }
            }
        }
    }

    private fun setupCategorySimpleAdapter(categories: List<Category>) {
        binding.rvCategoryName.adapter = simpleAdapter
        simpleAdapter.submitList(categories)
        binding.emptyView.root.isVisible=categories.isEmpty()
    }

    private fun setupCategoryPagerAdapter() {
        binding.rvCategoryName.adapter = pagerAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            searchViewModel.allCategoryPagingData.collectLatest { pagingData ->
                pagerAdapter.submitData(pagingData)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            pagerAdapter.loadStateFlow.collectLatest {
                val state = it.refresh
                if (state is LoadState.Loading) {
                    showLoader()
                } else {
                    hideLoader()
                }

                if (state is LoadState.Error) {
                    showErrorToast(state)
                }
                binding.emptyView.root.isVisible=
                    it.refresh is LoadState.NotLoading && pagerAdapter.itemCount < 1
            }
        }
    }

    private fun openChild(category: Category) {
        val navigateToCategoryName =
            if (isFirstPage) {
                SearchFragmentDirections.actionSearchFragmentToCategoryNameFragment(
                    category, ""
                )
            } else {
                CategoryNameFragmentDirections.actionCategoryNameFragmentSelf(
                    category, "${binding.tvCategoryName.text} / "
                )
            }
        navigateTo(navigateToCategoryName)
    }

    private fun openProductListing(categoryTitle: String, categoryId: Int) {
        val navigateToProductListing = if (isFirstPage) {
            SearchFragmentDirections
                .actionSearchFragmentToCategoryProductListingFragment(categoryTitle, categoryId)
        } else {
            CategoryNameFragmentDirections
                .actionCategoryNameFragmentToCategoryProductListingFragment(
                    categoryTitle, categoryId
                )
        }
        navigateTo(navigateToProductListing)
    }

    override fun getTvCartCount(): TextView = binding.tvCartCount
    override fun getIvCart(): View = binding.ivCart

    companion object {
        private const val KEY_FIRST_PAGE = "keyFirstPage"

        @JvmStatic
        fun newInstance() = CategoryNameFragment().apply {
            arguments = Bundle().apply {
                putBoolean(KEY_FIRST_PAGE, true)
            }
        }
    }
}