package com.hexagram.febys.ui.screens.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hexagram.febys.models.api.product.Product
import com.hexagram.febys.models.view.HomeModel
import com.hexagram.febys.network.DataState
import com.hexagram.febys.repos.IHomeRepo
import com.hexagram.febys.repos.IProductRepo
import com.hexagram.febys.ui.screens.product.ProductViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepo: IHomeRepo,
    productRepo: IProductRepo,
) : ProductViewModel(productRepo) {
    private val _observeHomeModel = MutableLiveData<DataState<HomeModel>>()
    val observeHomeModel: LiveData<DataState<HomeModel>> = _observeHomeModel

    private val _observeStoreYouFollow = MutableLiveData<List<Product>>()
    val observeStoreYouFollow: LiveData<List<Product>> = _observeStoreYouFollow

    fun fetchHomeModel(isRefresh: Boolean = false, resetUiCallback: (() -> Unit)? = null) {
        viewModelScope.launch {
            if (!isRefresh && (_observeHomeModel.value != null && _observeHomeModel.value is DataState.Data)) {
                return@launch
            }
            _observeHomeModel.postValue(DataState.Loading())
            delay(300)
            val uniqueCategories = async { homeRepo.fetchAllUniqueCategories() }
            val banners = async { homeRepo.fetchAllBanner() }
            val todayDeals = async { homeRepo.fetchTodayDeals() }
            val featuredCategories = async { homeRepo.fetchFeaturedCategories() }
            val seasonalOffers = async { homeRepo.fetchAllSeasonalOffers() }
            val trendingProducts = async { homeRepo.fetchTrendingProducts() }
            val under100DollarsItems = async { homeRepo.fetchUnder100DollarsItems() }
            val editorsPickItems = async { homeRepo.fetchEditorsPickItems() }
            val featuredStores = async { homeRepo.fetchFeaturedStores() }

            val homeModel = HomeModel(
                uniqueCategories.await(),
                banners.await(),
                todayDeals.await(),
                featuredCategories.await(),
                seasonalOffers.await(),
                trendingProducts.await(),
                under100DollarsItems.await(),
                editorsPickItems.await(),
                featuredStores.await()
            )

            _observeHomeModel.postValue(DataState.Data(homeModel))
            resetUiCallback?.invoke()
        }
    }

    fun fetchStoreYouFollow() {
        viewModelScope.launch {
            val storeYouFollow = homeRepo.fetchStoresYouFollow()
            _observeStoreYouFollow.postValue(storeYouFollow)
        }
    }
}