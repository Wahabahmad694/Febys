package com.hexagram.febys.ui.screens.location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragmentWithPermission
import com.hexagram.febys.databinding.FragmentLocationBinding
import com.hexagram.febys.utils.*
import com.hexagram.yahuda.ui.activity.location.adapter.SearchLocationAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*


@AndroidEntryPoint
class LocationFragment : BaseFragmentWithPermission(), OnMapReadyCallback {


    companion object {
        const val LOCATION = "LOCATION"

        const val POSITION = "POSITION"

//        fun getIntent(context: Context, latLng: LatLng?): Intent {
//            return Intent(context, LocationActivity::class.java).apply {
//                putExtra(POSITION, latLng)
//            }
//        }
    }

    private val viewModel: LocationViewModel by viewModels()
    private var googleMap: GoogleMap? = null
    private var hasPreviousLocation = false

    private lateinit var binding: FragmentLocationBinding


    private fun getPosition(): LatLng? {
        val position = arguments?.getParcelable<LatLng>(POSITION)
        return position
    }

    private val searchLocationAdapter: SearchLocationAdapter by lazy {
        SearchLocationAdapter(object : SearchLocationAdapter.Interaction {
            override fun onItemSelected(position: Int, item: AutocompletePrediction) {
                hideKeyboard()
                viewModel.getPlaceFromSuggestions(item.placeId)
                searchLocationAdapter.submitList(listOf())
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = FragmentLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun getPermissionGranted() {
//        viewModel.getCurrentLocation()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()

        pickAddressUI()

        setupSearchLocationRV()

        getMapAsync()
    }

    private fun chooseLocation() {

        if (viewModel.pickupLocationView.value == null || binding.locationEt.text.toString()
                .isStringEmpty()
        ) {
            goBack()
        } else {
            setFragmentResult(
                LOCATION, bundleOf(LOCATION to viewModel.pickupLocationView.value)
            )
        }
        goBack()
    }

    private fun setupListeners() = binding.apply {
        clearIV.setOnClickListener {
            binding.locationEt.setText("")
            searchLocationAdapter.submitList(listOf())

        }

        ivCurrentLocation.setOnClickListener {
            viewModel.getCurrentLocation()
        }

        btnSave.setOnClickListener {
            chooseLocation()
        }

        backIV.setOnClickListener {
            goBack()
        }
    }

    private fun observers() {

        viewModel.pickupLocationView.observe(this) {

            binding.locationEt.setText(it?.name ?: "")
            binding.locationEt.clearFocus()
            if (it?.lat != null && it.lng != null) {
                moveToLocation(it.lat ?: 0.0, it.lng ?: 0.0)
            } else if (!hasPreviousLocation) {

                viewModel.getCurrentLocation()

            }
        }

    }

    private fun pickAddressUI() {

        lifecycleScope.launchWhenStarted {

            binding.apply {

                locationEt.getQueryTextChangeStateFlow()
                    .debounce(300)
                    .filter { query ->
                        var mQuery = query
                        if (mQuery.isEmpty() || viewModel.hasAddress(binding.locationEt.text.toString())) {
                            searchLocationAdapter.submitList(listOf())
                            mQuery = ""
                        }
                        return@filter mQuery.isNotEmpty()
                    }
                    .distinctUntilChanged()
                    .flatMapLatest { query ->

                        viewModel.getLocationPredictions(query).flowOn(Dispatchers.IO)
                            .catch {
                                emitAll(flowOf(listOf()))
                            }
                    }
                    .flowOn(Dispatchers.Main)
                    .collect { result ->

                        searchLocationAdapter.submitList(result)

                    }


            }


        }
    }


    private fun setupSearchLocationRV() {

        binding.suggestionsRv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.suggestionsRv.adapter = searchLocationAdapter
        binding.suggestionsRv.addItemDecoration(
            DividerItemDecoration(
                binding.suggestionsRv.context,
                DividerItemDecoration.VERTICAL
            )
        )
    }

    private fun checkPermissions(): Boolean {
        return enableRunTimePermission(CommonMethods.getStorageAndCameraPermissions())
    }


    private fun getMapAsync() {
        if (checkPermissions()) {
            val mapFragment = childFragmentManager
                .findFragmentById(R.id.map_fragment) as SupportMapFragment
            mapFragment.getMapAsync(this)
        }
    }

    override fun onMapReady(p0: GoogleMap?) {

        googleMap = p0
        moveToPreviousChooseLocation()
        googleMap?.setOnCameraIdleListener {
            if (hasPreviousLocation) {
                moveCamera(getPosition()?.latitude ?: 0.0, getPosition()?.longitude ?: 0.0)
                hasPreviousLocation = false

            } else {
                getAddressFromLatLng(p0?.cameraPosition?.target)

            }
        }
//        setupLocationObserver()
        observers()


    }

    private fun moveToPreviousChooseLocation() {
        getPosition()?.let {
            hasPreviousLocation = true
        }
    }

    private fun moveToLocation(latitude: Double, longitude: Double) {
        val currentPosition = googleMap?.cameraPosition?.target
        if (viewModel.pickupLocationView.value == null || currentPosition?.latitude != latitude && currentPosition?.longitude != latitude) {
            moveCamera(latitude, longitude)
        }
    }

    private fun moveCamera(latitude: Double, longitude: Double) {
        googleMap?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(latitude, longitude),
                15f
            )
        )
    }

    private fun getAddressFromLatLng(latLng: LatLng?) {
        latLng?.let { latLng ->
            viewModel.getAddressFromLatLng(latLng.latitude, latLng.longitude)

        }

    }

}