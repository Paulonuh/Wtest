package com.paulo.wtest.service.download

import android.os.Binder

/**
 * Created by Paulo Henrique Teixeira.
 */

class DownloadBinder(val downloadService: DownloadService) : Binder()