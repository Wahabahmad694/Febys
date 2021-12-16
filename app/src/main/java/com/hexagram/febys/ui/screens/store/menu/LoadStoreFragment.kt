package com.hexagram.febys.ui.screens.store.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentStoreLoadBinding
import com.hexagram.febys.models.api.menu.StoreTemplate
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.utils.goBack
import com.hexagram.febys.utils.navigateTo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoadStoreFragment : BaseFragment() {
    private val args by navArgs<LoadStoreFragmentArgs>()
    private val storeListViewModel by viewModels<StoreListViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = FragmentStoreLoadBinding.inflate(inflater, container, false).root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        storeListViewModel.fetchStoreTemplate(args.storeId)
        setObserver()
    }

    private fun setObserver() {
        storeListViewModel.observeStoreTemplate.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {
                    // do nothing
                }
                is DataState.Error -> {
                    ErrorDialog(
                        it, onOkayClick = { goBack() }, onCloseClick = { goBack() }
                    ).show(childFragmentManager, ErrorDialog.TAG)
                }
                is DataState.Data -> {
                    gotoWebView(it.data)
                }
            }
        }
    }

    private fun gotoWebView(template: StoreTemplate) {
        val actionStoreLoadFragmentToWebViewFragment = LoadStoreFragmentDirections
            .actionStoreLoadFragmentToWebViewFragment(args.title, template.content, true)
        navigateTo(actionStoreLoadFragmentToWebViewFragment)
    }

}