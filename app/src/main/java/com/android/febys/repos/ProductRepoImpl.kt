package com.android.febys.repos

import com.android.febys.R
import com.android.febys.dto.ProductDetailDTO
import com.android.febys.network.DataState
import com.android.febys.network.FebysBackendService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow

class ProductRepoImpl(service: FebysBackendService) : IProductRepo {
    override fun fetchProductDetail(
        productId: String, dispatcher: CoroutineDispatcher
    ) = flow<DataState<ProductDetailDTO>> {
        val productDetailD = ProductDetailDTO(
            "abc",
            "TCL",
            false,
            listOf(
                "res:///${R.drawable.ic_lcd}",
                "https://i.pinimg.com/originals/05/f0/22/05f02277a38817cc07b7e411b0d81927.jpg"
            ),
            "TCL 32-inch 3-Series 720p Roku Smart TV  (2018 Model)",
            58.12,
            "res:///${R.drawable.ic_product_verified_logo}",
            listOf(34.12, 26.90),
            mapOf(
                3 to 16.12, 4 to 20.24
            ),
            "TCL, one of the world's largest TV manufacturers in the world and America’s fastest growing TV brand, bring the latest in 4K technology and design. We've simplified TV so you can instantly enjoy endless entertainment. Choose from more than 5,000 streaming channels that feature 450,000 movies and TV episodes plus live sports, news, music, kids and family, food, science and tech, fitness, foreign language and so much more. The power consumption is 5 watts. Refresh Rate = 60Hz (Hertz).",
            "TCL",
            8.2,
            16.0 to 9.0,
            "32S335",
            "Easy Voice Control works with Amazon Alexa and Google Assistant; Advanced Digital TV Tuner with Live TV Channel Guide"
        )
    }
}