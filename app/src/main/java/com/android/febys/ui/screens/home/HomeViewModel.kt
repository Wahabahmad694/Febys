package com.android.febys.ui.screens.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.febys.base.BaseViewModel
import com.android.febys.network.domain.models.*
import com.android.febys.network.response.Category
import com.android.febys.network.DataState
import com.android.febys.network.response.Banner
import com.android.febys.network.response.SeasonalOffer
import com.android.febys.network.response.UniqueCategory
import com.android.febys.repos.IHomeRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: IHomeRepo
) : BaseViewModel() {
    private val _observeUniqueCategories = MutableLiveData<DataState<List<UniqueCategory>>>()
    val observeUniqueCategories: LiveData<DataState<List<UniqueCategory>>> =
        _observeUniqueCategories

    private val _observeSliderImages = MutableLiveData<DataState<List<Banner>>>()
    val observeSliderImages: LiveData<DataState<List<Banner>>> = _observeSliderImages

    private val _observeTodayDeals = MutableLiveData<DataState<List<Product>>>()
    val observeTodayDeals: LiveData<DataState<List<Product>>> = _observeTodayDeals

    private val _observeFeaturedCategories = MutableLiveData<DataState<List<Category>>>()
    val observeFeaturedCategories: LiveData<DataState<List<Category>>> = _observeFeaturedCategories

    private val _observeSeasonalOffers = MutableLiveData<DataState<List<SeasonalOffer>>>()
    val observeSeasonalOffers: LiveData<DataState<List<SeasonalOffer>>> = _observeSeasonalOffers

    private val _observeFeaturedCategoryProducts = MutableLiveData<DataState<List<Product>>>()
    val observeFeaturedCategoryProducts: LiveData<DataState<List<Product>>> =
        _observeFeaturedCategoryProducts

    private val _observeTrendingProducts = MutableLiveData<DataState<List<Product>>>()
    val observeTrendingProducts: LiveData<DataState<List<Product>>> = _observeTrendingProducts

    private val _observeStoreYouFollow = MutableLiveData<DataState<List<String>>>()
    val observeStoreYouFollow: LiveData<DataState<List<String>>> = _observeStoreYouFollow

    private val _observeUnder100DollarsItems = MutableLiveData<DataState<List<Product>>>()
    val observeUnder100DollarsItems: LiveData<DataState<List<Product>>> =
        _observeUnder100DollarsItems


    fun fetchAllBanner() {
        viewModelScope.launch {
            _observeSliderImages.postValue(DataState.Loading())
            repo.fetchAllBanner().collect {
                _observeSliderImages.postValue(it)
            }
        }
    }

    fun fetchUniqueCategory() {
        viewModelScope.launch {
            _observeUniqueCategories.postValue(DataState.Loading())
            repo.fetchAllUniqueCategories().collect {
                _observeUniqueCategories.postValue(it)
            }
        }
    }

    fun fetchTodayDeals() {
        viewModelScope.launch {
            _observeTodayDeals.postValue(DataState.Loading())
            repo.fetchTodayDeals().collect {
                _observeTodayDeals.postValue(it)
            }
        }
    }

    fun fetchFeaturedCategories() {
        viewModelScope.launch {
            _observeFeaturedCategories.postValue(DataState.Loading())
            repo.fetchFeaturedCategories().collect {
                _observeFeaturedCategories.postValue(it)
            }
        }
    }

    fun fetchAllSeasonalOffers() {
        viewModelScope.launch {
            _observeSeasonalOffers.postValue(DataState.Loading())
            repo.fetchAllSeasonalOffers().collect {
                _observeSeasonalOffers.postValue(it)
            }
        }
    }

    fun fetchFeaturedCategoryProducts() {
        viewModelScope.launch {
            _observeFeaturedCategoryProducts.postValue(DataState.Loading())
            repo.fetchFeaturedCategoryProducts().collect {
                _observeFeaturedCategoryProducts.postValue(it)
            }
        }
    }

    fun fetchTrendingProducts() {
        viewModelScope.launch {
            _observeTrendingProducts.postValue(DataState.Loading())
            repo.fetchTrendingProducts().collect {
                _observeTrendingProducts.postValue(it)
            }
        }
    }

    fun fetchStoresYouFollow() {
        viewModelScope.launch {
            _observeStoreYouFollow.postValue(DataState.Loading())
            repo.fetchStoresYouFollow().collect {
                _observeStoreYouFollow.postValue(it)
            }
        }
    }

    fun fetchUnder100DollarsItems() {
        viewModelScope.launch {
            _observeUnder100DollarsItems.postValue(DataState.Loading())
            repo.fetchUnder100DollarsItems().collect {
                _observeUnder100DollarsItems.postValue(it)
            }
        }
    }

}