package com.hexagram.febys.ui.screens.shipping.address

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentAddEditShippingAddressBinding
import com.hexagram.febys.models.api.cities.City
import com.hexagram.febys.models.api.contact.PhoneNo
import com.hexagram.febys.models.api.countries.Country
import com.hexagram.febys.models.api.location.LatLong
import com.hexagram.febys.models.api.location.LocationSuggestion
import com.hexagram.febys.models.api.shippingAddress.Address
import com.hexagram.febys.models.api.shippingAddress.ShippingAddress
import com.hexagram.febys.models.api.shippingAddress.ShippingDetail
import com.hexagram.febys.models.api.states.State
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.ui.screens.list.selection.ListSelectionAdapter
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

//    private val regionBottomSheet get() = BottomSheetBehavior.from(binding.bottomSheetRegion.root)
//    private val stateBottomSheet get() = BottomSheetBehavior.from(binding.bottomSheetState.root)
//    private val cityBottomSheet get() = BottomSheetBehavior.from(binding.bottomSheetCity.root)

    private val regionsAdapter = ListSelectionAdapter()
    private val statesAdapter = ListSelectionAdapter()
    private val citiesAdapter = ListSelectionAdapter()


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

    private var countries = listOf<Country>()
    private var states = listOf<State>()
    private var cities = listOf<City>()

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
//        closeBottomSheet(regionBottomSheet)
//        closeBottomSheet(stateBottomSheet)
//        closeBottomSheet(cityBottomSheet)
//
//        binding.bottomSheetRegion.rvSelectionList.apply {
//            setHasFixedSize(true)
//            adapter = regionsAdapter
//            addItemDecoration(
//                DividerItemDecoration(context, (layoutManager as LinearLayoutManager).orientation)
//            )
//        }
//
//        binding.bottomSheetRegion.btnClose.setOnClickListener {
//            closeBottomSheet(regionBottomSheet)
//        }
//
//        binding.bottomSheetState.rvSelectionList.apply {
//            setHasFixedSize(true)
//            adapter = statesAdapter
//            addItemDecoration(
//                DividerItemDecoration(context, (layoutManager as LinearLayoutManager).orientation)
//            )
//        }
//
//        binding.bottomSheetState.btnClose.setOnClickListener {
//            closeBottomSheet(stateBottomSheet)
//        }
//
//        binding.bottomSheetCity.rvSelectionList.apply {
//            setHasFixedSize(true)
//            adapter = citiesAdapter
//            addItemDecoration(
//                DividerItemDecoration(context, (layoutManager as LinearLayoutManager).orientation)
//            )
//        }
//
//        binding.bottomSheetCity.btnClose.setOnClickListener {
//            closeBottomSheet(cityBottomSheet)
//        }

        updateUi(args.shippingAddress)
    }

    private fun updateUi(shippingAddress: ShippingAddress?) {
        if (shippingAddress == null) {
            binding.labelShippingAddress.setText(R.string.label_new_shipping_address)

            isDefault = args.forceSetAsDefault
            binding.switchSetAsDefault.isChecked = args.forceSetAsDefault
            binding.switchSetAsDefault.isEnabled = !args.forceSetAsDefault

            binding.tvRegion.text = regionsAdapter.getSelectedItem()
            binding.tvState.text = statesAdapter.getSelectedItem()
            binding.tvCity.text = citiesAdapter.getSelectedItem()

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

//        binding.containerRegion.setOnClickListener {
//            showBottomSheet(regionBottomSheet)
//        }
//        binding.containerState.setOnClickListener {
//            showBottomSheet(stateBottomSheet)
//        }
//        binding.containerCity.setOnClickListener {
//            showBottomSheet(cityBottomSheet)
//        }
//        regionsAdapter.interaction = { selectedItem ->
//            countryCodeISO = countries.firstOrNull { it.name == selectedItem }?.isoCode ?: ""
//            updateSelectedCountry()
//            updateDefaultCCP(PhoneNo(countryCodeISO, ""))
//            closeBottomSheet(regionBottomSheet)
//        }
//        statesAdapter.interaction = { selectedItem ->
//            stateISO = states.firstOrNull { it.name == selectedItem }?.isoCode ?: ""
//            updateSelectedState()
//            closeBottomSheet(stateBottomSheet)
//        }
//        citiesAdapter.interaction = { selectedItem ->
//            city = cities.firstOrNull { it.name == selectedItem }?.name ?: ""
//            updateSelectedCity()
//            closeBottomSheet(cityBottomSheet)
//        }

//        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
//            closeBottomsSheetElseGoBack()
//        }

//        regionBottomSheet.onStateChange { state ->
//            onBottomSheetStateChange(state)
//        }
//        stateBottomSheet.onStateChange { state ->
//            onBottomSheetStateChange(state)
//        }
//        cityBottomSheet.onStateChange { state ->
//            onBottomSheetStateChange(state)
//        }
//
//        binding.bgDim.setOnClickListener {
//            // do nothing, just add to avoid click on views that are behind of bg dim when bg dim is visible
//        }
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

//        shippingAddressViewModel.countryResponse.observe(viewLifecycleOwner) {
//            when (it) {
//                is DataState.Loading -> {
//                    showLoader()
//                }
//                is DataState.Error -> {
//                    hideLoader()
//                    ErrorDialog(it).show(childFragmentManager, ErrorDialog.TAG)
//                }
//                is DataState.Data -> {
//                    hideLoader()
//                    countries = it.data.countries
//                    val countriesName = countries.map { country -> country.name }
//                    regionsAdapter.submitList(countriesName)
//                    updateSelectedCountry()
//                }
//            }
//        }
//        shippingAddressViewModel.statesResponse.observe(viewLifecycleOwner) {
//            when (it) {
//                is DataState.Loading -> {
//                    showLoader()
//                }
//                is DataState.Error -> {
//                    hideLoader()
//                    ErrorDialog(it).show(childFragmentManager, ErrorDialog.TAG)
//                }
//                is DataState.Data -> {
//                    hideLoader()
//                    states = it.data.states
//                    Log.d("States", "${it.data.states}")
//                    val statesName = it.data.states.map { state -> state.name }
//                    statesAdapter.submitList(statesName)
////                    updateSelectedState()
//                }
//            }
//        }
////        shippingAddressViewModel.citiesResponse.observe(viewLifecycleOwner) {
//            when (it) {
//                is DataState.Loading -> {
//                    showLoader()
//                }
//                is DataState.Error -> {
//                    hideLoader()
//                    ErrorDialog(it).show(childFragmentManager, ErrorDialog.TAG)
//                }
//                is DataState.Data -> {
//                    hideLoader()
//                    cities = it.data.cities
//                    val citiesName = it.data.cities.map { city -> city.name }
//                    citiesAdapter.submitList(citiesName)
//                    updateSelectedCity()
//                }
//            }
//        }
    }

    //    private fun updateSelectedCountry() {
//        val selectedCountry =
//            countries.firstOrNull { country -> country.isoCode == countryCodeISO }
//
//        if (selectedCountry != null) {
//            val index = countries.indexOf(selectedCountry)
//            binding.bottomSheetRegion.rvSelectionList.scrollToPosition(index)
//
//            shippingAddressViewModel.fetchStates(GetStatesRequest(countryCodeISO))
//        }
//
//        val countryName = selectedCountry?.name ?: ""
//        regionsAdapter.updateSelectedItem(countryName)
//        binding.tvRegion.text = countryName
//
//        binding.tvState.text = ""
//        binding.tvCity.text = ""
//
//        binding.containerState.isEnabled = selectedCountry != null
//        binding.containerCity.isEnabled = selectedCountry != null
//    }
//
//
//    private fun updateSelectedState() {
//        val selectedState =
//            states.firstOrNull { state -> state.isoCode == stateISO }
//
//        if (selectedState != null) {
//            val index = states.indexOf(selectedState)
//            binding.bottomSheetState.rvSelectionList.scrollToPosition(index)
//
//            shippingAddressViewModel.fetchCities(GetCitiesRequest(countryCodeISO, stateISO))
//        }
//
//        val stateName = selectedState?.name ?: ""
//        statesAdapter.updateSelectedItem(stateName)
//        binding.tvState.text = stateName
//
//        binding.tvCity.text = ""
//
//        binding.containerCity.isEnabled = selectedState != null
//    }
//
//    private fun updateSelectedCity() {
//        val selectedCity =
//            cities.firstOrNull { city -> city.name == this.city }
//
//        if (selectedCity != null) {
//            val index = cities.indexOf(selectedCity)
//            binding.bottomSheetCity.rvSelectionList.scrollToPosition(index)
//        }
//
//        val cityName = selectedCity?.name ?: ""
//        citiesAdapter.updateSelectedItem(cityName)
//        binding.tvCity.text = cityName
//    }

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
        val address = Address(
            city = city,
            countryCode = country,
            state = state,
            street = addressLine1,
            zipCode = zipCode,
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


        if (!Validator.isValidCity(city)) {
            showErrorDialog(getString(R.string.error_enter_valid_city))
            return false
        }

        if (state.isEmpty() && !Validator.isValidState(state)) {
            showErrorDialog(getString(R.string.error_enter_valid_state))
            return false
        }

        if (country.isEmpty() && !Validator.isValidCountry(country)) {
            showErrorDialog(getString(R.string.error_enter_valid_country))
            return false
        }

        if (!Validator.isValidPostalCode(zipCode)) {
            showErrorDialog(getString(R.string.error_enter_valid_postal_code))
            return false
        }

        if (!Validator.isValidPhone(phoneNo)) {
            showErrorDialog(getString(R.string.error_enter_valid_phone))
            return false
        }

        return true
    }

//    private fun showBottomSheet(bottomSheet: BottomSheetBehavior<View>) {
//        binding.bgDim.fadeVisibility(true)
//        bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
//    }
//
//    private fun closeBottomSheet(bottomSheet: BottomSheetBehavior<View>) {
//        binding.bgDim.fadeVisibility(false)
//        bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
//    }
//
//    private fun onBottomSheetStateChange(state: Int) {
//        val isClosed = state == BottomSheetBehavior.STATE_HIDDEN
//        if (isClosed) {
//            binding.bgDim.fadeVisibility(false, 200)
//        }
//    }
//
//    private fun closeBottomsSheetElseGoBack() {
//        listOf(
//            regionBottomSheet,
//            stateBottomSheet,
//            cityBottomSheet
//        ).forEach {
//            if (it.state != BottomSheetBehavior.STATE_HIDDEN) {
//                closeBottomSheet(it)
//                return
//            }
//        }
//
//        goBack()
//    }

    private fun observeLocation() {
        setFragmentResultListener(LocationFragment.LOCATION) { _, bundle ->
            location =
                bundle.getParcelable<LocationSuggestion?>(LocationFragment.LOCATION)
            binding.etAddressLine1.text = location?.address?.fullAddress()
            binding.tvCity.text = location?.address?.city
            binding.tvRegion.text = location?.address?.country
            binding.tvState.text = location?.address?.state
            binding.etPostalCode.setText(location?.address?.postalCode)

        }
    }

}