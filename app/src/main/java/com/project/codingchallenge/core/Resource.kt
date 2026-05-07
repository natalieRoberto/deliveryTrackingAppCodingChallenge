package com.project.codingchallenge.core

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>() // Use 'out T' here too
    data class Error(val message: String, val throwable: Throwable? = null) : Resource<Nothing>()
}