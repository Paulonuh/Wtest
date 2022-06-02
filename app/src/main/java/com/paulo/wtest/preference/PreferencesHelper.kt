package com.paulo.wtest.preference

import android.content.Context
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

/**
 * Created by Paulo Henrique Teixeira.
 */


class PreferencesHelper @Inject constructor(@ApplicationContext private val context: Context) {

    companion object {
        const val DOWNLOADED = "downloaded"
        const val LAST_DOWNLOAD_ID = "lastDownloadId"
    }

    suspend fun clearAllData(): Boolean {
        return suspendCancellableCoroutine { continuation ->
            try {
                val isCleared = clearAll()

                if (isCleared) {
                    continuation.resume(true)
                } else {
                    continuation.cancel(Throwable())
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
                continuation.cancel(exception)
            }
        }
    }


    //region Download
    fun isDownloaded() =
        getBoolean(DOWNLOADED, false)

    fun saveIsDownloaded(downloaded: Boolean) =
        putBoolean(DOWNLOADED, downloaded)

    fun getLastDownloadId() = getLong(LAST_DOWNLOAD_ID)

    fun saveLastDownloadId(id: Long) = putLong(LAST_DOWNLOAD_ID, id)

    fun clearLastDownloadId() {
        remove(LAST_DOWNLOAD_ID)
    }

    //region Methods Preferences
    private fun putString(key: String, value: String?) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(key: String): String? {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getString(key, null)
    }

    private fun putInt(key: String, value: Int) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    private fun getInt(key: String): Int {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getInt(key, 0)
    }

    private fun putLong(key: String, value: Long) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    private fun getLong(key: String): Long {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getLong(key, 0)
    }

    private fun putBoolean(key: String, value: Boolean) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    private fun getBoolean(key: String): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getBoolean(key, false)
    }

    private fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    private fun contains(key: String): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.contains(key)
    }

    private fun remove(key: String) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.edit().remove(key).apply()
    }

    private fun clearAll(): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.edit().clear().commit()
    }
    //endregion Methods Preferences
}