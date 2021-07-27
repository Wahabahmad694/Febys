package com.hexagram.febys.ui.screens.wishlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.filter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentWishListBinding
import com.hexagram.febys.network.response.Product
import com.hexagram.febys.utils.goBack
import com.hexagram.febys.utils.hideLoader
import com.hexagram.febys.utils.showLoader
import com.hexagram.febys.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WishListFragment : BaseFragment() {
    private lateinit var binding: FragmentWishListBinding
    private val wishlistViewModel: WishlistViewModel by viewModels()

    private val wishlistPagerAdapter = WishlistPagerAdapter()
    private var removeCount = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentWishListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        uiListeners()

        if (isUserLoggedIn)
            setupWishlistPagerAdapter()
    }

    private fun initUi() {
        binding.wishListCount = 0

        binding.rvWishList.apply {
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL))
            layoutManager = GridLayoutManager(context, 2)
            adapter = this@WishListFragment.wishlistPagerAdapter
        }
    }

    private fun uiListeners() {
        binding.btnGetInspired.setOnClickListener { goBack() }

        wishlistPagerAdapter.interaction = object : WishlistPagerAdapter.Interaction {
            override fun onItemSelected(position: Int, item: Product) {
                // todo open product detail
            }

            override fun removeFav(variantId: Int) {
                wishlistViewModel.toggleFav(variantId)
                removeCount++
                updateWishList()
            }
        }
    }

    private fun setupWishlistPagerAdapter() {
        binding.rvWishList.adapter = wishlistPagerAdapter

        updateWishList()

        viewLifecycleOwner.lifecycleScope.launch {
            wishlistPagerAdapter.loadStateFlow.collectLatest {
                val state = it.refresh
                if (state is LoadState.Loading) {
                    showLoader()
                } else {
                    hideLoader()
                }

                if (state is LoadState.Error) {
                    showToast(getString(R.string.error_something_went_wrong))
                }
            }
        }
    }

    private fun updateWishList() {
        viewLifecycleOwner.lifecycleScope.launch {
            val fav = wishlistViewModel.getFav()
            wishlistViewModel.fetchWishList {
                binding.wishListCount = it.totalRows - removeCount
            }.collectLatest { pagingData ->

                val list = pagingData.filter {
                    val variantId = it.product_variants[0].id
                    variantId in fav
                }

                wishlistPagerAdapter.submitData(list)
            }
        }
    }
}