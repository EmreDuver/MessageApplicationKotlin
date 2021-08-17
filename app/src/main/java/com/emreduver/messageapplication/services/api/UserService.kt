package com.emreduver.messageapplication.services.api

import com.emreduver.messageapplication.constants.Api
import com.emreduver.messageapplication.entities.receive.result.ApiResult
import com.emreduver.messageapplication.entities.receive.user.UserDto
import com.emreduver.messageapplication.entities.receive.user.UserSearchDto
import com.emreduver.messageapplication.entities.send.user.*
import com.emreduver.messageapplication.services.retrofit.ApiClient
import com.emreduver.messageapplication.services.retrofit.RetrofitUserService
import com.emreduver.messageapplication.utilities.HelperService

class UserService {
    companion object {
        private var retrofitUserServiceWithInterceptor = ApiClient.buildService(Api.baseUrl,RetrofitUserService::class.java,true)
        private var retrofitUserServiceWithoutInterceptor = ApiClient.buildService(Api.baseUrl,RetrofitUserService::class.java,false)

        suspend fun getUser () : ApiResult<UserDto> {
            try {
                val result = retrofitUserServiceWithInterceptor.getUser()

                if (!result.isSuccessful)
                    return HelperService.handleApiError(result)

                return result.body() as ApiResult<UserDto>
            }
            catch (e:Exception){
                return HelperService.handleException(e)
            }
        }

        suspend fun userSearch (username:String) : ApiResult<ArrayList<UserSearchDto>> {
            try {
                val result = retrofitUserServiceWithInterceptor.userSearch(username)

                if (!result.isSuccessful)
                    return HelperService.handleApiError(result)

                return result.body() as ApiResult<ArrayList<UserSearchDto>>
            }
            catch (e:Exception){
                return HelperService.handleException(e)
            }
        }

        suspend fun resetPasswordRequest (email:String) : ApiResult<Unit>{
            try {
                val result = retrofitUserServiceWithoutInterceptor.resetPasswordRequest(email)

                if (!result.isSuccessful)
                    return HelperService.handleApiError(result)

                return result.body() as ApiResult<Unit>
            }
            catch (e:Exception){
                return HelperService.handleException(e)
            }
        }

        suspend fun emailChangeRequest (emailChangeDto: EmailChangeDto) : ApiResult<Unit>{
            try {
                val result = retrofitUserServiceWithInterceptor.emailChangeRequest(emailChangeDto)

                if (!result.isSuccessful)
                    return HelperService.handleApiError(result)

                return result.body() as ApiResult<Unit>
            }
            catch (e:Exception){
                return HelperService.handleException(e)
            }
        }

        suspend fun updateProfile (updateProfileDto: UpdateProfileDto) : ApiResult<Unit>{
            try {
                val result = retrofitUserServiceWithInterceptor.updateProfile(updateProfileDto)

                if (!result.isSuccessful)
                    return HelperService.handleApiError(result)

                return result.body() as ApiResult<Unit>
            }
            catch (e:Exception){
                return HelperService.handleException(e)
            }
        }

        suspend fun addProfilePicture (addProfilePhotoDto: AddProfilePhotoDto) : ApiResult<Unit>{
            try {
                val result = retrofitUserServiceWithInterceptor.addProfilePicture(addProfilePhotoDto)

                if (!result.isSuccessful)
                    return HelperService.handleApiError(result)

                return result.body() as ApiResult<Unit>
            }
            catch (e:Exception){
                return HelperService.handleException(e)
            }
        }

        suspend fun deleteProfilePicture (userId:String) : ApiResult<Unit>{
            try {
                val result = retrofitUserServiceWithInterceptor.deleteProfilePicture(userId)

                if (!result.isSuccessful)
                    return HelperService.handleApiError(result)

                return result.body() as ApiResult<Unit>
            }
            catch (e:Exception){
                return HelperService.handleException(e)
            }
        }

        suspend fun changeUsername (changeUsernameDto: ChangeUsernameDto) : ApiResult<Unit>{
            try {
                val result = retrofitUserServiceWithInterceptor.changeUsername(changeUsernameDto)

                if (!result.isSuccessful)
                    return HelperService.handleApiError(result)

                return result.body() as ApiResult<Unit>
            }
            catch (e:Exception){
                return HelperService.handleException(e)
            }
        }

        suspend fun changePassword (changePasswordDto: ChangePasswordDto) : ApiResult<Unit>{
            try {
                val result = retrofitUserServiceWithInterceptor.changePassword(changePasswordDto)

                if (!result.isSuccessful)
                    return HelperService.handleApiError(result)

                return result.body() as ApiResult<Unit>
            }
            catch (e:Exception){
                return HelperService.handleException(e)
            }
        }
    }
}