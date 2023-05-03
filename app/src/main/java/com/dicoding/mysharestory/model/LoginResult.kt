package com.dicoding.mysharestory.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginResult(
    val name: String?,
    val token: String?,
    val userId: String?
) : Parcelable