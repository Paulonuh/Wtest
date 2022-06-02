package com.paulo.wtest.helper.exception

import androidx.annotation.StringRes
import com.bumptech.glide.load.HttpException
import com.paulo.wtest.R
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException
import javax.inject.Inject


class ExceptionHelper @Inject constructor(){

    companion object {

        fun getMessage(exception: Throwable): Int {
            return getMessage(
                exception,
                R.string.there_was_fail_try_again
            )
        }

        private fun getMessage(
            exception: Throwable,
            @StringRes defaultMessageRes: Int
        ): Int {
            val message: Any? = when (exception) {
                is HttpException -> exception.message
                is TimeoutException ->
                    exception.message
                is UnknownHostException ->
                    R.string.no_internet_signal
                is SocketTimeoutException ->
                    R.string.no_server_response
                else ->
                    defaultMessageRes
            }

            return defaultMessageRes
        }
    }

}