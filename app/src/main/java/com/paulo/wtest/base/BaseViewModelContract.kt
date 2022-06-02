package com.paulo.wtest.base

import androidx.lifecycle.LiveData

interface BaseViewModelContract {
    val loading: LiveData<Boolean>
    val messaging: LiveData<Int>
}
