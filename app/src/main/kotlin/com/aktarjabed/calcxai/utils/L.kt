package com.aktarjabed.calcxai.utils

import android.util.Log
import com.aktarjabed.calcxai.BuildConfig

object L {
  fun d(tag: String, msg: String) {
    if (BuildConfig.DEBUG) {
      Log.d(tag, msg)
    }
  }

  fun e(tag: String, msg: String, t: Throwable? = null) {
    Log.e(tag, msg, t)
  }
}
