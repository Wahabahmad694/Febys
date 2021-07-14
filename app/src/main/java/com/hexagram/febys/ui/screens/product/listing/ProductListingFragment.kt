package com.hexagram.febys.ui.screens.product.listing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentProductListingBinding
import com.hexagram.febys.network.response.Product
import com.hexagram.febys.utils.goBack
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class ProductListingFragment : BaseFragment() {
    protected lateinit var binding: FragmentProductListingBinding
    protected val productListingViewModel: ProductListingViewModel by viewModels()

    protected val productListingAdapter = ProductListingAdapter()

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
    }

    private fun initUi() {
        binding.productListingTitle = getListingTitle()

        binding.rvProductList.apply {
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL))
            layoutManager = GridLayoutManager(context, 2)
            adapter = this@ProductListingFragment.productListingAdapter
        }

        productListingAdapter.interaction = object : ProductListingAdapter.Interaction {
            override fun onItemSelected(position: Int, item: Product) {
                onProductClick(position, item)
            }
        }
    }

    private fun uiListeners() {
        binding.ivBack.setOnClickListener {
            goBack()
        }

        binding.btnRefine.setOnClickListener {
            // todo show refine screen
        }

    }

    abstract fun getListingTitle(): String

    abstract fun onProductClick(position: Int, item: Product)
}