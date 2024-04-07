package com.idmt.simplevoice.ui.network

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.core.os.trace
import com.idmt.simplevoice.ui.network.model.ListResponse
import com.idmt.simplevoice.ui.network.model.category_response.CategoryDropDownResponse
import com.idmt.simplevoice.ui.network.model.zone_response.ZonDropDownResponse
import okhttp3.Call
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import okio.GzipSource
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.nio.charset.Charset


/**
 * Retrofit API declaration for MovieDb Network API
 */
private interface RetrofitNetworkApi {
    @GET(value = ":97/api/Section/Getrpt")
    suspend fun getList(
        @Query("roleid") roleid: Int,
        @Query("sectionid") sectionid: Int,
    ): ListResponse

    @FormUrlEncoded
    @POST(value = ":97/api/Section/InsertSectionData")
    suspend fun submitText(
        @Field("SectionId") SectionId: Int,
        @Field("Notes") Notes: String,
        @Field("UserId") UserId: Int = 8,
        @Field("SectiondataId") SectiondataId: Int = 0,

        ): Unit


    @FormUrlEncoded
    @POST(value = ":97/api/Section/InsertSectionData")
    suspend fun submitInputEntry(
        @Field("id") id: Int,
        @Field("EntryDate") EntryDate: String,
        @Field("CategoryId") CategoryId: Int,
        @Field("SubCategoryId") SubCategoryId: Int,
        @Field("ZoneId") ZoneId: Int = 0,
        @Field("DistrictId") DistrictId: Int = 0,
        @Field("StationId") StationId: Int = 0,
        @Field("InputData") InputData: String,

        ): Unit


    @GET(value = ":90/api/InputEntry/CategoryDropdown")
    suspend fun getCategoryDropDown(): CategoryDropDownResponse

    @GET(value = ":90/api/InputEntry/ZoneDropdown")
    suspend fun getZoneDropDown(): ZonDropDownResponse
}

class RetrofitMoviesNetworkApi {

    private val networkApi = trace("RetrofitNetwork") {
        Retrofit.Builder()
            .baseUrl("http://43.254.41.144")
            .callFactory {
                okHttpCallFactory().newCall(it)
            }
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitNetworkApi::class.java)
    }

    fun okHttpCallFactory(): Call.Factory = trace("") {

        OkHttpClient.Builder()
            .addInterceptor(ErrorResponseInterceptor())
            .addInterceptor(
                HttpLoggingInterceptor()
                    .apply {
                        setLevel(HttpLoggingInterceptor.Level.BODY)
                    },
            )
            .build()
    }

    suspend fun submitText(secitonId: Int, notes: String, userId: Int = 8, sectionDataId: Int = 0) =
        networkApi.submitText(
            SectionId = secitonId,
            Notes = notes,
            UserId = userId,
            SectiondataId = sectionDataId
        )

    suspend fun getList(roleid: Int, sectionid: Int) =
        networkApi.getList(roleid = roleid, sectionid = sectionid)

    suspend fun getZoneDropDown() =
        networkApi.getZoneDropDown()

    suspend fun getCategoryDropDown() =
        networkApi.getCategoryDropDown()

    suspend fun submitInputEntry() =
        networkApi.submitInputEntry(
            id = 0,
            EntryDate = "",
            CategoryId = 0,
            SubCategoryId = 0,
            ZoneId = 0,
            StationId = 0,
            InputData = ""
        )
}

class ErrorResponseInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val response = chain.proceed(chain.request())
        val code = response.code
        try {

            if (code in 400..500) {
                responseBody(response)?.also { errorString ->
                    Log.e("error", errorString)

                }
            }
        } catch (e: Exception) {
            Log.e("error", e.message.toString())

        }

        return chain.proceed(chain.request())
    }

    private fun responseBody(response: Response): String? {
        val responseBody = response.body ?: return null
        val contentLength = responseBody.contentLength()

        if (contentLength == 0L) {
            return null
        }

        val source = responseBody.source()
        source.request(Long.MAX_VALUE) // Buffer the entire body.
        var buffer = source.buffer()
        val headers = response.headers

        if ("gzip".equals(headers.get("Content-Encoding"), ignoreCase = true)) {
            var gzippedResponseBody: GzipSource? = null
            try {
                gzippedResponseBody = GzipSource(buffer.clone())
                buffer = okio.Buffer()
                buffer.writeAll(gzippedResponseBody)
            } finally {
                gzippedResponseBody?.close()
            }
        }

        val charset: Charset = responseBody.contentType()?.charset(UTF8) ?: UTF8
        return buffer.clone().readString(charset)
    }

    private companion object {
        val UTF8: Charset = Charset.forName("UTF-8")
    }
}


object NetworkUtils {
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}