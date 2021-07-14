package com.hexagram.febys.ui.screens.wishlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentWishListBinding
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.utils.goBack
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
                    ErrorDialog(it).show(childFragmentManager, ErrorDialog.TAG)
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