package com.android.febys.repos

import com.android.febys.R
import com.android.febys.network.DataState
import com.android.febys.network.FebysWebCustomizationService
import com.android.febys.network.adapter.*
import com.android.febys.network.domain.models.Product
import com.android.febys.network.response.Banner
import com.android.febys.network.response.Category
import com.android.febys.network.response.SeasonalOffer
import com.android.febys.network.response.UniqueCategory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class HomeRepoImpl @Inject constructor(
    private val service: FebysWebCustomizationService
) : IHomeRepo {

    override fun fetchAllUniqueCategories(dispatcher: CoroutineDispatcher): Flow<DataState<List<UniqueCategory>>> {
        return flow<DataState<List<UniqueCategory>>> {
            service.fetchAllUniqueCategories()
                .onSuccess { emit(DataState.data(data!!)) }
                .onError { emit(DataState.error(message())) }
                .onException { emit(DataState.error(R.string.error_something_went_wrong)) }
                .onNetworkError { emit(DataState.error(R.string.error_no_network_connected)) }
        }.flowOn(dispatcher)
    }

    override fun fetchAllBanner(dispatcher: CoroutineDispatcher): Flow<DataState<List<Banner>>> {
        return flow<DataState<List<Banner>>> {
            service.fetchAllBanner()
                .onSuccess { emit(DataState.data(data!!)) }
                .onError { emit(DataState.error(message())) }
                .onException { emit(DataState.error(R.string.error_something_went_wrong)) }
                .onNetworkError { emit(DataState.error(R.string.error_no_network_connected)) }
        }.flowOn(dispatcher)
    }

    override fun fetchTodayDeals(dispatcher: CoroutineDispatcher): Flow<DataState<List<Product>>> {
        return flow {
            val list = getProductList()
            emit(DataState.Data(list))
        }.flowOn(dispatcher)
    }

    override fun fetchFeaturedCategories(dispatcher: CoroutineDispatcher): Flow<DataState<List<Category>>> {
        return flow {
            val list = getCategories()
            emit(DataState.Data(list))
        }.flowOn(dispatcher)
    }

    override fun fetchFeaturedCategoryProducts(dispatcher: CoroutineDispatcher): Flow<DataState<List<Product>>> {
        return flow {
            val list = getProductList()
            emit(DataState.Data(list))
        }.flowOn(dispatcher)
    }

    override fun fetchAllSeasonalOffers(dispatcher: CoroutineDispatcher): Flow<DataState<List<SeasonalOffer>>> {
        return flow<DataState<List<SeasonalOffer>>> {
            service.fetchAllSeasonalOffers()
                .onSuccess { emit(DataState.data(data!!)) }
                .onError { emit(DataState.error(message())) }
                .onException { emit(DataState.error(R.string.error_something_went_wrong)) }
                .onNetworkError { emit(DataState.error(R.string.error_no_network_connected)) }
        }.flowOn(dispatcher)
    }

    override fun fetchTrendingProducts(dispatcher: CoroutineDispatcher): Flow<DataState<List<Product>>> {
        return flow<DataState<List<Product>>> {
            val list = getProductList()
            emit(DataState.Data(list))
        }.flowOn(dispatcher)
    }

    override fun fetchStoresYouFollow(dispatcher: CoroutineDispatcher): Flow<DataState<List<String>>> {
        return flow {
            val list = listOf(
                "res:///${R.drawable.ic_shirt}",
                "res:///${R.drawable.ic_shirt}"
            )
            emit(DataState.Data(list))
        }.flowOn(dispatcher)
    }

    override fun fetchUnder100DollarsItems(dispatcher: CoroutineDispatcher): Flow<DataState<List<Product>>> {
        return flow {
            val list = getProductList()
            emit(DataState.Data(list))
        }.flowOn(dispatcher)
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
                1,
                "res:///${R.drawable.ic_shoes}",
                "Service Running Shoe",
                "Service Running Shoe",
                15.12,
                14.18,
                false
            ),
            Product(
                2,
                "res:///${R.drawable.ic_bag}",
                "Bags",
                "Bags",
                15.12,
                14.18,
                false
            )
        )
    }
}