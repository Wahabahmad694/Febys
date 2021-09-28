package com.hexagram.febys.ui.screens.shipping.address

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentShippingAddressBinding
import com.hexagram.febys.models.view.ShippingAddress
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.utils.goBack
import com.hexagram.febys.utils.hideLoader
import com.hexagram.febys.utils.navigateTo
import com.hexagram.febys.utils.showLoader
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShippingAddressFragment : BaseFragment() {
    companion object {
        const val REQ_KEY_DEFAULT_SHIPPING_ADDRESS = "reqKeyDefaultShippingAddress"
    }

    private lateinit var binding: FragmentShippingAddressBinding
    private val shippingAddressViewModel: ShippingAddressViewModel by viewModels()
    private val shippingAddressAdapter = ShippingAddressAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentShippingAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        uiListener()
        setObservers()
    }

    private fun initUi() {
        binding.rvShippingAddress.adapter = shippingAddressAdapter
    }

    private fun uiListener() {
        binding.ivBack.setOnClickListener { goBack() }

        shippingAddressAdapter.setAsDefault = { id ->
            shippingAddressViewModel.setAsDefault(id)
        }

        shippingAddressAdapter.editShippingAddress = { shippingAddress ->
            gotoAddEditShippingAddress(shippingAddress)
        }

        binding.labelAddNewShippingAddress.setOnClickListener {
            gotoAddEditShippingAddress()
        }

        binding.btnSave.setOnClickListener {
            val defaultShippingAddress = shippingAddressAdapter.getDefaultShippingAddress()
            setFragmentResult(
                REQ_KEY_DEFAULT_SHIPPING_ADDRESS,
                bundleOf(REQ_KEY_DEFAULT_SHIPPING_ADDRESS to defaultShippingAddress)
            )
            goBack()
        }
    }

    private fun setObservers() {
        shippingAddressViewModel.shippingAddresses.observe(viewLifecycleOwner) {
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

        setFragmentResultListener(AddEditShippingAddressFragment.REQ_KEY_IS_ADD_OR_UPDATE) { _, bundle ->
            val isAddOrUpdate =
                bundle.getBoolean(AddEditShippingAddressFragment.REQ_KEY_IS_ADD_OR_UPDATE, false)

            if (isAddOrUpdate) {
                shippingAddressViewModel.refreshShippingAddresses()
            }
        }
    }

    private fun updateUi(addresses: List<ShippingAddress>) {
        if (addresses.isEmpty()) {
            return
        }

        shippingAddressAdapter.submitList(addresses)
    }


    private fun gotoAddEditShippingAddress(shippingAddress: ShippingAddress? = null) {
        val forceSetAsDefault = shippingAddressAdapter.itemCount == 0
        val gotoAddEditShippingAddress = ShippingAddressFragmentDirections
            .actionShippingAddressFragmentToAddEditShippingAddressFragment(
                shippingAddress, forceSetAsDefault
            )
        navigateTo(gotoAddEditShippingAddress)
    }
}