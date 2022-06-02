package com.paulo.wtest.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.lang.Exception

/**
 * Created by Paulo Henrique Teixeira.
 */

abstract class BaseViewModel : ViewModel(), BaseViewModelContract {

    override val loading: LiveData<Boolean>
        get() = mLoading
    val mLoading = MutableLiveData<Boolean>()

    override val messaging: LiveData<Int>
        get() = mMessaging
    val mMessaging = MutableLiveData<Int>()

    protected fun defaultLaunch(
        errorMsgId: Int? = null,
        block: suspend CoroutineScope.() -> Unit

    ) {
        mLoading.postValue(true)
        viewModelScope.launch {
            try {
                block.invoke(this)
                mLoading.postValue(false)
            } catch (ex: Exception) {
                ex.printStackTrace()
                mLoading.postValue(false)
                errorMsgId?.let {
                    mMessaging.postValue(it)
                }
            }
        }
    }
}
