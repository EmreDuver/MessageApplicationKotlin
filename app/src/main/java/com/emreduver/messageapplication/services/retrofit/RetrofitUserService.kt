package com.emreduver.messageapplication.services.retrofit

import com.emreduver.messageapplication.constants.RetrofitUrl
import com.emreduver.messageapplication.entities.receive.result.ApiResult
import com.emreduver.messageapplication.entities.receive.user.UserDto
import com.emreduver.messageapplication.entities.send.auth.Register
import com.emreduver.messageapplication.entities.send.user.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RetrofitUserService {

    @GET(RetrofitUrl.GetUser)
    suspend fun getUser() : Response<ApiResult<UserDto>>

    @POST(RetrofitUrl.ResetPasswordRequest)
    suspend fun resetPasswordRequest(@Body email: String) : Response<ApiResult<Unit>>

    @POST(RetrofitUrl.EmailChangeRequest)
    suspend fun emailChangeRequest(@Body emailChangeDto: EmailChangeDto) : Response<ApiResult<Unit>>

    @POST(RetrofitUrl.UpdateProfile)
    suspend fun updateProfile(@Body updateProfileDto: UpdateProfileDto) : Response<ApiResult<Unit>>

    @POST(RetrofitUrl.AddUserImage)
    suspend fun addProfilePicture(@Body addProfilePhotoDto: AddProfilePhotoDto) : Response<ApiResult<Unit>>

    @POST(RetrofitUrl.DeleteUserImage)
    suspend fun deleteProfilePicture(@Body userId: String) : Response<ApiResult<Unit>>

    @POST(RetrofitUrl.ChangeUsername)
    suspend fun changeUsername(@Body changeUsernameDto: ChangeUsernameDto) : Response<ApiResult<Unit>>

    @POST(RetrofitUrl.ChangePassword)
    suspend fun changePassword(@Body changePasswordDto: ChangePasswordDto) : Response<ApiResult<Unit>>

}