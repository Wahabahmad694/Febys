package com.hexagram.febys.ui.screens.order.refund

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentReturnOrderBinding
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.requests.RequestReturnOrder
import com.hexagram.febys.network.requests.SkuIdAndQuantity
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.ui.screens.list.selection.ListSelectionAdapter
import com.hexagram.febys.ui.screens.order.OrderViewModel
import com.hexagram.febys.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReturnOrderFragment : BaseFragment() {
    companion object {
        const val REQ_KEY_IS_ORDER_RETURN = "isOrderReturn"
    }

    private lateinit var binding: FragmentReturnOrderBinding
    private val orderViewModel: OrderViewModel by viewModels()
    private val args: ReturnOrderFragmentArgs by navArgs()
    private val returnOrderVendorProductAdapter = ReturnOrderVendorProductAdapter()
    private val returnReasonsAdapter = ListSelectionAdapter()
    private val returnReasonsBottomSheet get() = BottomSheetBehavior.from(binding.bottomSheetReturnReasons.root)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        orderViewModel.fetchReturnReasons()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentReturnOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        uiListener()
        setObservers()
    }

    private fun initUi() {
        closeBottomSheet(returnReasonsBottomSheet)

        binding.bottomSheetReturnReasons.rvSelectionList.apply {
            setHasFixedSize(true)
            adapter = returnReasonsAdapter
            addItemDecoration(
                DividerItemDecoration(context, (layoutManager as LinearLayoutManager).orientation)
            )
        }

        binding.rvVendorWithProducts.isNestedScrollingEnabled = false
        binding.rvVendorWithProducts.adapter = returnOrderVendorProductAdapter
        binding.rvVendorWithProducts.applySpaceItemDecoration(R.dimen._16sdp)
        returnOrderVendorProductAdapter.submitList(args.vendorProducts.toList())
    }

    private fun uiListener() {
        binding.ivBack.setOnClickListener { goBack() }

        binding.bottomSheetReturnReasons.btnClose.setOnClickListener {
            closeBottomSheet(returnReasonsBottomSheet)
        }
        binding.containerReason.setOnClickListener {
            showBottomSheet(returnReasonsBottomSheet)
        }
        returnReasonsBottomSheet.onStateChange { state ->
            onBottomSheetStateChange(state)
        }

        returnReasonsAdapter.interaction = { selectedItem ->
            updateReturnReason(selectedItem)
            closeBottomSheet(returnReasonsBottomSheet)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            closeBottomsSheetElseGoBack()
        }

        binding.btnSave.setOnClickListener {
            returnOrder()
        }
    }

    private fun setObservers() {
        orderViewModel.observeReturnReason.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {
                    showLoader()
                }
                is DataState.Error -> {
                    hideLoader()
                    ErrorDialog(it).show(childFragmentManager, ErrorDialog.TAG)
                }
                is DataState.Data -> {
                    hideLoader()
                    val reasons = it.data
                    returnReasonsAdapter.submitList(reasons)
                    updateReturnReason(reasons.firstOrNull())
                }
            }
        }

        orderViewModel.observeReturnOrder.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {
                    showLoader()
                }
                is DataState.Error -> {
                    hideLoader()
                    ErrorDialog(it).show(childFragmentManager, ErrorDialog.TAG)
                }
                is DataState.Data -> {
                    hideLoader()

                    setFragmentResult(
                        REQ_KEY_IS_ORDER_RETURN, bundleOf(REQ_KEY_IS_ORDER_RETURN to true)
                    )

                    goBack()
                }
            }
        }
    }

    private fun returnOrder() {
        val reason = binding.tvReason.text.toString()
        val comment = binding.etComment.text.toString()
        val returnItems = mutableListOf<SkuIdAndQuantity>()
        args.vendorProducts.forEach { vendorProducts ->
            vendorProducts.products.forEach {
                it.product.variants.firstOrNull()?.skuId?.let { skuId ->
                    returnItems.add(SkuIdAndQuantity(skuId, it.quantity))
                }
            }
        }

        val returnOrderRequest = RequestReturnOrder(returnItems, reason, comment)
        orderViewModel.returnOrder(args.orderId, returnOrderRequest)
    }

    private fun updateReturnReason(reason: String?) {
        binding.tvReason.text = reason
    }

    private fun showBottomSheet(bottomSheet: BottomSheetBehavior<View>) {
        binding.bgDim.fadeVisibility(true)
        bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun closeBottomSheet(bottomSheet: BottomSheetBehavior<View>) {
        binding.bgDim.fadeVisibility(false)
        bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun onBottomSheetStateChange(state: Int) {
        val isClosed = state == BottomSheetBehavior.STATE_HIDDEN
        if (isClosed) {
            binding.bgDim.fadeVisibility(false, 200)
        }
    }

    private fun closeBottomsSheetElseGoBack() {
        if (returnReasonsBottomSheet.state != BottomSheetBehavior.STATE_HIDDEN) {
            closeBottomSheet(returnReasonsBottomSheet)
            return
        }

        goBack()
    }
}