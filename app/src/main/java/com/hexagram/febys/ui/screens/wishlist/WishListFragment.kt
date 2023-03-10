package com.hexagram.febys.ui.screens.wishlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.filter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.hexagram.febys.NavGraphDirections
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentWishListBinding
import com.hexagram.febys.models.api.product.Product
import com.hexagram.febys.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WishListFragment : BaseFragment() {
    private lateinit var binding: FragmentWishListBinding
    private val wishlistViewModel: WishlistViewModel by viewModels()

    private val wishlistPagerAdapter = WishlistPagerAdapter()

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
        binding.rvWishList.apply {
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL))
            layoutManager = GridLayoutManager(context, 2)
            adapter = this@WishListFragment.wishlistPagerAdapter
        }
    }

    private fun uiListeners() {
        binding.ivBack.setOnClickListener { goBack() }


        wishlistPagerAdapter.interaction = object : WishlistPagerAdapter.Interaction {
            override fun onItemSelected(position: Int, item: Product) {
                val gotoProductDetail =
                    NavGraphDirections.actionToProductDetail(item._id, item.variants[0].skuId)
                navigateTo(gotoProductDetail)
            }

            override fun removeFav(skuId: String) {
                wishlistViewModel.toggleFav(skuId)
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
                    showErrorToast(state)
                }
                binding.emptyView.root.isVisible =
                    it.refresh is LoadState.NotLoading && wishlistPagerAdapter.itemCount < 1
            }
        }
    }

    private fun updateWishList() {
        viewLifecycleOwner.lifecycleScope.launch {
            val fav = wishlistViewModel.getFav()
            binding.wishListCount = fav.size
            wishlistViewModel.fetchWishList().collectLatest { pagingData ->
                val list = pagingData.filter {
                    val skuId = it.variants[0].skuId
                    skuId in fav
                }
                wishlistPagerAdapter.submitData(list)
            }
        }
    }
}