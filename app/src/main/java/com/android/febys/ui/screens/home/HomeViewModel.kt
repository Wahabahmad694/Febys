package com.android.febys.ui.screens.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.febys.models.*
import com.android.febys.models.responses.Category
import com.android.febys.repos.HomeRepo
import com.android.febys.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: HomeRepo
) : ViewModel() {
    private val _observeUniqueCategories = MutableLiveData<Resource<List<UniqueCategory>>>()
    val observeUniqueCategories: LiveData<Resource<List<UniqueCategory>>> = _observeUniqueCategories

    private val _observeSliderImages = MutableLiveData<Resource<List<String>>>()
    val observeSliderImages: LiveData<Resource<List<String>>> = _observeSliderImages

    private val _observeTodayDeals = MutableLiveData<Resource<List<Product>>>()
    val observeTodayDeals: LiveData<Resource<List<Product>>> = _observeTodayDeals

    private val _observeFeaturedCategories = MutableLiveData<Resource<List<Category>>>()
    val observeFeaturedCategories: LiveData<Resource<List<Category>>> = _observeFeaturedCategories

    private val _observeFeaturedCategoryProducts = MutableLiveData<Resource<List<Product>>>()
    val observeFeaturedCategoryProducts: LiveData<Resource<List<Product>>> =
        _observeFeaturedCategoryProducts

    private val _observeTrendingProducts = MutableLiveData<Resource<List<Product>>>()
    val observeTrendingProducts: LiveData<Resource<List<Product>>> = _observeTrendingProducts

    private val _observeStoreYouFollow = MutableLiveData<Resource<List<String>>>()
    val observeStoreYouFollow: LiveData<Resource<List<String>>> = _observeStoreYouFollow

    private val _observeUnder100DollarsItems = MutableLiveData<Resource<List<Product>>>()
    val observeUnder100DollarsItems: LiveData<Resource<List<Product>>> =
        _observeUnder100DollarsItems


    fun fetchSliderImages() {
        viewModelScope.launch {
            _observeSliderImages.postValue(Resource.Loading())
            repo.fetchSliderImages().collect {
                _observeSliderImages.postValue(it)
            }
        }
    }

    fun fetchUniqueCategory() {
        viewModelScope.launch {
            _observeUniqueCategories.postValue(Resource.Loading())
            repo.fetchUniqueCategory().collect {
                _observeUniqueCategories.postValue(it)
            }
        }
    }

    fun fetchTodayDeals() {
        viewModelScope.launch {
            _observeTodayDeals.postValue(Resource.Loading())
            repo.fetchTodayDeals().collect {
                _observeTodayDeals.postValue(it)
            }
        }
    }

    fun fetchFeaturedCategories() {
        viewModelScope.launch {
            _observeFeaturedCategories.postValue(Resource.Loading())
            repo.fetchFeaturedCategories().collect {
                _observeFeaturedCategories.postValue(it)
            }
        }
    }

    fun fetchFeaturedCategoryProducts() {
        viewModelScope.launch {
            _observeFeaturedCategoryProducts.postValue(Resource.Loading())
            repo.fetchFeaturedCategoryProducts().collect {
                _observeFeaturedCategoryProducts.postValue(it)
            }
        }
    }

    fun fetchTrendingProducts() {
        viewModelScope.launch {
            _observeTrendingProducts.postValue(Resource.Loading())
            repo.fetchTrendingProducts().collect {
                _observeTrendingProducts.postValue(it)
            }
        }
    }

    fun fetchStoresYouFollow() {
        viewModelScope.launch {
            _observeStoreYouFollow.postValue(Resource.Loading())
            repo.fetchStoresYouFollow().collect {
                _observeStoreYouFollow.postValue(it)
            }
        }
    }

    fun fetchUnder100DollarsItems() {
        viewModelScope.launch {
            _observeUnder100DollarsItems.postValue(Resource.Loading())
            repo.fetchUnder100DollarsItems().collect {
                _observeUnder100DollarsItems.postValue(it)
            }
        }
    }

}