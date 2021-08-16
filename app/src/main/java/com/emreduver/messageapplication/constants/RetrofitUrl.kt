package com.emreduver.messageapplication.constants

object RetrofitUrl {
    const val Register = "/api/Auth/register"
    const val Login = "/api/Auth/login"
    const val Logout = "/api/Auth/logout"
    const val CheckToken = "/api/Auth/checktoken"
    const val LoginByRefreshToken = "/api/Auth/loginbyrefreshtoken"

    const val GetUser = "/api/User/getuser"
    const val ResetPasswordRequest = "/api/User/resetpasswordrequest"
    const val EmailChangeRequest = "/api/User/emailchangerequest"
    const val UpdateProfile = "/api/User/updateprofile"
    const val ChangeUsername = "/api/User/changeusername"
    const val ChangePassword = "/api/User/changepassword"
    const val AddUserImage = "/api/User/adduserimage"
    const val DeleteUserImage = "/api/user/deleteuserimage"
}