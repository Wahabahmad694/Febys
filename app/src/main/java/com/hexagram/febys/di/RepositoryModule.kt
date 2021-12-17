package com.hexagram.febys.di

import com.hexagram.febys.repos.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindAuthRepo(repo: AuthRepoImpl): IAuthRepo

    @Singleton
    @Binds
    abstract fun bindSearchRepo(repo: SearchRepoImpl): ISearchRepo

    @Singleton
    @Binds
    abstract fun bindHomeRepo(repo: HomeRepoImpl): IHomeRepo

    @Singleton
    @Binds
    abstract fun bindProductRepo(repo: ProductRepoImpl): IProductRepo

    @Singleton
    @Binds
    abstract fun bindProductListingRepo(repo: ProductListingRepoImpl): IProductListingRepo

    @Singleton
    @Binds
    abstract fun bindCartRepo(repo: CartRepoImpl): ICartRepo

    @Singleton
    @Binds
    abstract fun bindShippingAddressRepo(repo: ShippingAddressRepoImpl): IShippingAddressRepo

    @Singleton
    @Binds
    abstract fun bindVendorRepo(repo: VendorRepoImpl): IVendorRepo

    @Singleton
    @Binds
    abstract fun bindCheckoutRepo(repo: CheckoutRepoImpl): ICheckoutRepo

    @Singleton
    @Binds
    abstract fun bindVouchersRepo(repo: VoucherRepoImpl): IVoucherRepo

    @Singleton
    @Binds
    abstract fun bindPaymentMethodRepo(repo: PaymentMethodRepoImpl): IPaymentMethodRepo

    @Singleton
    @Binds
    abstract fun bindOrderRepo(repo: OrderRepoImpl): IOrderRepo

    @Singleton
    @Binds
    abstract fun bindFiltersRepo(repo: FiltersRepoImpl): IFiltersRepo

    @Singleton
    @Binds
    abstract fun bindStoreMenuRepo(repo: StoreMenusRepoImpl): IStoreMenusRepo
}