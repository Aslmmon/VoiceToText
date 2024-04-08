package com.idmt.simplevoice.ui.network

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.core.os.trace
import com.idmt.simplevoice.ui.network.model.ListResponse
import com.idmt.simplevoice.ui.network.model.category_response.CategoryDropDownResponse
import com.idmt.simplevoice.ui.network.model.comments_model.CommentsSuccessResponse
import com.idmt.simplevoice.ui.network.model.comments_model.GetUserCommentsResponse
import com.idmt.simplevoice.ui.network.model.zone_response.ZonDropDownResponse
import okhttp3.Call
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import okio.GzipSource
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
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
    @GET(value = "/api/Section/Getrpt")
    suspend fun getList(
        @Query("roleid") roleid: Int,
        @Query("sectionid") sectionid: Int,
    ): ListResponse

    @FormUrlEncoded
    @POST(value = "/api/Section/InsertSectionData")
    suspend fun submitText(
        @Field("SectionId") SectionId: Int,
        @Field("Notes") Notes: String,
        @Field("UserId") UserId: Int = 8,
        @Field("SectiondataId") SectiondataId: Int = 0,

        ): Unit


    @FormUrlEncoded
    @POST(value = "/api/InputEntry/Stage1")
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


    @GET(value = "/api/InputEntry/CategoryDropdown")
    suspend fun getCategoryDropDown(): CategoryDropDownResponse

    @GET(value = "/api/InputEntry/ZoneDropdown")
    suspend fun getZoneDropDown(): ZonDropDownResponse


    @FormUrlEncoded
    @POST(value = "/api/Section/sp_insert_usercommnetssectiondata")
    suspend fun submitUserComments(
        @Field("sectiondataid") sectiondataid: Int,
        @Field("comments") comments: String,
        @Field("userid") userid: Int = 8
    ): CommentsSuccessResponse


    @GET(value = "/api/Section/Getusercommentssectionddata")
    suspend fun getUserComments(@Query("secdataid") sectiondataid: Int): GetUserCommentsResponse
}

class RetrofitMoviesNetworkApi {

    private val networkApi = trace("RetrofitNetwork") {
        Retrofit.Builder()
            .baseUrl("http://43.254.41.144:90")
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

    suspend fun getSectionComments(sectionid: Int) =
        networkApi.getUserComments(sectionid)


    suspend fun submitSectionComment(sectionid: Int, comments: String, userId: Int) =
        networkApi.submitUserComments(sectionid, comments, userId)


    suspend fun submitInputEntry(
        date: String,
        categoryId: Int,
        subCategoryId: Int,
        zoneId: Int,
        stationId: Int,
        inputData: String
    ) =
        networkApi.submitInputEntry(
            id = 0,
            EntryDate = date,
            CategoryId = categoryId,
            SubCategoryId = subCategoryId,
            ZoneId = zoneId,
            StationId = stationId,
            InputData = inputData
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