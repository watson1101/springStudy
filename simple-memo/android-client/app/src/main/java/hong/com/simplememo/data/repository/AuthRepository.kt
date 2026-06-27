package hong.com.simplememo.data.repository

import hong.com.simplememo.data.api.ApiClient
import hong.com.simplememo.data.model.*

class AuthRepository {
    private val api get() = ApiClient.getApiService()

    suspend fun login(username: String, password: String): Result<UserInfoResponse> = runCatching {
        val response = api.login(LoginRequest(username, password))
        if (response.code == 200 && response.data != null) {
            ApiClient.setToken(response.data.token)
            response.data
        } else throw Exception(response.message ?: "登录失败")
    }

    suspend fun register(username: String, password: String, nickname: String?, email: String?): Result<UserInfoResponse> = runCatching {
        val response = api.register(RegisterRequest(username, password, nickname, email))
        if (response.code == 200 && response.data != null) response.data
        else throw Exception(response.message ?: "注册失败")
    }
}
