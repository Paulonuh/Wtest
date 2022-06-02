package com.paulo.wtest.extensions

import androidx.fragment.app.Fragment


/**
 * Created by Paulo Henrique Teixeira.
 */

fun Fragment.showToast(message: String) = context?.toastLong(message)

fun Fragment.showToast(messageId: Int) = context?.toastLong(messageId)


