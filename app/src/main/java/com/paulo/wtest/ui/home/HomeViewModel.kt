package com.paulo.wtest.ui.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.paulo.wtest.base.BaseViewModel
import com.paulo.wtest.database.dao.postalcode.PostalCodeDao
import com.paulo.wtest.helper.download.DownloadHelper
import com.paulo.wtest.helper.exception.ExceptionHelper
import com.paulo.wtest.model.postalcode.PostalCode
import com.paulo.wtest.preference.PreferencesHelper
import com.paulo.wtest.repository.PostalCodeRepository
import com.paulo.wtest.service.download.DownloadService
import com.paulo.wtest.util.CSVUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Created by Paulo Henrique Teixeira.
 */

class HomeViewModel @ViewModelInject constructor(
    private val postalCodeDao: PostalCodeDao,
    private val repository: PostalCodeRepository,
    private val downloadHelper: DownloadHelper,
    private val preferenceHelper: PreferencesHelper
) : BaseViewModel() {
    val ldDownloadProgress: MutableLiveData<Int> = MutableLiveData()
    val ldDownloadCompleted: MutableLiveData<Unit> = MutableLiveData()
    val ldAlreadyDownloaded: MutableLiveData<Unit> = MutableLiveData()
    val ldDbPopulating: MutableLiveData<Unit> = MutableLiveData()
    val ldDbPopulated: MutableLiveData<Unit> = MutableLiveData()

    private var query: String = ""
    private var queryLower: String = ""

    fun createDBFromCsv(filePath: String) {
        viewModelScope.launch {
            CoroutineScope(Dispatchers.IO).launch {
                ldDbPopulating.postValue(Unit)
                mLoading.postValue(true)
                val list = CSVUtil.readData(filePath)
                postalCodeDao.insertOrReplaceAll(list)
                mLoading.postValue(false)
                init()
                ldDbPopulated.postValue(Unit)
            }
        }
    }

    val flPostalCode: Flow<PagingData<PostalCode>> = Pager(
        config = PagingConfig(
            pageSize = 60,
            enablePlaceholders = true
        )
    ) {
        repository.getPostalCodeSearch(query.trim(), queryLower.trim())
    }.flow
        .cachedIn(viewModelScope)


    fun getPostalCode(text: String?) {
        query = text ?: ""
        queryLower = text?.lowercase() ?: ""
    }

    fun init() {
        if (preferenceHelper.isDownloaded()) {
            ldAlreadyDownloaded.postValue(Unit)
        } else {
            val listener = object : DownloadService.DownloadListener {
                override fun onPause() {
                    mLoading.postValue(false)
                }

                override fun onStart() {
                    mLoading.postValue(true)
                }

                override fun onComplete(filePath: String) {
                    mLoading.postValue(false)
                    ldDownloadCompleted.postValue(Unit)
                    createDBFromCsv(filePath)
                }

                override fun onError(exception: Throwable) {
                    mLoading.postValue(false)
                    mMessaging.postValue(ExceptionHelper.getMessage(exception))
                }

                override fun onProgressUpdated(percent: Int) {
                    if (ldDownloadProgress.value != percent) {
                        ldDownloadProgress.postValue(percent)
                    }
                }
            }
            downloadHelper.initDownload(listener)
        }
    }

}