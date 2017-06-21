package com.exallium.rxmvvmapp

import android.support.design.widget.TextInputLayout

fun TextInputLayout.applyError(error: String?) {
    this.isErrorEnabled = error.isNullOrEmpty()
    this.error = error
}
