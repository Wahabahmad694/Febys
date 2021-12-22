package com.hexagram.febys.ui.screens.store.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentStoreListBinding
import com.hexagram.febys.models.api.menu.StoreMenus
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.ui.screens.search.SearchFragmentDirections
import com.hexagram.febys.utils.hideLoader
import com.hexagram.febys.utils.navigateTo
import com.hexagram.febys.utils.showLoader
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StoreListFragment : BaseFragment() {
    private lateinit var binding: FragmentStoreListBinding
    private val storeListViewModel by viewModels<StoreListViewModel>()

    private val storeListAdapter = StoreListAdapter()

    companion object {
        @JvmStatic
        fun newInstance() = StoreListFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = FragmentStoreListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iniUi()
        uiListeners()
        setObserver()
    }

    private fun iniUi() {
        binding.rvStoreList.adapter = storeListAdapter
    }

    private fun uiListeners() {
        storeListAdapter.itemClickCallback = {
            val title = it.name
            val storeId = it.template?.split(",")?.lastOrNull() ?: ""

            if (storeId.isNotEmpty()){
                val toWebViewFragment = SearchFragmentDirections
                    .actionStoreListFragmentToStoreLoadFragment(title, storeId)
                navigateTo(toWebViewFragment)
            }
        }
    }

    private fun setObserver() {
        storeListViewModel.observeStoreMenus.observe(viewLifecycleOwner) {
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
                    updateUi(it.data)
                }
            }
        }
    }

    private fun updateUi(storeList: List<StoreMenus>) {
        storeListAdapter.submitList(storeList)
    }
}