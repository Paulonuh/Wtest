package com.paulo.wtest.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.paulo.wtest.R
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Paulo Henrique Teixeira.
 */

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}