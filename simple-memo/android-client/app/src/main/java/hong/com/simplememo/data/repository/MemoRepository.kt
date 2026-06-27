package hong.com.simplememo.data.repository

import hong.com.simplememo.data.api.ApiClient
import hong.com.simplememo.data.model.*

class MemoRepository {
    private val api get() = ApiClient.getApiService()

    suspend fun listMemos(): Result<List<MemoResponse>> = runCatching {
        val response = api.listMemos()
        if (response.code == 200) response.data ?: emptyList()
        else throw Exception(response.message ?: "获取失败")
    }

    suspend fun getMemo(id: Long): Result<MemoResponse> = runCatching {
        val response = api.getMemo(id)
        if (response.code == 200 && response.data != null) response.data
        else throw Exception(response.message ?: "获取失败")
    }

    suspend fun createMemo(request: MemoCreateRequest): Result<MemoResponse> = runCatching {
        val response = api.createMemo(request)
        if (response.code == 200 && response.data != null) response.data
        else throw Exception(response.message ?: "创建失败")
    }

    suspend fun updateMemo(request: MemoUpdateRequest): Result<MemoResponse> = runCatching {
        val response = api.updateMemo(request)
        if (response.code == 200 && response.data != null) response.data
        else throw Exception(response.message ?: "更新失败")
    }

    suspend fun completeMemo(id: Long): Result<Unit> = runCatching {
        val response = api.completeMemo(id)
        if (response.code != 200) throw Exception(response.message ?: "操作失败")
    }

    suspend fun deleteMemo(id: Long): Result<Unit> = runCatching {
        val response = api.deleteMemo(id)
        if (response.code != 200) throw Exception(response.message ?: "删除失败")
    }
}
