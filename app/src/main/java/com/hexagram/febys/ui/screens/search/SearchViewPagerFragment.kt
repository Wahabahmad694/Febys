package com.hexagram.febys.ui.screens.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.hexagram.febys.R
import com.hexagram.febys.databinding.FragmentSearchViewPagerBinding
import com.hexagram.febys.network.response.Category
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchViewPagerFragment : com.hexagram.febys.base.BaseFragment() {
    private lateinit var binding: FragmentSearchViewPagerBinding
    private val viewModel: SearchViewModel by viewModels()

    private val adapter = SearchViewPagerAdapter()

    private var tabName: String = ""
    private var tabPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tabName = requireArguments()[ARGS_TAB_NAME] as String
        tabPosition = requireArguments()[ARGS_TAB_POSITION] as Int
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchViewPagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvSearchViewPager.adapter = adapter
        binding.rvSearchViewPager.setHasFixedSize(true)
        binding.rvSearchViewPager.addItemDecoration(
            DividerItemDecoration(
                binding.rvSearchViewPager.context,
                (binding.rvSearchViewPager.layoutManager as LinearLayoutManager).orientation
            )
        )

        when (tabPosition) {
            0 -> {
                setupAllCategories()
            }
        }
    }

    private fun setupAllCategories() {
        adapter.interaction = object : SearchViewPagerAdapter.Interaction {
            override fun onItemSelected(position: Int, item: Category) {
                // todo navigate to sub category page
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.fetchAllCategories().collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest {
                val state = it.refresh
                binding.progressBar.isVisible = state is LoadState.Loading

                if (state is LoadState.Error) {
                    showToast(getString(R.string.error_something_went_wrong))
                }
            }
        }
    }

    companion object {
        const val ARGS_TAB_NAME = "argsTabName"
        const val ARGS_TAB_POSITION = "argsTabPosition"

        @JvmStatic
        fun newInstance(tabName: String, tabPosition: Int) = SearchViewPagerFragment().apply {
            arguments = Bundle().apply {
                putString(ARGS_TAB_NAME, tabName)
                putInt(ARGS_TAB_POSITION, tabPosition)
            }
        }
    }
}