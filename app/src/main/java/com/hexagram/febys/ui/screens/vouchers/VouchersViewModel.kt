package com.hexagram.febys.ui.screens.vouchers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hexagram.febys.base.BaseViewModel
import com.hexagram.febys.models.api.vouchers.Voucher
import com.hexagram.febys.network.DataState
import com.hexagram.febys.repos.IVoucherRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VouchersViewModel @Inject constructor(
    private val vouchersRepo: IVoucherRepo
) : BaseViewModel() {
    private val _observeVouchers = MutableLiveData<DataState<List<Voucher>>>()
    val observeVouchers: LiveData<DataState<List<Voucher>>> = _observeVouchers

    private val _observeCollectVoucher = MutableLiveData<DataState<Unit>>()
    val observeCollectVoucher: LiveData<DataState<Unit>> = _observeCollectVoucher

    init {
        fetchVouchers()
    }

    fun fetchVouchers() = viewModelScope.launch {
        _observeVouchers.postValue(DataState.Loading())
        vouchersRepo.fetchVouchers().collect {
            _observeVouchers.postValue(it)
        }
    }

    fun collectVoucher(voucher: String) = viewModelScope.launch {
        _observeCollectVoucher.postValue(DataState.Loading())
        vouchersRepo.collectVouchers(voucher).collect {
            if (it is DataState.Data) fetchVouchers()
            else _observeCollectVoucher.postValue(it)
        }
    }
}