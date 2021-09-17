package com.hexagram.febys.ui.screens.vendor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentVendorListingBinding
import com.hexagram.febys.utils.applySpaceItemDecoration
import com.hexagram.febys.utils.hideLoader
import com.hexagram.febys.utils.showLoader
import com.hexagram.febys.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VendorListingFragment : BaseFragment() {
    companion object {
        @JvmStatic
        fun newInstance() = VendorListingFragment()
    }

    private lateinit var binding: FragmentVendorListingBinding
    private val vendorViewModel: VendorViewModel by viewModels()
    private val vendorListingAdapter = VendorListingAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentVendorListingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        setObserver()
    }

    private fun initUi() {
        binding.rvVendors.applySpaceItemDecoration(verticalDimenRes = R.dimen._12sdp)
        binding.rvVendors.adapter = vendorListingAdapter
    }

    private fun setObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            vendorViewModel.allCategoryPagingData.collectLatest { pagingData ->
                vendorListingAdapter.submitData(pagingData)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            vendorListingAdapter.loadStateFlow.collectLatest {
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
}