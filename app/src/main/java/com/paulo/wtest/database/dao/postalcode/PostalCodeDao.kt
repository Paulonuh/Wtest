package com.paulo.wtest.database.dao.postalcode

import androidx.paging.PagingSource
import androidx.room.*
import com.paulo.wtest.base.BaseDao
import com.paulo.wtest.model.postalcode.PostalCode


@Dao
interface PostalCodeDao : BaseDao<PostalCode> {

    @Transaction
    @Query("SELECT * FROM postal_code")
    fun getAllPostalCodes(): PagingSource<Int, PostalCode>

    @Transaction
    @Query("SELECT * FROM postal_code WHERE name like '%' || :query || '%' " +
            "OR code like '%' || :query || '%' " +
            "OR ext_code like '%' || :query || '%' " +
            "OR lower(search_full_name) like '%' || :queryLower || '%' ")
    fun getPostalCodeSearch(query: String, queryLower:String): PagingSource<Int, PostalCode>

}