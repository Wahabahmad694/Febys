package com.hexagram.febys.ui.screens.shipping.address

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.hexagram.febys.R
import com.hexagram.febys.databinding.FragmentAddEditShippingAddressBinding
import com.hexagram.febys.models.api.contact.PhoneNo
import com.hexagram.febys.models.api.request.GetCitiesRequest
import com.hexagram.febys.models.api.request.GetStatesRequest
import com.hexagram.febys.models.api.shippingAddress.*
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.ui.screens.list.selection.ListSelectionAdapter
import com.hexagram.febys.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditShippingAddressFragment : Fragment() {
    companion object {
        const val REQ_KEY_IS_ADD_OR_UPDATE = "isAddOrUpdate"
    }

    private lateinit var binding: FragmentAddEditShippingAddressBinding
    private val shippingAddressViewModel: ShippingAddressViewModel by viewModels()
    private val args: AddEditShippingAddressFragmentArgs by navArgs()

    private val addressLabelsBottomSheet get() = BottomSheetBehavior.from(binding.bottomSheetAddressLabels.root)
    private val regionBottomSheet get() = BottomSheetBehavior.from(binding.bottomSheetRegion.root)
    private val stateBottomSheet get() = BottomSheetBehavior.from(binding.bottomSheetState.root)
    private val cityBottomSheet get() = BottomSheetBehavior.from(binding.bottomSheetCity.root)

    private val addressLabelsAdapter = ListSelectionAdapter()
    private val regionsAdapter = ListSelectionAdapter()
    private val statesAdapter = ListSelectionAdapter()
    private val citiesAdapter = ListSelectionAdapter()


    private var firstName: String = ""
    private var lastName: String = ""
    private var addressLabel: String = ""
    private var countryCode: String = ""
    private var addressLine1: String = ""
    private var addressLine2: String = ""
    private var city: String = ""
    private var state: String = ""
    private var zipCode: String = ""
    private var phoneNo: String = ""
    private var street: String = ""
    private var isDefault: Boolean = false

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
    }

    private fun initUi() {
        closeBottomSheet(addressLabelsBottomSheet)
        closeBottomSheet(regionBottomSheet)
        closeBottomSheet(stateBottomSheet)
        closeBottomSheet(cityBottomSheet)

        binding.bottomSheetAddressLabels.rvSelectionList.apply {
            setHasFixedSize(true)
            adapter = addressLabelsAdapter
            addItemDecoration(
                DividerItemDecoration(context, (layoutManager as LinearLayoutManager).orientation)
            )
        }
        binding.bottomSheetAddressLabels.btnClose.setOnClickListener{
            closeBottomSheet(addressLabelsBottomSheet)
        }

        binding.bottomSheetRegion.rvSelectionList.apply {
            setHasFixedSize(true)
            adapter = regionsAdapter
            addItemDecoration(
                DividerItemDecoration(context, (layoutManager as LinearLayoutManager).orientation)
            )
        }
        binding.bottomSheetRegion.btnClose.setOnClickListener{
            closeBottomSheet(regionBottomSheet)
        }
        binding.bottomSheetState.rvSelectionList.apply {
            setHasFixedSize(true)
            adapter = statesAdapter
            addItemDecoration(
                DividerItemDecoration(context, (layoutManager as LinearLayoutManager).orientation)
            )
        }
        binding.bottomSheetState.btnClose.setOnClickListener{
            closeBottomSheet(stateBottomSheet)
        }
        binding.bottomSheetCity.rvSelectionList.apply {
            setHasFixedSize(true)
            adapter = citiesAdapter
            addItemDecoration(
                DividerItemDecoration(context, (layoutManager as LinearLayoutManager).orientation)
            )
        }
        binding.bottomSheetCity.btnClose.setOnClickListener{
            closeBottomSheet(cityBottomSheet)
        }

        val addressLabels = resources.getStringArray(R.array.address_labels).toMutableList()
        addressLabelsAdapter.submitList(addressLabels)


        updateUi(args.shippingAddress)
    }

    private fun updateUi(shippingAddress: ShippingAddress?) {
        if (shippingAddress == null) {
            binding.labelShippingAddress.setText(R.string.label_new_shipping_address)

            isDefault = args.forceSetAsDefault
            binding.switchSetAsDefault.isChecked = args.forceSetAsDefault
            binding.switchSetAsDefault.isEnabled = !args.forceSetAsDefault

            binding.tvAddressLabel.text = addressLabelsAdapter.getSelectedItem()
            binding.tvRegion.text = regionsAdapter.getSelectedItem()
            binding.tvState.text = statesAdapter.getSelectedItem()
            binding.tvCity.text = citiesAdapter.getSelectedItem()

            return
        }

        binding.labelShippingAddress.setText(R.string.label_edit_shipping_address)

        addressLabelsAdapter.updateSelectedItem(shippingAddress.shippingDetail.label)
        binding.tvAddressLabel.text = shippingAddress.shippingDetail.label

        binding.etFirstName.setText(shippingAddress.shippingDetail.firstName)
        binding.etLastName.setText(shippingAddress.shippingDetail.lastName)

        regionsAdapter.updateSelectedItem(shippingAddress.shippingDetail.address.countryCode)
        binding.tvRegion.text = shippingAddress.shippingDetail.address.countryCode

        statesAdapter.updateSelectedItem(shippingAddress.shippingDetail.address.state)
        binding.tvState.text = shippingAddress.shippingDetail.address.state

        citiesAdapter.updateSelectedItem(shippingAddress.shippingDetail.address.city)
        binding.tvCity.text = shippingAddress.shippingDetail.address.city

        binding.etAddressLine1.setText(shippingAddress.shippingDetail.address.street)
        binding.etPostalCode.setText(shippingAddress.shippingDetail.address.zipCode)
        binding.etPhone.setText(shippingAddress.shippingDetail.contact.number)

        isDefault = shippingAddress.shippingDetail.isDefault
        binding.switchSetAsDefault.isChecked = shippingAddress.shippingDetail.isDefault
        binding.switchSetAsDefault.isEnabled = !shippingAddress.shippingDetail.isDefault
    }

    private fun uiListeners() {
        binding.btnSave.setOnClickListener {
            initFields()
            if (areAllFieldsValid()) {
                saveOrUpdateShippingAddress()
            }
        }

        binding.ivBack.setOnClickListener {
            goBack()
        }
        binding.containerAddressLabel.setOnClickListener {
            showBottomSheet(addressLabelsBottomSheet)
        }

        binding.containerRegion.setOnClickListener {
            showBottomSheet(regionBottomSheet)
        }
        binding.containerState.setOnClickListener {
            showBottomSheet(stateBottomSheet)
        }
        binding.containerCity.setOnClickListener {
            showBottomSheet(cityBottomSheet)
        }

        addressLabelsAdapter.interaction = { selectedItem ->
            binding.tvAddressLabel.text = selectedItem
            closeBottomSheet(addressLabelsBottomSheet)
        }

        regionsAdapter.interaction = { selectedItem ->
            binding.tvRegion.text = selectedItem
            chooseCountry(selectedItem)
            closeBottomSheet(regionBottomSheet)
        }
        statesAdapter.interaction = { selectedItem ->
            binding.tvState.text = selectedItem
            chooseState(selectedItem)
            closeBottomSheet(stateBottomSheet)
        }
        citiesAdapter.interaction = { selectedItem ->
            binding.tvCity.text = selectedItem
            closeBottomSheet(cityBottomSheet)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            closeBottomsSheetElseGoBack()
        }

        addressLabelsBottomSheet.onStateChange { state ->
            onBottomSheetStateChange(state)
        }

        regionBottomSheet.onStateChange { state ->
            onBottomSheetStateChange(state)
        }
        stateBottomSheet.onStateChange { state ->
            onBottomSheetStateChange(state)
        }
        cityBottomSheet.onStateChange { state ->
            onBottomSheetStateChange(state)
        }

        binding.bgDim.setOnClickListener {
            // do nothing, just add to avoid click on views that are behind of bg dim when bg dim is visible
        }
    }

    private fun chooseCountry(countryName: String) {
        shippingAddressViewModel.countryResponse.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Data -> {
                    val country = it.data.countries.filter { mCountry->
                        mCountry.name.equals(countryName, ignoreCase = true)
                    }
                    countryCode= country.first().isoCode
                    getStates(country.first().isoCode)

                }
            }
        }

    }
    private fun chooseState(stateName: String) {
        shippingAddressViewModel.statesResponse.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Data -> {
                    val country = it.data.states.filter { mState->
                        mState.name.equals(stateName, ignoreCase = true)
                    }
                    state= country.first().isoCode
                    getCities(country.first().countryCode,country.first().isoCode)

                }
            }
        }

    }

    private fun getStates(countryCode : String)
    {
        shippingAddressViewModel.getStates(GetStatesRequest(countryCode))

    }
    private fun getCities(countryCode : String, stateCode:String)
    {
        shippingAddressViewModel.getCities(GetCitiesRequest(countryCode,stateCode))

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


        shippingAddressViewModel.countryResponse.observe(viewLifecycleOwner) {
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
                    val countries = it.data.countries.map { country -> country.name }
                    regionsAdapter.submitList(countries)
                }
            }
        }
        shippingAddressViewModel.statesResponse.observe(viewLifecycleOwner) {
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
                    val states = it.data.states.map { state -> state.name }
                    statesAdapter.submitList(states)
                }
            }
        }
        shippingAddressViewModel.citiesResponse.observe(viewLifecycleOwner) {
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
                    val cities = it.data.cities.map { city -> city.name }
                    citiesAdapter.submitList(cities)
                }
            }
        }
    }


    private fun saveOrUpdateShippingAddress() {
        val id = args.shippingAddress?.id ?: ""
        if (id != "") {
            val shippingAddress = createShippingAddress(id)
            shippingAddressViewModel.updateShippingAddress(shippingAddress)
        } else {
            val shippingAddress = createShippingAddress()
            shippingAddressViewModel.addShippingAddress(shippingAddress)
        }
    }

    private fun createShippingAddress(id: String? = null): PostShippingAddress {
        val phoneNo: PhoneNo = PhoneNo(
            countryCode,
            number =phoneNo
        )
        val address: Address = Address(
            city,
            countryCode,
            state,
            street=addressLine1,
            zipCode,
        )
        val shippingDetail = PostShippingDetail(
            address = address,
            contact = phoneNo,
            firstName,
            label = addressLabel,
            lastName,
        )

        return PostShippingAddress(
            postShippingDetail = shippingDetail
        )
    }


    private fun initFields() {
        firstName = binding.etFirstName.text.toString()
        lastName = binding.etLastName.text.toString()
        addressLabel = binding.tvAddressLabel.text.toString()
        city = binding.tvCity.text.toString()
        addressLine1 = binding.etAddressLine1.text.toString()
        addressLine2 = binding.etAddressLine2.text.toString()
        zipCode = binding.etPostalCode.text.toString()
        phoneNo = binding.etPhone.text.toString()
        isDefault = binding.switchSetAsDefault.isChecked
    }

    private fun areAllFieldsValid(): Boolean {
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

    private fun showBottomSheet(bottomSheet: BottomSheetBehavior<View>) {
        binding.bgDim.fadeVisibility(true)
        bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun closeBottomSheet(bottomSheet: BottomSheetBehavior<View>) {
        binding.bgDim.fadeVisibility(false)
        bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun onBottomSheetStateChange(state: Int) {
        val isClosed = state == BottomSheetBehavior.STATE_HIDDEN
        if (isClosed) {
            binding.bgDim.fadeVisibility(false, 200)
        }
    }

    private fun closeBottomsSheetElseGoBack() {
        if (addressLabelsBottomSheet.state != BottomSheetBehavior.STATE_HIDDEN
        ) {
            closeBottomSheet(addressLabelsBottomSheet)
            return
        }

        if (regionBottomSheet.state != BottomSheetBehavior.STATE_HIDDEN) {
            closeBottomSheet(regionBottomSheet)
            return
        }
        if (stateBottomSheet.state != BottomSheetBehavior.STATE_HIDDEN) {
            closeBottomSheet(stateBottomSheet)
            return
        }
        if (cityBottomSheet.state != BottomSheetBehavior.STATE_HIDDEN) {
            closeBottomSheet(cityBottomSheet)
            return
        }

        goBack()
    }

}