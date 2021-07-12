package com.hexagram.febys.ui.screens.product.listing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentProductListingBinding
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductListingFragment : BaseFragment() {
    private lateinit var binding: FragmentProductListingBinding
    private val productListingViewModel: ProductListingViewModel by viewModels()

    private val args: ProductListingFragmentArgs by navArgs()

    private val productListingAdapter = ProductListingAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductListingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        uiListeners()
        setupObserver()

        productListingViewModel.fetchWishList()
    }

    private fun initUi() {
        binding.productListingTitle = args.productListTitle

        binding.rvProductList.apply {
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL))
            layoutManager = GridLayoutManager(context, 2)
            adapter = this@ProductListingFragment.productListingAdapter
        }
    }

    private fun uiListeners() {
        binding.btnRefine.setOnClickListener {
            // todo show refine screen
        }

    }

    private fun setupObserver() {
        productListingViewModel.observeWishlist.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {

                }
                is DataState.Error -> {
                    ErrorDialog(it).show(childFragmentManager, ErrorDialog.TAG)
                }
                is DataState.Data -> {
                    val productList = it.data
                    binding.productListingCount = productList.size
                    productListingAdapter.submitList(productList)
                }
            }
        }
    }
}