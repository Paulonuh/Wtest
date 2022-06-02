package com.paulo.wtest.repository

import androidx.paging.PagingSource
import com.paulo.wtest.database.AppDatabase
import com.paulo.wtest.model.postalcode.PostalCode
import javax.inject.Inject


/**
 * Created by Paulo Henrique Teixeira.
 */

class PostalCodeRepository @Inject constructor(
    private val appDatabase: AppDatabase
) {

    fun getPostalCodeSearch(query: String, queryLower:String): PagingSource<Int, PostalCode> {
        return appDatabase.postalCodeDao().getPostalCodeSearch(query, queryLower)
    }

}