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
import com.hexagram.febys.models.api.vendor.Vendor
import com.hexagram.febys.ui.screens.search.SearchFragmentDirections
import com.hexagram.febys.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VendorListingFragment : BaseFragment() {
    private var isCelebrity = false

    companion object {
        private const val KEY_IS_CELEBRITY = "keyIsCelebrity"

        @JvmStatic
        fun newInstance(isCelebrity: Boolean = false) = VendorListingFragment().apply {
            arguments = Bundle().apply {
                putBoolean(KEY_IS_CELEBRITY, isCelebrity)
            }
        }
    }

    private lateinit var binding: FragmentVendorListingBinding
    private val vendorViewModel: VendorViewModel by viewModels()
    private lateinit var vendorListingAdapter: VendorListingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCelebrity = arguments?.getBoolean(KEY_IS_CELEBRITY) ?: false
        vendorListingAdapter = VendorListingAdapter(isCelebrity)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentVendorListingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        uiListener()
        setObserver()
    }

    private fun initUi() {
        binding.rvVendors.applySpaceItemDecoration(verticalDimenRes = R.dimen._12sdp)
        binding.rvVendors.adapter = vendorListingAdapter
    }

    private fun uiListener() {
        vendorListingAdapter.followVendor =
            { vendorId -> vendorViewModel.followVendor(vendorId) }

        vendorListingAdapter.unFollowVendor =
            { vendorId -> vendorViewModel.unFollowVendor(vendorId) }

        vendorListingAdapter.gotoCelebrityDetail =
            { vendor -> gotoCelebrityDetail(vendor._id, vendor.isFollow) }

        vendorListingAdapter.gotoVendorDetail =
            { vendor -> gotoVendorDetail(vendor, vendor.isFollow) }
    }

    private fun setObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            vendorViewModel.fetchVendors(isCelebrity).collectLatest { pagingData ->
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

    private fun gotoCelebrityDetail(vendorId: String, isFollow: Boolean) {
        val direction = SearchFragmentDirections
            .actionSearchFragmentToCelebrityDetailFragment(vendorId, isFollow)
        navigateTo(direction)
    }

    private fun gotoVendorDetail(vendor: Vendor, isFollow: Boolean) {
        val direction = SearchFragmentDirections
            .actionSearchFragmentToProductListingByVendorFragment(
                vendor._id, vendor.name, vendor.role.name, isFollow
            )
        navigateTo(direction)
    }
}