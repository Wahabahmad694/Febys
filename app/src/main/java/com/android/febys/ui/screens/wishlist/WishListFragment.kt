package com.android.febys.ui.screens.wishlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.android.febys.base.BaseFragment
import com.android.febys.databinding.FragmentWishListBinding
import com.android.febys.network.DataState
import com.android.febys.utils.getErrorMessage
import com.android.febys.utils.goBack
import com.android.febys.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WishListFragment : BaseFragment() {
    private lateinit var binding: FragmentWishListBinding
    private val viewModel: WishlistViewModel by viewModels()

    private val adapter = WishlistAdapter()

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
        setupObserver()

        if (isUserLoggedIn)
            viewModel.fetchWishList()
        else {
            binding.wishListCount = 0
        }
    }

    private fun initUi() {
        binding.rvWishList.apply {
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL))
            layoutManager = GridLayoutManager(context, 2)
            adapter = this@WishListFragment.adapter
        }
    }

    private fun uiListeners() {
        binding.btnGetInspired.setOnClickListener { goBack() }
    }

    private fun setupObserver() {
        viewModel.observeWishlist.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {

                }
                is DataState.Error -> {
                    val error = getErrorMessage(it)
                    showToast(error)
                }
                is DataState.Data -> {
                    val wishList = it.data
                    binding.wishListCount = wishList.size
                    adapter.submitList(wishList)
                }
            }
        }
    }
}