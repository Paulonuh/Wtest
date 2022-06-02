package com.paulo.wtest.model.notification

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Paulo Henrique Teixeira.
 */

@Parcelize
data class Notification(
    val screen: Int,
    val data: Int? = null
) : Parcelable