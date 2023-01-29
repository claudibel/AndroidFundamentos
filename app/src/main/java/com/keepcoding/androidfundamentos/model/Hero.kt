package com.keepcoding.androidfundamentos.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Hero(
    val id: String,
    val name: String,
    var photo: String,
    val order: Int,
    val maxHealth: Int,
    var currentHealth: Int
): Parcelable
