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
    private val observeUniqueCategories = MutableLiveData<Resource<List<UniqueCategory>>>()
    private val observeSliderImages = MutableLiveData<Resource<List<String>>>()
    private val observeTodayDeals = MutableLiveData<Resource<List<Product>>>()
    private val observeFeaturedCategories = MutableLiveData<Resource<List<Category>>>()
    private val observeFeaturedCategoryProducts = MutableLiveData<Resource<List<Product>>>()
    private val observeTrendingProducts = MutableLiveData<Resource<List<Product>>>()
    private val observeStoreYouFollow = MutableLiveData<Resource<List<String>>>()
    private val observeUnder100DollarsItems = MutableLiveData<Resource<List<Product>>>()

    fun fetchSliderImages(): LiveData<Resource<List<String>>> {
        viewModelScope.launch {
            observeSliderImages.postValue(Resource.Loading())
            repo.fetchSliderImages().collect {
                observeSliderImages.postValue(it)
            }
        }

        return observeSliderImages
    }

    fun fetchUniqueCategory(): LiveData<Resource<List<UniqueCategory>>> {
        viewModelScope.launch {
            observeUniqueCategories.postValue(Resource.Loading())
            repo.fetchUniqueCategory().collect {
                observeUniqueCategories.postValue(it)
            }
        }

        return observeUniqueCategories
    }

    fun fetchTodayDeals(): LiveData<Resource<List<Product>>> {
        viewModelScope.launch {
            observeTodayDeals.postValue(Resource.Loading())
            repo.fetchTodayDeals().collect {
                observeTodayDeals.postValue(it)
            }
        }

        return observeTodayDeals
    }

    fun fetchFeaturedCategories(): LiveData<Resource<List<Category>>> {
        viewModelScope.launch {
            observeFeaturedCategories.postValue(Resource.Loading())
            repo.fetchFeaturedCategories().collect {
                observeFeaturedCategories.postValue(it)
            }
        }

        return observeFeaturedCategories
    }

    fun fetchFeaturedCategoryProducts(): LiveData<Resource<List<Product>>> {
        viewModelScope.launch {
            observeFeaturedCategoryProducts.postValue(Resource.Loading())
            repo.fetchFeaturedCategoryProducts().collect {
                observeFeaturedCategoryProducts.postValue(it)
            }
        }

        return observeFeaturedCategoryProducts
    }

    fun fetchTrendingProducts(): LiveData<Resource<List<Product>>> {
        viewModelScope.launch {
            observeTrendingProducts.postValue(Resource.Loading())
            repo.fetchTrendingProducts().collect {
                observeTrendingProducts.postValue(it)
            }
        }

        return observeTrendingProducts
    }

    fun fetchStoresYouFollow(): LiveData<Resource<List<String>>> {
        viewModelScope.launch {
            observeStoreYouFollow.postValue(Resource.Loading())
            repo.fetchStoresYouFollow().collect {
                observeStoreYouFollow.postValue(it)
            }
        }

        return observeStoreYouFollow
    }

    fun fetchUnder100DollarsItems(): LiveData<Resource<List<Product>>> {
        viewModelScope.launch {
            observeUnder100DollarsItems.postValue(Resource.Loading())
            repo.fetchUnder100DollarsItems().collect {
                observeUnder100DollarsItems.postValue(it)
            }
        }

        return observeUnder100DollarsItems
    }

}