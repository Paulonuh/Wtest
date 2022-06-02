package com.paulo.wtest.helper.download

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.paulo.wtest.service.download.DownloadBinder
import com.paulo.wtest.service.download.DownloadService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Created by Paulo Henrique Teixeira.
 */
class DownloadHelper @Inject constructor(@ApplicationContext val context: Context) {

    private var mDownloadService: DownloadService? = null
    private var mDownloadListener: DownloadService.DownloadListener? = null

    fun initDownload(downloadListener: DownloadService.DownloadListener) {
        mDownloadListener = downloadListener

        startService()
    }

    private fun startService() {
        val intent = Intent(context, DownloadService::class.java)
        context.startService(intent)

        context.bindService(
            Intent(context, DownloadService::class.java),
            mDownloadServiceConnection,
            Context.BIND_IMPORTANT
        )
    }

    private val mDownloadServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            mDownloadService = null
        }

        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            mDownloadService = (iBinder as DownloadBinder).downloadService
            mDownloadListener?.let {
                mDownloadService?.setDownloadListener(it)
            }
        }
    }

}