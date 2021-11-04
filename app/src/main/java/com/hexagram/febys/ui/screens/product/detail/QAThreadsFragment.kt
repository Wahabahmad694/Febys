package com.hexagram.febys.ui.screens.product.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.hexagram.febys.base.BaseBottomSheet
import com.hexagram.febys.databinding.FragmentQAThreadsBinding
import com.hexagram.febys.utils.goBack
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class QAThreadsFragment : BaseBottomSheet() {
    private lateinit var binding: FragmentQAThreadsBinding
    private val args: QAThreadsFragmentArgs by navArgs()

    private val qaThreadsAdapter = QAThreadsAdapter()

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
        qaThreadsAdapter.submitList(args.threads.toMutableList())
    }

    private fun uiListeners() {
        binding.ivBack.setOnClickListener { goBack() }
    }

    override fun fullScreen() = true
}