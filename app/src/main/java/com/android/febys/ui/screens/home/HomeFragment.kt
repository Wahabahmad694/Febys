package com.android.febys.ui.screens.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.android.febys.R
import com.android.febys.databinding.FragmentHomeBinding
import com.android.febys.models.Product
import com.android.febys.ui.base.BaseFragment
import com.android.febys.utils.*
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private val sliderAdapter = SliderHomeAdapter()
    private val uniqueCategoryAdapter = UniqueCategoryAdapter()
    private val todayDealsAdapter = HomeProductsAdapter()
    private val featuredCategoryProductsAdapter = HomeProductsAdapter()
    private val trendingProductsAdapter = HomeProductsAdapter()
    private val storeYouFollowAdapter = HomeStoresAdapter()
    private val under100DollarsItemAdapter = HomeProductsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
    }

    private fun initUi() {
        setupUniqueCategories()
        sliderSetup()

        // today deals
        productListSetup(
            binding.rvTodayDeals,
            todayDealsAdapter,
            viewModel.fetchTodayDeals()
        )

        // featured categories
        viewModel.fetchFeaturedCategories().observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {

                }
                is Resource.Error -> {

                }
                is Resource.Data -> {
                    val featuredCategories = it.data
                    featuredCategories.forEach { category ->
                        val chip =
                            layoutInflater.inflate(
                                R.layout.item_featured_category,
                                binding.chipGroupFeaturedCategories,
                                false
                            ) as Chip
                        chip.id = category.id
                        chip.text = category.name
                        binding.chipGroupFeaturedCategories.addView(chip)
                    }
                }
            }
        }

        // featured category products
        productListSetup(
            binding.rvFeaturedCategoryProducts,
            featuredCategoryProductsAdapter,
            viewModel.fetchFeaturedCategoryProducts()
        )

        // seasonal feature
        seasonalOfferSetup()

        // trending products
        productListSetup(
            binding.rvTrendingProducts,
            trendingProductsAdapter,
            viewModel.fetchTrendingProducts()
        )

        // under 100$ items
        productListSetup(
            binding.rvUnder100DollarsItems,
            under100DollarsItemAdapter,
            viewModel.fetchUnder100DollarsItems()
        )
    }

    private fun setupUniqueCategories() {
        binding.rvUniqueCategories.adapter = uniqueCategoryAdapter
        viewModel.fetchUniqueCategory().observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {

                }
                is Resource.Error -> {

                }
                is Resource.Data -> {
                    val uniqueCategories = it.data
                    uniqueCategoryAdapter.submitList(uniqueCategories)
                }
            }
        }

        // custom scroll bar position
        binding.rvUniqueCategories.setOnScrollChangeListener { _, _, _, _, _ ->
            val horizontalScrollPosition = binding.rvUniqueCategories.getHorizontalScrollPosition()
            val param =
                (binding.ivIcScrollUniqueCategory.layoutParams as ConstraintLayout.LayoutParams)
            param.horizontalBias = horizontalScrollPosition
            binding.ivIcScrollUniqueCategory.layoutParams = param
        }
    }

    private fun sliderSetup() {
        binding.imageSliderHome.setSliderAdapter(sliderAdapter)
        viewModel.fetchSliderImages().observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {

                }
                is Resource.Error -> {

                }
                is Resource.Data -> {
                    binding.imageSliderHome.show()
                    val sliderImages = it.data
                    sliderAdapter.submitList(sliderImages)
                    binding.imageSliderHome.setInfiniteAdapterEnabled(true)
                    binding.imageSliderHome.startAutoCycle()
                }
            }
        }
    }

    private fun seasonalOfferSetup() {
        binding.rvStoreYouFollow.apply {
            addItemDecoration(
                SpaceItemDecoration(
                    horizontal = resources.getDimension(R.dimen.margin_large)
                        .toInt()
                )
            )
            this.adapter = storeYouFollowAdapter
        }

        viewModel.fetchStoresYouFollow().observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {

                }
                is Resource.Error -> {

                }
                is Resource.Data -> {
                    val storesList = it.data
                    storeYouFollowAdapter.submitList(storesList)
                }
            }
        }
    }

    private fun productListSetup(
        rv: RecyclerView,
        adapter: HomeProductsAdapter,
        observable: LiveData<Resource<List<Product>>>
    ) {
        with(rv) {
            addItemDecoration(
                SpaceItemDecoration(
                    horizontal = resources.getDimension(R.dimen.margin_large).toInt()
                )
            )
            this.adapter = adapter
        }

        observable.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {

                }
                is Resource.Error -> {

                }
                is Resource.Data -> {
                    val products = it.data
                    adapter.submitList(products)
                }
            }
        }
    }
}