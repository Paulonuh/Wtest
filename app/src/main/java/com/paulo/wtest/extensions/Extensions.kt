package com.paulo.wtest.extensions

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.paulo.wtest.R
import java.text.Normalizer


/**
 * Created by Paulo Henrique Teixeira.
 */

fun isAtLeastPie(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
}

fun isAtLeastQ(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
}

fun isAtLeastR(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
}

fun Context.toastLong(text: CharSequence) = Toast.makeText(this, text, Toast.LENGTH_LONG).show()

fun Context.toastLong(resId: Int) = Toast.makeText(this, resId, Toast.LENGTH_LONG).show()

fun View?.increaseMarginTop(margin: Int) {
    this?.let {
        val layoutParams = it.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(
            layoutParams.leftMargin,
            layoutParams.topMargin + margin,
            layoutParams.rightMargin,
            layoutParams.bottomMargin
        )
        it.layoutParams = layoutParams
    }
}

fun CharSequence.unaccent(): String {
    val regexUnaccent = "\\p{InCombiningDiacriticalMarks}+".toRegex()

    val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
    return regexUnaccent.replace(temp, "")
}

fun Long?.isNullOrZero(): Boolean {
    return this == null || this == 0L
}