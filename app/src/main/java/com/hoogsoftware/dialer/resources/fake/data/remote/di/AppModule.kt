package com.hoogsoftware.dialer.resources.fake.data.remote.di

import android.app.Application
import com.hoogsoftware.dialer.resources.fake.data.remote.MyApi
import com.hoogsoftware.dialer.resources.fake.data.remote.domain.MyRepositoryImp
import com.hoogsoftware.dialer.resources.fake.data.remote.domain.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun provideApi():MyApi{
        return Retrofit.Builder().baseUrl("https://test.com").build().create(MyApi::class.java)

    }

    @Provides
    @Singleton
    fun provideRepository(api: MyApi,app:Application):Repository  {
        return MyRepositoryImp(api,app)

    }


}