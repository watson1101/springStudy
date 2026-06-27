package hong.com.simplememo.data.model

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    val code: Int,
    val message: String?,
    val data: T?
)

data class LoginRequest(val username: String, val password: String)

data class UserInfoResponse(
    val id: Long?, val username: String?, val nickname: String?,
    val email: String?, val avatar: String?, val role: String?,
    @SerializedName("multiDeviceLogin") val multiDeviceLogin: Int?,
    val token: String?
)

data class MemoResponse(
    val id: Long?, val userId: Long?, val title: String?, val content: String?,
    val memoType: Int?, val memoTypeName: String?, val backgroundImage: String?,
    val remindTime: String?, val cronExpression: String?,
    val status: Int?, val statusName: String?, val sortOrder: Int?,
    val createdTime: String?, val updatedTime: String?
)

data class MemoCreateRequest(
    val title: String, val content: String?, @SerializedName("memoType") val memoType: Int,
    val backgroundImage: String?, val remindTime: String?, val cronExpression: String?
)

data class MemoUpdateRequest(
    val id: Long, val title: String?, val content: String?,
    val backgroundImage: String?, val remindTime: String?,
    val cronExpression: String?, val sortOrder: Int?
)

data class RegisterRequest(
    val username: String, val password: String,
    val nickname: String?, val email: String?
)

data class PasswordChangeRequest(
    @SerializedName("oldPassword") val oldPassword: String,
    @SerializedName("newPassword") val newPassword: String
)
