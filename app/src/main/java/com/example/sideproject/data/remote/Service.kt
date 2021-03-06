package com.example.sideproject.data.remote

import com.example.sideproject.data.model.Account
import com.example.sideproject.data.model.ApiBaseResponse
import com.example.sideproject.data.model.UserInfo
import com.example.sideproject.data.model.Winning
import io.reactivex.Single
import retrofit2.http.*

interface Service {

    @POST("/api/user/register")
    fun register(@Body fields : HashMap<String, String>) : Single<ApiBaseResponse<Account>>

    @POST("/api/user/login")
    fun login(@Body fields : HashMap<String, String>) : Single<ApiBaseResponse<Account>>

    @POST("/api/user/refresh_token")
    fun refreshToken() : Single<ApiBaseResponse<Account>>

    @POST("/api/user/logout")
    fun logout() : Single<ApiBaseResponse<String>>

    @GET("/api/user/me")
    fun getProfile() : Single<ApiBaseResponse<UserInfo>>

    @GET("/api/user/me")
    fun getAccountProfile() : Single<ApiBaseResponse<Account>>

    @PUT("/api/user/me")
    fun putProfile(@Body fields : HashMap<String, String?>) : Single<ApiBaseResponse<Int>>

    @GET("/api/lottery/winning")
    fun getWinningList(@QueryMap params : HashMap<String, String>) : Single<ApiBaseResponse<List<Winning>>>
}