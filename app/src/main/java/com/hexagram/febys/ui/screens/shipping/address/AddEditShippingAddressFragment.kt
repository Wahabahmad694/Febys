package com.hexagram.febys.ui.screens.shipping.address

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentAddEditShippingAddressBinding
import com.hexagram.febys.models.api.contact.PhoneNo
import com.hexagram.febys.models.api.location.LatLong
import com.hexagram.febys.models.api.location.LocationSuggestion
import com.hexagram.febys.models.api.shippingAddress.Address
import com.hexagram.febys.models.api.shippingAddress.Coordinates
import com.hexagram.febys.models.api.shippingAddress.ShippingAddress
import com.hexagram.febys.models.api.shippingAddress.ShippingDetail
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.ui.screens.location.LocationFragment
import com.hexagram.febys.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditShippingAddressFragment : BaseFragment() {
    companion object {
        const val REQ_KEY_IS_ADD_OR_UPDATE = "isAddOrUpdate"
    }

    private lateinit var binding: FragmentAddEditShippingAddressBinding
    private val shippingAddressViewModel: ShippingAddressViewModel by viewModels()
    private val args: AddEditShippingAddressFragmentArgs by navArgs()

    private var firstName: String = ""
    private var lastName: String = ""
    private var addressLabel: String = ""
    private var country: String = ""
    private var addressLine1: String = ""
    private var city: String = ""
    private var state: String = ""
    private var zipCode: String = ""
    private var phoneNo: String = ""
    private var isDefault: Boolean = false
    private var location: LocationSuggestion? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddEditShippingAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        uiListeners()
        setObserver()
        observeLocation()
    }

    private fun initUi() {
        updateUi(args.shippingAddress)
    }

    private fun updateUi(shippingAddress: ShippingAddress?) {
        if (shippingAddress == null) {
            binding.labelShippingAddress.setText(R.string.label_new_shipping_address)

            isDefault = args.forceSetAsDefault
            binding.switchSetAsDefault.isChecked = args.forceSetAsDefault
            binding.switchSetAsDefault.isEnabled = !args.forceSetAsDefault

            updateDefaultCCP(PhoneNo(Utils.DEFAULT_COUNTRY_CODE, ""))

            return
        }

        binding.labelShippingAddress.setText(R.string.label_edit_shipping_address)

        binding.etAddressLabel.setText(shippingAddress.shippingDetail.label)
        binding.etFirstName.setText(shippingAddress.shippingDetail.firstName)
        binding.etLastName.setText(shippingAddress.shippingDetail.lastName)

        binding.tvRegion.text = shippingAddress.shippingDetail.address.countryCode
        binding.tvState.text = shippingAddress.shippingDetail.address.state.toString()
        binding.tvCity.text = shippingAddress.shippingDetail.address.city.toString()

        binding.etAddressLine1.text = shippingAddress.shippingDetail.address.street
        binding.etPostalCode.setText(shippingAddress.shippingDetail.address.zipCode)

        updateDefaultCCP(shippingAddress.shippingDetail.contact)

        isDefault = shippingAddress.shippingDetail.isDefault
        binding.switchSetAsDefault.isChecked = shippingAddress.shippingDetail.isDefault
        binding.switchSetAsDefault.isEnabled = !shippingAddress.shippingDetail.isDefault
    }

    private fun updateDefaultCCP(contact: PhoneNo) {
        binding.ccpPhoneCode.setDefaultCountryUsingNameCode(contact.countryCode)
        binding.ccpPhoneCode.resetToDefaultCountry()
        val countryCodeWithPlus = binding.ccpPhoneCode.selectedCountryCodeWithPlus
        binding.etPhone.setText(contact.number.replace(countryCodeWithPlus, ""))
    }

    private fun uiListeners() {
        binding.containerAddress.setOnClickListener {
            val gotoMap =
                AddEditShippingAddressFragmentDirections.actionAddEditShippingAddressFragmentToLocationFragment(
                    LatLong(0.0, 0.0)
                )
            navigateTo(gotoMap)
        }
        binding.btnSave.setOnClickListener {
            initFields()
            if (areAllFieldsValid()) {
                saveOrUpdateShippingAddress()
            }
        }

        binding.ivBack.setOnClickListener {
            goBack()
        }
    }

    private fun setObserver() {
        shippingAddressViewModel.addUpdateShippingAddress.observe(viewLifecycleOwner) {
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
                    setFragmentResult(
                        REQ_KEY_IS_ADD_OR_UPDATE,
                        bundleOf(REQ_KEY_IS_ADD_OR_UPDATE to true)
                    )
                    goBack()
                }
            }
        }

    }


    private fun observeLocation() {
        setFragmentResultListener(LocationFragment.LOCATION) { _, bundle ->
            location =
                bundle.getParcelable<LocationSuggestion?>(LocationFragment.LOCATION)
            binding.etAddressLine1.text = location?.name
            binding.tvCity.text = location?.address?.city
            binding.tvRegion.text = location?.address?.country
            binding.tvState.text = location?.address?.state
            binding.etPostalCode.setText(location?.address?.postalCode)

            if (!binding.etAddressLine1.text.isNullOrEmpty()) {
                binding.containerRegion.isVisible = true
                if (!location?.address?.state.isNullOrEmpty()) {
                    binding.containerState.isVisible = true
                }
                if (!location?.address?.city.isNullOrEmpty()) {
                    binding.containerCity.isVisible = true
                }
                if (!location?.address?.postalCode.isNullOrEmpty()) {
                    binding.containerPostalCode.isVisible = true
                }
            }
        }
    }


    private fun saveOrUpdateShippingAddress() {
        val shippingAddress = createShippingAddress(args.shippingAddress?.id)
        shippingAddressViewModel.addEditShippingAddress(shippingAddress)
    }


    private fun createShippingAddress(id: String?): ShippingAddress {
        val phoneCountryCode = binding.ccpPhoneCode.selectedCountryNameCode
        val phoneNo = PhoneNo(
            countryCode = phoneCountryCode,
            number = phoneNo
        )
        val coordinates: MutableList<Double?> = mutableListOf(location?.lng, location?.lat)
        val coordinate = Coordinates(coordinates)
        val address = Address(
            city = city,
            countryCode = country,
            state = state,
            street = addressLine1,
            zipCode = zipCode,
            location = coordinate
        )
        val shippingDetail = ShippingDetail(
            shippingDetailId = id,
            address = address,
            contact = phoneNo,
            createdAt = "",
            isDefault = isDefault,
            firstName = firstName,
            label = addressLabel,
            lastName = lastName,
            updatedAt = ""

        )

        return ShippingAddress(
            id = id,
            consumerId = consumer?.id ?: -1,
            creationTime = "",
            shippingDetail = shippingDetail,
            updatedTime = ""
        )
    }


    private fun initFields() {
        firstName = binding.etFirstName.text.toString()
        lastName = binding.etLastName.text.toString()
        addressLabel = binding.etAddressLabel.text.toString()
        country = binding.tvRegion.text.toString()
        state = binding.tvState.text.toString()
        city = binding.tvCity.text.toString()
        addressLine1 = binding.etAddressLine1.text.toString()
        zipCode = binding.etPostalCode.text.toString()
        phoneNo =
            binding.ccpPhoneCode.selectedCountryCodeWithPlus.toString() + binding.etPhone.text.toString()
        isDefault = binding.switchSetAsDefault.isChecked
    }

    private fun areAllFieldsValid(): Boolean {
        if (!Validator.isValidName(addressLabel)) {
            showErrorDialog(getString(R.string.error_enter_address_label))
            return false
        }
        if (!Validator.isValidName(firstName)) {
            showErrorDialog(getString(R.string.error_enter_first_name))
            return false
        }

        if (!Validator.isValidName(lastName)) {
            showErrorDialog(getString(R.string.error_enter_last_name))
            return false
        }

        if (!Validator.isValidAddress(addressLine1)) {
            showErrorDialog(getString(R.string.error_enter_valid_address))
            return false
        }

//        if (!Validator.isValidCity(city)) {
//            showErrorDialog(getString(R.string.error_enter_valid_city))
//            return false
//        }
//
//        if (state.isEmpty() && !Validator.isValidState(state)) {
//            showErrorDialog(getString(R.string.error_enter_valid_state))
//            return false
//        }
//
//        if (country.isEmpty() && !Validator.isValidCountry(country)) {
//            showErrorDialog(getString(R.string.error_enter_valid_country))
//            return false
//        }
//
//        if (!Validator.isValidPostalCode(zipCode)) {
//            showErrorDialog(getString(R.string.error_enter_valid_postal_code))
//            return false
//        }

        if (!Validator.isValidPhone(phoneNo)) {
            showErrorDialog(getString(R.string.error_enter_valid_phone))
            return false
        }

        return true
    }
}