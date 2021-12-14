package com.hexagram.febys.ui.screens.product.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.hexagram.febys.NavGraphDirections
import com.hexagram.febys.base.BaseBottomSheet
import com.hexagram.febys.databinding.FragmentQAThreadsBinding
import com.hexagram.febys.models.api.product.QAThread
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.utils.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class QAThreadsFragment : BaseBottomSheet() {

    companion object {
        const val REQ_KEY_QA_THREAD_UPDATE = "reqKeyQAThreadUpdate"
    }

    private lateinit var binding: FragmentQAThreadsBinding
    private val args: QAThreadsFragmentArgs by navArgs()
    private val productDetailViewModel: ProductDetailViewModel by viewModels()

    private val replyQuestionBottomSheet get() = BottomSheetBehavior.from(binding.bottomSheetReplyQuestion.root)

    private val qaThreadsAdapter = QAThreadsAdapter()

    private val isUserLoggedIn
        get() = !args.userId.isNullOrEmpty()

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
        setupObserver()
    }

    private fun initUi() {
        closeBottomSheet(replyQuestionBottomSheet)

        binding.rvQAThreads.adapter = qaThreadsAdapter
        qaThreadsAdapter.consumerId = args.userId ?: ""

        updateQA(args.threads.toMutableList())
    }

    private fun uiListeners() {
        binding.ivBack.setOnClickListener { goBack() }

        binding.bottomSheetReplyQuestion.btnClose.setOnClickListener {
            closeBottomSheet(replyQuestionBottomSheet)
        }

        replyQuestionBottomSheet.onStateChange { state ->
            onBottomSheetStateChange(state)
        }

        qaThreadsAdapter.replyTo = {
            if (isUserLoggedIn) {
                binding.thread = it
                showBottomSheet(replyQuestionBottomSheet)
            } else {
                gotoLogin()
            }
        }

        qaThreadsAdapter.upVote = { thread, isRevoke ->
            if (isUserLoggedIn) {
                productDetailViewModel.questionsVoteUp(args.productId, thread._id, isRevoke)
            } else {
                gotoLogin()
            }
        }

        qaThreadsAdapter.downVote = { thread, isRevoke ->
            if (isUserLoggedIn) {
                productDetailViewModel.questionVoteDown(args.productId, thread._id, isRevoke)
            } else {
                gotoLogin()
            }
        }

        binding.bottomSheetReplyQuestion.btnPostAnswer.setOnClickListener {
            val answer = binding.bottomSheetReplyQuestion.etAnswer.text.toString().trim()
            val threadId = binding.thread?._id
            if (threadId == null || answer.isEmpty()) return@setOnClickListener
            productDetailViewModel.replyQuestion(args.productId, answer, threadId)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            closeBottomsSheetElseGoBack()
        }
    }

    private fun setupObserver() {
        productDetailViewModel.observeQAThreads.observe(viewLifecycleOwner) {
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
                    binding.bottomSheetReplyQuestion.etAnswer.setText("")
                    closeBottomSheet(replyQuestionBottomSheet)
                    updateQA(it.data)

                    val bundle = Bundle()
                    bundle.putParcelableArrayList(REQ_KEY_QA_THREAD_UPDATE, ArrayList(it.data))
                    setFragmentResult(REQ_KEY_QA_THREAD_UPDATE, bundle)
                }
            }
        }
    }

    private fun updateQA(qa: MutableList<QAThread>) {
        qaThreadsAdapter.submitList(qa)
    }

    private fun onBottomSheetStateChange(state: Int) {
        val isClosed = state == BottomSheetBehavior.STATE_HIDDEN
        if (isClosed) {
            binding.bgDim.fadeVisibility(false, 200)
        }
    }

    private fun showBottomSheet(bottomSheet: BottomSheetBehavior<View>) {
        binding.bgDim.fadeVisibility(true)
        bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun closeBottomSheet(bottomSheet: BottomSheetBehavior<View>) {
        binding.bgDim.fadeVisibility(false)
        bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun closeBottomsSheetElseGoBack() {
        listOf(
            replyQuestionBottomSheet
        ).forEach {
            if (it.state != BottomSheetBehavior.STATE_HIDDEN) {
                closeBottomSheet(it)
                return
            }
        }

        goBack()
    }

    private fun gotoLogin() {
        val gotoLogin = NavGraphDirections.actionToLoginFragment()
        navigateTo(gotoLogin)
    }

    override fun fullScreen() = true
}