package com.hexagram.febys.ui.screens.order.cancel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseBottomSheet
import com.hexagram.febys.databinding.BottomSheetListOfSelectionBinding
import com.hexagram.febys.ui.screens.list.selection.ListSelectionAdapter
import com.hexagram.febys.utils.goBack

class CancelOrderReasonsBottomSheet : BaseBottomSheet() {
    companion object {
        const val REQ_KEY_SELECTED_REASON = "reqKeySelectedReason"
    }

    private lateinit var binding: BottomSheetListOfSelectionBinding
    private val cancelReasonsAdapter = ListSelectionAdapter()
    private val args: CancelOrderReasonsBottomSheetArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetListOfSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        uiListener()
    }

    private fun initUi() {
        binding.title = getString(R.string.label_order_cancel_reason)
        binding.rvSelectionList.apply {
            setHasFixedSize(true)
            adapter = cancelReasonsAdapter
            addItemDecoration(
                DividerItemDecoration(context, (layoutManager as LinearLayoutManager).orientation)
            )
        }

        cancelReasonsAdapter.submitList(args.cancelReasons.toList())
        if (args.selectedReason.isNotEmpty()) cancelReasonsAdapter.updateSelectedItem(args.selectedReason)
    }

    private fun uiListener() {
        binding.btnClose.setOnClickListener { goBack() }

        cancelReasonsAdapter.interaction = { selectedItem ->
            setFragmentResult(
                REQ_KEY_SELECTED_REASON, bundleOf(REQ_KEY_SELECTED_REASON to selectedItem)
            )
            goBack()
        }
    }

    override fun fullScreen() = false
}