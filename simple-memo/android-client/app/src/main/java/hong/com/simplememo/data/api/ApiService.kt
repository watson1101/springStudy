package hong.com.simplememo.data.api

import hong.com.simplememo.data.model.*
import retrofit2.http.*

interface ApiService {
    @POST("api/user/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<UserInfoResponse>

    @POST("api/user/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<UserInfoResponse>

    @GET("api/user/me")
    suspend fun getCurrentUser(): ApiResponse<UserInfoResponse>

    @PUT("api/user/password")
    suspend fun changePassword(@Body request: PasswordChangeRequest): ApiResponse<Any>

    @GET("api/memo/list")
    suspend fun listMemos(): ApiResponse<List<MemoResponse>>

    @GET("api/memo/{id}")
    suspend fun getMemo(@Path("id") id: Long): ApiResponse<MemoResponse>

    @POST("api/memo")
    suspend fun createMemo(@Body request: MemoCreateRequest): ApiResponse<MemoResponse>

    @PUT("api/memo")
    suspend fun updateMemo(@Body request: MemoUpdateRequest): ApiResponse<MemoResponse>

    @PUT("api/memo/{id}/complete")
    suspend fun completeMemo(@Path("id") id: Long): ApiResponse<Any>

    @DELETE("api/memo/{id}")
    suspend fun deleteMemo(@Path("id") id: Long): ApiResponse<Any>
}
