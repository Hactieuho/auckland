package com.liz.auckland.resource

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Loading<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    fun isLoading() = this is Loading
    fun isSuccess() = this is Success && data != null
    fun isError() = this is Error
}