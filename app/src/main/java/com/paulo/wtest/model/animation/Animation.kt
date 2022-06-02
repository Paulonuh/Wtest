package com.paulo.wtest.model.animation

import androidx.annotation.AnimRes

/**
 * Created by Paulo Henrique Teixeira.
 */

data class FragmentTransitionAnimation(
    @AnimRes val enter: Int,
    @AnimRes val exit: Int,
    @AnimRes val popEnter: Int,
    @AnimRes val popExit: Int
)
