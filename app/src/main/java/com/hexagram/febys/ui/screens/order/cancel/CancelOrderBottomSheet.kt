package com.hexagram.febys.ui.screens.order.cancel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.hexagram.febys.base.BaseBottomSheet
import com.hexagram.febys.databinding.BottomSheetCancelOrderBinding
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.ui.screens.order.OrderViewModel
import com.hexagram.febys.utils.goBack
import com.hexagram.febys.utils.hideLoader
import com.hexagram.febys.utils.navigateTo
import com.hexagram.febys.utils.showLoader
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CancelOrderBottomSheet : BaseBottomSheet() {
    private lateinit var binding: BottomSheetCancelOrderBinding
    private val orderViewModel: OrderViewModel by viewModels()

    private var cancelReasons = listOf<String>()
    private var selectedReason = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        orderViewModel.fetchCancelReasons()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetCancelOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uiListener()
        setupObserver()
    }

    private fun uiListener() {
        binding.btnClose.setOnClickListener { goBack() }

        binding.containerOrderCancelReason.setOnClickListener {
            gotoCancelReasons()
        }

        setFragmentResultListener(CancelOrderReasonsBottomSheet.REQ_KEY_SELECTED_REASON) { _, bundle ->
            updateSelectedReason(
                bundle.getString(CancelOrderReasonsBottomSheet.REQ_KEY_SELECTED_REASON) ?: ""
            )
        }
    }

    private fun setupObserver() {
        orderViewModel.observeCancelReasons.observe(viewLifecycleOwner) {
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
                    cancelReasons = it.data.reasons
                    updateSelectedReason(cancelReasons.firstOrNull() ?: "")
                }
            }
        }
    }

    private fun updateSelectedReason(cancelReason: String) {
        selectedReason = cancelReason
        binding.tvOrderCancelReason.text = selectedReason
    }


    private fun gotoCancelReasons() {
        val gotoCancelReasons =
            CancelOrderBottomSheetDirections.actionCancelOrderBottomSheetToCancelOrderReasonsBottomSheet(
                cancelReasons.toTypedArray(), selectedReason
            )
        navigateTo(gotoCancelReasons)
    }

    override fun fullScreen() = false
}