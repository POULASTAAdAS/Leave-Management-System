package com.poulastaa.lms.data.remote

import android.util.Log
import com.google.gson.Gson
import com.poulastaa.lms.domain.utils.DataError
import com.poulastaa.lms.domain.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.net.CookieManager
import java.nio.channels.UnresolvedAddressException
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import okhttp3.Request as Req

val mediaType = "application/json".toMediaType()

suspend inline fun <reified Response : Any> OkHttpClient.uploadFile(
    route: String,
    file: File,
    gson: Gson
): Result<Response, DataError.Network> {
    val requestBody = file.asRequestBody("application/octet-stream".toMediaTypeOrNull())

    val url = constructRoute(route)

    val multipartBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("file", file.name, requestBody)
        .build()

    val req = Req.Builder().url(url)
        .post(multipartBody)
        .build()

    return try {
        val response = makeCall(req)
        responseToResult<Response>(response, gson)
    } catch (e: Exception) {
        handleOtherException(e)
    }
}


suspend inline fun <reified Request : Any, reified Response : Any> OkHttpClient.authPost(
    route: String,
    body: Request,
    gson: Gson
): Result<Response, DataError.Network> {
    val url = constructRoute(route)

    val json = gson.toJson(body)

    val reqBody = json.toRequestBody(mediaType)
    val req = Req.Builder().url(url).post(reqBody).build()

    return try {
        val response = makeCall(req)
        responseToResult<Response>(response, gson)
    } catch (e: Exception) {
        handleOtherException(e)
    }
}

suspend inline fun <reified Response : Any> OkHttpClient.authGet(
    route: String,
    params: List<Pair<String, String>>,
    gson: Gson
): Result<Response, DataError.Network> {
    val urlBuilder =
        constructRoute(route).toHttpUrlOrNull()?.newBuilder()
            ?: return Result.Error(DataError.Network.UNKNOWN)

    params.forEach {
        urlBuilder.addQueryParameter(it.first, it.second)
    }

    val url = urlBuilder.build()
    val req = Req.Builder().url(url).get().build()

    return try {
        val response = makeCall(req)
        responseToResult<Response>(response, gson)
    } catch (e: Exception) {
        handleOtherException(e)
    }
}


suspend inline fun <reified Response : Any> OkHttpClient.get(
    route: String,
    params: List<Pair<String, String>>,
    gson: Gson,
    cookie: String
): Result<Response, DataError.Network> {
    val urlBuilder =
        constructRoute(route).toHttpUrlOrNull()?.newBuilder()
            ?: return Result.Error(DataError.Network.UNKNOWN)

    params.forEach {
        urlBuilder.addQueryParameter(it.first, it.second)
    }

    val url = urlBuilder.build()
    val req = Req.Builder().url(url).header("Cookie", cookie).get().build()

    return try {
        val response = makeCall(req)
        responseToResult<Response>(response, gson)
    } catch (e: Exception) {
        handleOtherException(e)
    }
}

suspend fun OkHttpClient.makeCall(request: Req): Response {
    return suspendCoroutine { continuation ->
        try {
            newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    continuation.resume(response)
                }
            })
        } catch (e: Exception) {
            continuation.resumeWithException(e)
        }
    }
}


suspend inline fun <reified T> responseToResult(
    response: Response,
    gson: Gson
): Result<T, DataError.Network> = withContext(Dispatchers.IO) {
    when (response.code) {
        in 200..299 -> {
            val body = response.body!!.string()
            val obj = gson.fromJson(body, T::class.java)

            try {
                val cookie = response.headers["Cookie"]

                Log.d("cookie", cookie.toString())
            } catch (e: Exception) {
                null
            }

            Result.Success(obj)
        }

        401 -> Result.Error(DataError.Network.UNAUTHORISED)
        403 -> Result.Error(DataError.Network.PASSWORD_DOES_NOT_MATCH)
        404 -> Result.Error(DataError.Network.NOT_FOUND)
        406 -> Result.Error(DataError.Network.EMAIL_NOT_VERIFIED)
        409 -> Result.Error(DataError.Network.CONFLICT)
        in 500..599 -> Result.Error(DataError.Network.SERVER_ERROR)
        else -> Result.Error(DataError.Network.UNKNOWN)
    }
}

fun handleOtherException(exception: Exception): Result<Nothing, DataError.Network> {
    return when (exception) {
        is UnresolvedAddressException -> Result.Error(DataError.Network.NO_INTERNET)
        is SerializationException -> Result.Error(DataError.Network.SERIALISATION)
        is CancellationException -> throw exception
        else -> Result.Error(DataError.Network.UNKNOWN)
    }
}


fun constructRoute(route: String) = "http://kyoku.poulastaa.online:8083$route"

fun extractCookie(cookieManager: CookieManager) = try {
    cookieManager.cookieStore.cookies[0].toString()
} catch (e: Exception) {
    null
}