package com.paulo.wtest.service.download

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import com.paulo.wtest.R
import com.paulo.wtest.extensions.isNullOrZero
import com.paulo.wtest.helper.notification.NotificationHelper
import com.paulo.wtest.preference.PreferencesHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.TimeoutException

/**
 * Created by Paulo Henrique Teixeira.
 */

class DownloadService : Service() {

    companion object {
        const val PROGRESS_UPDATE_DELAY = 300L
        const val MAX_DOWNLOAD_TIME_TO_START = 30_000L
        const val MAX_DOWNLOAD_TIME_TO_FINISH = 300_000L
        const val FILENAME = "codigos_postais.csv"
        const val URL =
            "https://raw.githubusercontent.com/centraldedados/codigos_postais/master/data/codigos_postais.csv"
    }

    //region Values
    private val mHandler by lazy { Handler(Looper.myLooper()!!) }
    private val mHandlerTimer by lazy { Handler(Looper.myLooper()!!) }

    private val receiverComplete = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            manageDownloadResult()
        }
    }

    private val mThreadProgress = object : Thread() {
        override fun run() {
            val progress = getProgress()

            if (progress == 0) {
                mDownloadListener?.onStart()
            }

            if (progress < 100) {
                mDownloadListener?.onProgressUpdated(progress)
                mHandler.postDelayed(this, PROGRESS_UPDATE_DELAY)
            }
        }
    }

    private val mThreadTimer = object : Thread() {
        override fun run() {
            // Se passar de 5 minutos e o download não tiver terminado ou
            // passar de 30 segundos e o download não tiver saído do zero,
            // o download é cancelado
            if (isTimeToFinishDownloadExceed() || isTimeToStartDownloadExceed()) {
                mIdDownloadManager?.let {
                    mDownloadManager?.remove(it)
                }
            } else {
                mDownloadTime += 1000L
                mHandlerTimer.postDelayed(this, 1000)
            }
        }
    }
    //endregion Values

    //region Variables
    private var mPath = ""
    private var mDownloadTime = 0L
    private var mDownloadAttempt = 1
    private var mIdDownloadManager: Long? = null
    private var mDownloadManager: DownloadManager? = null
    private var mDownloadListener: DownloadListener? = null
    private var mPreferencesHelper: PreferencesHelper? = null
    //endregion Variables

    //region Service
    override fun onCreate() {
        super.onCreate()

        mPreferencesHelper = PreferencesHelper(this@DownloadService)

        mDownloadManager?.let {
            mHandler.post(mThreadProgress)
            mHandlerTimer.post(mThreadTimer)
        }

        registerReceiver(receiverComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    override fun onBind(intent: Intent?) = DownloadBinder(this)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (mDownloadManager == null) {
            mDownloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            startDownload()
        }

        return START_STICKY
    }

    override fun onDestroy() {
        unregisterReceiver(receiverComplete)
        mHandler.removeCallbacks(mThreadProgress)
        mHandlerTimer.removeCallbacks(mThreadTimer)

        super.onDestroy()
    }
    //endregion Service

    //region Local
    private fun startDownload() {
        try {
            removePreviousIncompleteDownloads()

            mPath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.path + "/" + FILENAME
            val file = File(mPath)
            val request = DownloadManager.Request(Uri.parse(URL))
            request.setTitle(getString(R.string.app_name))
            request.setDescription(getString(R.string.downloading))
            request.setDestinationUri(Uri.fromFile(file))
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI
            )

            mDownloadTime = 0L
            mIdDownloadManager = mDownloadManager?.enqueue(request)

            mIdDownloadManager?.let {
                mPreferencesHelper?.saveLastDownloadId(it)
            }

            mHandler.post(mThreadProgress)
            mHandlerTimer.post(mThreadTimer)
        } catch (e: Exception) {
            onError()
        }
    }

    private fun removePreviousIncompleteDownloads() {
        val lastDownloadId = mPreferencesHelper?.getLastDownloadId()
        if (!lastDownloadId.isNullOrZero()) {
            mDownloadManager?.remove(lastDownloadId!!)
            mPreferencesHelper?.clearLastDownloadId()
        }
    }

    private fun onError() {
        mHandler.removeCallbacks(mThreadProgress)
        mHandlerTimer.removeCallbacks(mThreadTimer)

        if (mDownloadAttempt > 1) {
            val throwable = when {
                isTimeToFinishDownloadExceed() -> TimeoutException(getString(R.string.download_time_limit_exceed))
                isTimeToStartDownloadExceed() -> TimeoutException(getString(R.string.not_possible_start_download))
                else -> Throwable()
            }

            mDownloadListener?.onError(throwable)
            stopSelf()
        } else {
            startDownload()
            mDownloadAttempt++
        }
    }

    private fun isTimeToFinishDownloadExceed() = mDownloadTime > MAX_DOWNLOAD_TIME_TO_FINISH

    private fun isTimeToStartDownloadExceed() = mDownloadTime == MAX_DOWNLOAD_TIME_TO_START && getProgress() == 0

    @SuppressLint("Range")
    private fun getProgress(): Int {
        mDownloadManager?.let { downloadManager ->
            val query = DownloadManager.Query().setFilterById(mIdDownloadManager ?: 0L)

            val cursor: Cursor? = downloadManager.query(query)

            if (cursor?.moveToFirst() != true) {
                cursor?.close()
                return 0
            }

            val downloaded = cursor.getLong(
                cursor.getColumnIndex(
                    DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR
                )
            )
            val fileSize: Long? = cursor.getLong(
                cursor.getColumnIndex(
                    DownloadManager.COLUMN_TOTAL_SIZE_BYTES
                )
            )

            cursor.close()

            return if (fileSize == null || fileSize == 0L) {
                0
            } else {
                ((downloaded * 100) / fileSize).toInt()
            }
        }

        return 0
    }

    @SuppressLint("Range")
    private fun manageDownloadResult() {
        try {
            val query = DownloadManager.Query()

            mIdDownloadManager?.let {
                query.setFilterById(it)
            }

            mDownloadManager?.let { manager ->
                val cursor = manager.query(query)
                if (cursor != null) {
                    cursor.moveToFirst()
                    val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        mHandler.removeCallbacks(mThreadProgress)
                        mHandlerTimer.removeCallbacks(mThreadTimer)

                        finishProcess()
                    } else {
                        onError()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            onError()
        }
    }

    private fun finishProcess() {
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            try {
                mPreferencesHelper?.saveIsDownloaded(true)
                mPreferencesHelper?.clearLastDownloadId()
                mDownloadListener?.onComplete(mPath)

                showNotification(this@DownloadService)

                stopSelf()
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
    }

    private fun showNotification(context: Context) {
        NotificationHelper.createNotification(
            context,
            getString(R.string.downloaded_with_success)
        )
    }

    fun setDownloadListener(downloadListener: DownloadListener) {
        mDownloadListener = downloadListener
    }
    //endregion Local

    interface DownloadListener {
        fun onPause()
        fun onStart()
        fun onComplete(filePath: String)
        fun onError(exception: Throwable)
        fun onProgressUpdated(percent: Int)
    }
}