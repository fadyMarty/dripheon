package com.fadymarty.dripheon.di

import com.fadymarty.dripheon.data.remote.ChatRemoteDataSource
import com.fadymarty.dripheon.data.remote.ChatRemoteDataSourceImpl
import com.fadymarty.dripheon.data.repository.ChatRepositoryImpl
import com.fadymarty.dripheon.domain.repository.ChatRepository
import com.fadymarty.dripheon.presentation.chat.ChatViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {

    single {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    singleOf(::ChatRemoteDataSourceImpl) { bind<ChatRemoteDataSource>() }

    singleOf(::ChatRepositoryImpl) { bind<ChatRepository>() }

    viewModelOf(::ChatViewModel)
}