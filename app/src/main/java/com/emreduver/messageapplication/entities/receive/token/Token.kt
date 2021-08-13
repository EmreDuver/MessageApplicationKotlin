package com.emreduver.messageapplication.entities.receive.token

import com.google.gson.annotations.SerializedName

data class Token(
    @SerializedName("accessToken") val AccessToken: String,
    @SerializedName("accessTokenExpiration") val AccessTokenExpiration: Long,
    @SerializedName("refreshToken") val RefreshToken: String,
    @SerializedName("refreshTokenExpiration") val RefreshTokenExpiration: Long
)
