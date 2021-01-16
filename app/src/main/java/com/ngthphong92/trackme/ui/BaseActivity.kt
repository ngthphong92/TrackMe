package com.ngthphong92.trackme.ui

import androidx.appcompat.app.AppCompatActivity

open class BaseActivity<T> : AppCompatActivity() {
    var activityBinding: T? = null
}