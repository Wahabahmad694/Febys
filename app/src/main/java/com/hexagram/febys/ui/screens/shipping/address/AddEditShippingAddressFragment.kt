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
import com.hexagram.febys.models.view.ShippingAddress
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.ui.screens.list.selection.ListSelectionAdapter
import com.hexagram.febys.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlin.random.Random

@AndroidEntryPoint
class AddEditShippingAddressFragment : Fragment() {
    companion object {
        const val ARG_IS_ADD_OR_UPDATE = "isAddOrUpdate"
    }

    private lateinit var binding: FragmentAddEditShippingAddressBinding
    private val shippingAddressViewModel: ShippingAddressViewModel by viewModels()
    private val args: AddEditShippingAddressFragmentArgs by navArgs()

    private val addressLabelsBottomSheet get() = BottomSheetBehavior.from(binding.bottomSheetAddressLabels.root)
    private val regionBottomSheet get() = BottomSheetBehavior.from(binding.bottomSheetRegion.root)

    private val addressLabelsAdapter = ListSelectionAdapter()
    private val regionsAdapter = ListSelectionAdapter()

    private var firstName: String = ""
    private var lastName: String = ""
    private var addressLabel: String = ""
    private var region: String = ""
    private var addressLine1: String = ""
    private var addressLine2: String = ""
    private var city: String = ""
    private var state: String = ""
    private var postalCode: String = ""
    private var phoneNo: String = ""
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

        binding.bottomSheetAddressLabels.rvSelectionList.apply {
            setHasFixedSize(true)
            adapter = addressLabelsAdapter
            addItemDecoration(
                DividerItemDecoration(context, (layoutManager as LinearLayoutManager).orientation)
            )
        }

        binding.bottomSheetRegion.rvSelectionList.apply {
            setHasFixedSize(true)
            adapter = regionsAdapter
            addItemDecoration(
                DividerItemDecoration(context, (layoutManager as LinearLayoutManager).orientation)
            )
        }

        val addressLabels = resources.getStringArray(R.array.address_labels).toMutableList()
        val regions = resources.getStringArray(R.array.regions).toMutableList()

        addressLabelsAdapter.submitList(addressLabels)
        regionsAdapter.submitList(regions)

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
            return
        }

        binding.labelShippingAddress.setText(R.string.label_edit_shipping_address)

        addressLabelsAdapter.updateSelectedItem(shippingAddress.addressLabel)
        binding.tvAddressLabel.text = shippingAddress.addressLabel

        binding.etFirstName.setText(shippingAddress.firstName)
        binding.etLastName.setText(shippingAddress.lastName)

        regionsAdapter.updateSelectedItem(shippingAddress.region)
        binding.tvRegion.text = shippingAddress.region

        binding.etAddressLine1.setText(shippingAddress.addressLine1)
        binding.etAddressLine2.setText(shippingAddress.addressLine2)
        binding.etCity.setText(shippingAddress.city)
        binding.etState.setText(shippingAddress.state)
        binding.etPostalCode.setText(shippingAddress.postalCode)
        binding.etPhone.setText(shippingAddress.phoneNo)

        isDefault = shippingAddress.isDefault
        binding.switchSetAsDefault.isChecked = shippingAddress.isDefault
        binding.switchSetAsDefault.isEnabled = !shippingAddress.isDefault
    }

    private fun uiListeners() {
        binding.btnSave.setOnClickListener {
            initFields()
            if (areAllFieldsValid()) {
                saveOrUpdateShippingAddress()
            }
        }

        binding.containerAddressLabel.setOnClickListener {
            showBottomSheet(addressLabelsBottomSheet)
        }

        binding.containerRegion.setOnClickListener {
            showBottomSheet(regionBottomSheet)
        }

        addressLabelsAdapter.interaction = { selectedItem ->
            binding.tvAddressLabel.text = selectedItem
            closeBottomSheet(addressLabelsBottomSheet)
        }

        regionsAdapter.interaction = { selectedItem ->
            binding.tvRegion.text = selectedItem
            closeBottomSheet(regionBottomSheet)
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

        binding.bgDim.setOnClickListener {
            // do nothing, just add to avoid click on views that are behind of bg dim when bg dim is visible
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
                    setFragmentResult(ARG_IS_ADD_OR_UPDATE, bundleOf(ARG_IS_ADD_OR_UPDATE to true))
                    goBack()
                }
            }
        }
    }

    private fun saveOrUpdateShippingAddress() {
        val id = args.shippingAddress?.id ?: -1
        if (id != -1) {
            val shippingAddress = createShippingAddress(id)
            shippingAddressViewModel.updateShippingAddress(shippingAddress)
        } else {
            val shippingAddress = createShippingAddress()
            shippingAddressViewModel.addShippingAddress(shippingAddress)
        }
    }

    private fun createShippingAddress(id: Int? = null): ShippingAddress {
        return ShippingAddress(
            id ?: Random.nextInt(),
            firstName,
            lastName,
            addressLabel,
            region,
            addressLine1,
            if (addressLine2.isNotEmpty()) addressLine2 else null,
            city,
            if (state.isNotEmpty()) state else null,
            postalCode,
            phoneNo,
            isDefault
        )
    }


    private fun initFields() {
        firstName = binding.etFirstName.text.toString()
        lastName = binding.etLastName.text.toString()
        addressLabel = binding.tvAddressLabel.text.toString()
        region = binding.tvRegion.text.toString()
        addressLine1 = binding.etAddressLine1.text.toString()
        addressLine2 = binding.etAddressLine2.text.toString()
        city = binding.etCity.text.toString()
        state = binding.etState.text.toString()
        postalCode = binding.etPostalCode.text.toString()
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

        if (addressLine2.isNotEmpty() && !Validator.isValidAddress(addressLine2)) {
            showErrorDialog(getString(R.string.error_enter_valid_address))
            return false
        }

        if (!Validator.isValidCity(city)) {
            showErrorDialog(getString(R.string.error_enter_valid_city))
            return false
        }

        if (state.isNotEmpty() && !Validator.isValidState(state)) {
            showErrorDialog(getString(R.string.error_enter_valid_state))
            return false
        }

        if (!Validator.isValidPostalCode(postalCode)) {
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

        goBack()
    }

}