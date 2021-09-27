package com.hexagram.febys.ui.screens.shipping.address

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.hexagram.febys.R
import com.hexagram.febys.databinding.FragmentAddEditShippingAddressBinding
import com.hexagram.febys.models.view.ShippingAddress
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditShippingAddressFragment : Fragment() {
    private lateinit var binding: FragmentAddEditShippingAddressBinding
    private val shippingAddressViewModel: ShippingAddressViewModel by viewModels()
    private val args: AddEditShippingAddressFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddEditShippingAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateUi(args.shippingAddress)
    }

    private fun updateUi(shippingAddress: ShippingAddress?) {
        if (shippingAddress == null) {
            binding.labelShippingAddress.setText(R.string.label_new_shipping_address)

            binding.switchSetAsDefault.isChecked = args.forceSetAsDefault
            binding.switchSetAsDefault.isEnabled = !args.forceSetAsDefault
            return
        }

        binding.labelShippingAddress.setText(R.string.label_edit_shipping_address)

        binding.tvAddressLabel.text = shippingAddress.addressLabel
        binding.etFirstName.setText(shippingAddress.firstName)
        binding.etLastName.setText(shippingAddress.lastName)
        binding.tvRegion.text = shippingAddress.region
        binding.etAddressLine1.setText(shippingAddress.addressLine1)
        binding.etAddressLine2.setText(shippingAddress.addressLine2)
        binding.etCity.setText(shippingAddress.city)
        binding.etState.setText(shippingAddress.state)
        binding.etPostalCode.setText(shippingAddress.postalCode)
        binding.etPhone.setText(shippingAddress.phoneNo)
        binding.switchSetAsDefault.isChecked = shippingAddress.isDefault
        binding.switchSetAsDefault.isEnabled = !shippingAddress.isDefault
    }
}