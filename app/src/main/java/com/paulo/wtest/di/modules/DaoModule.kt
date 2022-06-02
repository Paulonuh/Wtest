package com.paulo.wtest.di.modules

import android.content.Context
import androidx.room.Room
import com.paulo.wtest.database.AppDatabase
import com.paulo.wtest.database.dao.postalcode.PostalCodeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class DaoModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context, AppDatabase::class.java, AppDatabase.NAME
        ).build()
    }

    @Provides
    @Singleton
    fun providePostalCodeDao(appDatabase: AppDatabase): PostalCodeDao {
        return appDatabase.postalCodeDao()
    }

}