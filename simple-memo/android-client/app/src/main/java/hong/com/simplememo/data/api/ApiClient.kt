package hong.com.simplememo.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private var baseUrl = "http://localhost:8080"
    private var token: String? = null
    private var retrofit: Retrofit? = null
    private var apiService: ApiService? = null

    fun setBaseUrl(url: String) {
        baseUrl = if (url.endsWith("/")) url else "$url/"
        retrofit = null
        apiService = null
    }

    fun setToken(t: String?) { token = t; retrofit = null; apiService = null }

    fun getToken(): String? = token

    private fun getRetrofit(): Retrofit {
        if (retrofit == null) {
            val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
            val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                    token?.let { request.addHeader("satoken", it) }
                    chain.proceed(request.build())
                }
                .addInterceptor(logging)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build()
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }

    fun getApiService(): ApiService {
        if (apiService == null) {
            apiService = getRetrofit().create(ApiService::class.java)
        }
        return apiService!!
    }
}
