package com.hexagram.febys.ui.screens.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentCategoryNameBinding
import com.hexagram.febys.network.response.Category
import com.hexagram.febys.utils.goBack
import com.hexagram.febys.utils.navigateTo
import com.hexagram.febys.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoryNameFragment : BaseFragment() {
    private lateinit var binding: FragmentCategoryNameBinding
    private val viewModel: SearchViewModel by viewModels()
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
                    openProductListing(item.name)
                }
            }
        }

        pagerAdapter.interaction = object : CategoryNamePagerAdapter.Interaction {
            override fun onItemSelected(position: Int, item: Category) {
                if (item.hasChild) {
                    openChild(item)
                } else {
                    openProductListing(item.name)
                }
            }
        }
    }

    private fun setupCategorySimpleAdapter(categories: List<Category>) {
        binding.rvCategoryName.adapter = simpleAdapter
        simpleAdapter.submitList(categories)
    }

    private fun setupCategoryPagerAdapter() {
        binding.rvCategoryName.adapter = pagerAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allCategoryPagingData.collectLatest { pagingData ->
                pagerAdapter.submitData(pagingData)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            pagerAdapter.loadStateFlow.collectLatest {
                val state = it.refresh
                binding.progressBar.isVisible = state is LoadState.Loading

                if (state is LoadState.Error) {
                    showToast(getString(R.string.error_something_went_wrong))
                }
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

    private fun openProductListing(name: String) {
        val navigateToProductListing = if (isFirstPage) {
            SearchFragmentDirections
                .actionSearchFragmentToCategoryProductListingFragment(name)
        } else {
            CategoryNameFragmentDirections
                .actionCategoryNameFragmentToCategoryProductListingFragment(name)
        }
        navigateTo(navigateToProductListing)
    }

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