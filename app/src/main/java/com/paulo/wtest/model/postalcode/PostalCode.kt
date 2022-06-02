package com.paulo.wtest.model.postalcode

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Paulo Henrique Teixeira.
 */

@Entity(tableName = "postal_code")
data class PostalCode(
    @PrimaryKey(autoGenerate = true)
    var id: Long?,
    var code: String,
    var ext_code: String,
    var name: String,
    var search_full_name: String,
)