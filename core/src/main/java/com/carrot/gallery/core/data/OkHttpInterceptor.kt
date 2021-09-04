package com.carrot.gallery.core.data

import com.carrot.gallery.core.BuildConfig
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import timber.log.Timber
import java.nio.charset.StandardCharsets

/**
 * Created by kyunghoon on 2021-08-28
 */
class OkHttpInterceptor : Interceptor {

    companion object {
        /**
         * Cookie 생략
         */
        val REQUEST_HEADER_LIST_TO_BE_SHOWN = arrayOf("User-Agent", "UserId", "Content-Type")

        /**
         * p3p, set-cookie, access-control*, cache-control, pragma, expires, x-xss-protection, x-frame-options 등은 생략
         */
        val RESPONSE_HEADER_LIST_TO_BE_SHOWN = arrayOf("server", "date", "content-type")
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = StringBuilder()
        if (BuildConfig.DEBUG) {
            log(builder, request)
        }

        val response = chain.proceed(request)
        if (BuildConfig.DEBUG) {
            log(builder, response)
            Timber.d(builder.toString())
        }
        return response
    }

    private fun log(builder: StringBuilder, request: Request) {
        builder.append("\n===== [START]")
        builder.append("\n")
        builder.append("\n [ Request ]")
        builder.append("\n")
        builder.append("\n(1) Request from : ${request.url}")
        builder.append("\n(2) Request Method : ${request.method}")
        builder.append("\n(3) Request Header")
        for (key in request.headers.names()) {
            for (headerKey in REQUEST_HEADER_LIST_TO_BE_SHOWN) {
                if (headerKey == key) {
                    builder.append("\n   - ")
                    builder.append(key)
                    builder.append(" : ")
                    builder.append(request.headers[key])
                }
            }
        }
        if (request.body != null) {
            try {
                builder.append("\n (4) Request Body : ")
                builder.append(getBodyStringFrom(request))
            } catch (e: Exception) {
                builder.append("\n (4) Request Body : error - ")
                builder.append(e.toString())
            }
        }
    }

    private fun log(builder: StringBuilder, response: Response) {
        val requestTime = response.sentRequestAtMillis
        val responseTime = response.receivedResponseAtMillis
        val responseHeaders = response.headers
        builder.append("\n")
        builder.append("\n [ Response ]")
        builder.append("\n")
        builder.append("\n(1) Response from : ")
        builder.append(response.request.url)
        builder.append("\n(2) Response Time : ")
        builder.append(responseTime - requestTime)
        builder.append("ms")
        builder.append("\n(3) Response Code : ")
        builder.append(response.code)
        builder.append("\n(4) Response Message : ")
        builder.append(response.message)
        builder.append("\n(5) Response Headers : ")
        var i = 0
        val len = responseHeaders.size
        while (i < len) {
            val name = responseHeaders.name(i)
            val value = responseHeaders.value(i)
            for (headerKey in RESPONSE_HEADER_LIST_TO_BE_SHOWN) {
                if (headerKey == name) {
                    builder.append("\n   - ")
                    builder.append(name)
                    builder.append(" : ")
                    builder.append(value)
                }
            }
            i++
        }
        try {
            builder.append("\n (6) Response Body : ")
            builder.append(getBodyStringFrom(response))
        } catch (e: Exception) {
            builder.append("\n (6) Response Body : error - ")
            builder.append(e.toString())
        }
        builder.append("\n")
        builder.append("\n===== [END]\n")
    }

    /**
     * response.body().string() 은 1회 밖에 call 못한다. (Response 메모리 이슈)
     * 즉, 로깅을 위해 source buffer 를 clone 해서 사용한다.
     */
    @Throws(Exception::class)
    private fun getBodyStringFrom(rawResponse: Response): String? {
        val responseBody = rawResponse.body
        val source = responseBody!!.source()
        source.request(Long.MAX_VALUE) // request the entire body.
        val buffer = source.buffer
        return buffer.clone().readString(StandardCharsets.UTF_8)
    }

    @Throws(Exception::class)
    private fun getBodyStringFrom(request: Request): String? {
        val copy = request.newBuilder().build()
        val buffer = Buffer()
        copy.body!!.writeTo(buffer)
        return buffer.readString(StandardCharsets.UTF_8)
    }

}