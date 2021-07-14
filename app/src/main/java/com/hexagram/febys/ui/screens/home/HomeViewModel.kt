package com.hexagram.febys.ui.screens.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hexagram.febys.base.BaseViewModel
import com.hexagram.febys.models.view.HomeModel
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.response.*
import com.hexagram.febys.repos.IHomeRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepo: IHomeRepo
) : BaseViewModel() {
    private val _observeHomeModel = MutableLiveData<DataState<HomeModel>>()
    val observeHomeModel: LiveData<DataState<HomeModel>> = _observeHomeModel

    fun fetchHomeModel() {
        viewModelScope.launch {
            if (_observeHomeModel.value != null && _observeHomeModel.value is DataState.Data) {
                return@launch
            }
            _observeHomeModel.postValue(DataState.Loading())
            val uniqueCategories = async { homeRepo.fetchAllUniqueCategories() }
            val banners = async { homeRepo.fetchAllBanner() }
            val todayDeals = async { homeRepo.fetchTodayDeals() }
            val featuredCategories = async { homeRepo.fetchFeaturedCategories() }
            val seasonalOffers = async { homeRepo.fetchAllSeasonalOffers() }
            val trendingProducts = async { homeRepo.fetchTrendingProducts() }
            val storeYouFollow = async { homeRepo.fetchStoresYouFollow() }
            val under100DollarsItems = async { homeRepo.fetchUnder100DollarsItems() }

            val homeModel = HomeModel(
                uniqueCategories.await(),
                banners.await(),
                todayDeals.await(),
                featuredCategories.await(),
                seasonalOffers.await(),
                trendingProducts.await(),
                storeYouFollow.await(),
                under100DollarsItems.await()
            )

            _observeHomeModel.postValue(DataState.Data(homeModel))
        }
    }

}