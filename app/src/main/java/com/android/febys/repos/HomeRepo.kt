package com.android.febys.repos

import com.android.febys.R
import com.android.febys.models.*
import com.android.febys.models.responses.Category
import com.android.febys.utils.Resource
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@ViewModelScoped
class HomeRepo @Inject constructor() {

    fun fetchUniqueCategory(): Flow<Resource<List<UniqueCategory>>> {
        return flow<Resource<List<UniqueCategory>>> {
            val list = listOf(
                UniqueCategory(
                    "res:///${R.drawable.ic_motors_parts_and_accessories}",
                    "Motor Parts & Accessories"
                ),
                UniqueCategory(
                    "res:///${R.drawable.ic_handmade_items}",
                    "Handmade Items"
                ),
                UniqueCategory(
                    "res:///${R.drawable.ic_energy_service}",
                    "Energy Service"
                ),
                UniqueCategory(
                    "res:///${R.drawable.ic_promoted_offerings}",
                    "Promoted Offerings"
                ),
                UniqueCategory(
                    "res:///${R.drawable.ic_smart_livings}",
                    "Smart Livings"
                )
            )
            // save to cache
            emit(Resource.Data(list))
        }.catch {
            emit(Resource.getError(it))
        }.flowOn(Dispatchers.IO)
    }

    fun fetchSliderImages(): Flow<Resource<List<String>>> {
        return flow<Resource<List<String>>> {
            val list = listOf("res:///${R.drawable.slider_image}")
            // save to cache
            emit(Resource.Data(list))
        }.catch {
            emit(Resource.getError(it))
        }.flowOn(Dispatchers.IO)
    }

    fun fetchTodayDeals(): Flow<Resource<List<Product>>> {
        return flow<Resource<List<Product>>> {
            val list = getProductList()
            // save to cache
            emit(Resource.Data(list))
        }.catch {
            emit(Resource.getError(it))
        }.flowOn(Dispatchers.IO)
    }

    fun fetchFeaturedCategories(): Flow<Resource<List<Category>>> {
        return flow<Resource<List<Category>>> {
            val list = getCategories()
            // save to cache
            emit(Resource.Data(list))
        }.catch {
            emit(Resource.getError(it))
        }.flowOn(Dispatchers.IO)
    }

    fun fetchFeaturedCategoryProducts(): Flow<Resource<List<Product>>> {
        return flow<Resource<List<Product>>> {
            val list = getProductList()
            // save to cache
            emit(Resource.Data(list))
        }.catch {
            emit(Resource.getError(it))
        }.flowOn(Dispatchers.IO)
    }

    fun fetchTrendingProducts(): Flow<Resource<List<Product>>> {
        return flow<Resource<List<Product>>> {
            val list = getProductList()
            // save to cache
            emit(Resource.Data(list))
        }.catch {
            emit(Resource.getError(it))
        }.flowOn(Dispatchers.IO)
    }

    fun fetchStoresYouFollow(): Flow<Resource<List<String>>> {
        return flow<Resource<List<String>>> {
            val list = listOf(
                "res:///${R.drawable.ic_shirt}",
                "res:///${R.drawable.ic_shirt}"
            )
            // save to cache
            emit(Resource.Data(list))
        }.catch {
            emit(Resource.getError(it))
        }.flowOn(Dispatchers.IO)
    }

    fun fetchUnder100DollarsItems(): Flow<Resource<List<Product>>> {
        return flow<Resource<List<Product>>> {
            val list = getProductList()
            // save to cache
            emit(Resource.Data(list))
        }.catch {
            emit(Resource.getError(it))
        }.flowOn(Dispatchers.IO)
    }

    private fun getCategories(): List<Category> {
        return listOf(
            Category(
                54,
                null,
                "Birthday CARD",
                "birthday-Card",
                "test",
                "https://febys-dev.s3.us-east-2.amazonaws.com/category/9e8b160b-cd54-46d1-8c28-cc9254c09daa.jpg",
                1,
                0,
                "2021-02-24T11:24:03.000Z",
                "2021-03-31T12:14:58.000Z",
                "",
                "",
                0
            ),
            Category(
                56,
                null,
                "Moms 50th Birthday",
                "birthday-Card",
                "test",
                "https://febys-dev.s3.us-east-2.amazonaws.com/category/598900ea-10cb-4aa9-9287-9c6ab9db1e1c.jpg",
                1,
                0,
                "2021-02-24T11:24:03.000Z",
                "2021-03-31T12:14:58.000Z",
                "",
                "",
                0
            ),
            Category(
                58,
                null,
                "Cosmetics",
                "birthday-Card",
                "test",
                "https://febys-dev.s3.us-east-2.amazonaws.com/category/7e275638-60fc-4263-aa45-028fd6e4b0e7.jpg",
                1,
                0,
                "2021-02-24T11:24:03.000Z",
                "2021-03-31T12:14:58.000Z",
                "",
                "",
                0
            ),
            Category(
                53,
                null,
                "Birthday Cards",
                "birthday-Card",
                "test",
                "https://febys-dev.s3.us-east-2.amazonaws.com/category/7e275638-60fc-4263-aa45-028fd6e4b0e7.jpg",
                1,
                0,
                "2021-02-24T11:24:03.000Z",
                "2021-03-31T12:14:58.000Z",
                "",
                "",
                0
            ),
            Category(
                200,
                null,
                "Birthday Cards",
                "birthday-Card",
                "test",
                "https://febys-dev.s3.us-east-2.amazonaws.com/category/129d435b-719d-4caa-a8d2-8356294df9cb.png",
                1,
                0,
                "2021-02-24T11:24:03.000Z",
                "2021-03-31T12:14:58.000Z",
                "",
                "",
                0
            )
        )
    }

    private fun getProductList(): List<Product> {
        return listOf(
            Product(
                "res:///${R.drawable.ic_shoes}",
                "Service Running Shoe",
                15.12,
                14.18,
                false
            ),
            Product(
                "res:///${R.drawable.ic_bag}",
                "Bags",
                15.12,
                14.18,
                false
            )
        )
    }
}