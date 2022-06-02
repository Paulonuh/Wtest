package com.paulo.wtest.util

import android.util.Log
import com.paulo.wtest.extensions.unaccent
import com.paulo.wtest.model.postalcode.PostalCode
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.Charset


/**
 * Created by Paulo Henrique Teixeira.
 */

class CSVUtil {

    companion object {
        fun readData(pathFile: String): List<PostalCode> {
            val list: MutableList<PostalCode> = mutableListOf()

            val inputStream = File(pathFile).inputStream()
            val reader = BufferedReader(
                InputStreamReader(inputStream, Charset.forName("UTF-8"))
            )
            var line = ""
            try {
                reader.readLine()
                var code: String
                var extCode: String
                var name: String
                var searchFullName: String
                while (reader.readLine()?.also { line = it } != null) {
                    Log.d("MyActivity", "Line: $line")
                    val tokens = line.split(",".toRegex()).toTypedArray()
                    code = tokens[14]
                    extCode = tokens[15]
                    name = tokens[16]
                    searchFullName = "$code-$extCode ${name.unaccent()}"
                    val babyName = PostalCode(null, code, extCode, name, searchFullName)
                    list.add(babyName)
                }
            } catch (e: IOException) {
                Log.wtf("MyActivity", "Error reading data file on line$line", e)
                e.printStackTrace()
            }
            return list
        }
    }
}