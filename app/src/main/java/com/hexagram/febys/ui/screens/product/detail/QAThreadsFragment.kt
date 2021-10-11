package com.hexagram.febys.ui.screens.product.detail

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hexagram.febys.databinding.FragmentQAThreadsBinding
import com.hexagram.febys.network.FakeApiService
import com.hexagram.febys.utils.goBack
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class QAThreadsFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentQAThreadsBinding

    private val qaThreadsAdapter = QAThreadsAdapter()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener {

            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { pl ->
                val behaviour = BottomSheetBehavior.from(pl)
                setupFullHeight(pl)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return dialog
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentQAThreadsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        uiListeners()
    }

    private fun initUi() {
        binding.rvQAThreads.adapter = qaThreadsAdapter
        qaThreadsAdapter.submitList(FakeApiService.fetchQuestionAnswersThread())
    }

    private fun uiListeners() {
        binding.ivBack.setOnClickListener { goBack() }
    }
}