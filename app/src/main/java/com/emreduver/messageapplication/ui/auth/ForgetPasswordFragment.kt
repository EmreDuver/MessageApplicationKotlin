package com.emreduver.messageapplication.ui.auth

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.emreduver.messageapplication.R
import com.emreduver.messageapplication.entities.send.auth.Login
import com.emreduver.messageapplication.ui.main.UserActivity
import com.emreduver.messageapplication.utilities.HelperService
import com.emreduver.messageapplication.viewmodels.auth.ForgetPasswordViewModel
import com.emreduver.messageapplication.viewmodels.auth.LoginViewModel
import kotlinx.android.synthetic.main.forget_password_fragment.*
import kotlinx.android.synthetic.main.login_fragment.*

class ForgetPasswordFragment : Fragment() {

    companion object {
        fun newInstance() = ForgetPasswordFragment()
    }

    private lateinit var viewModel: ForgetPasswordViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.forget_password_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ForgetPasswordViewModel::class.java)

        btnForgetPassword.setOnClickListener{
            forgetPassword()
        }
    }

    private fun forgetPassword(){
        if (!editTextForForgetPassword.editText?.text.toString().isNullOrEmpty()){
            val email = editTextForForgetPassword.editText!!.text.toString()
            viewModel.forgetPassword(email).observe(viewLifecycleOwner){
                if (it){
                    HelperService.showMessageByToast("Şifrenizi sıfırlamak için e-posta adresinize göderilen maili açınız.")
                }else{
                    errorListener()
                }
            }
        }else{
            HelperService.showMessageByToast("Lütfen e-posta adresinizi giriniz!")
        }
    }

    private fun errorListener() {
        viewModel.errorState.observe(viewLifecycleOwner, {
            HelperService.showMessageByToast(it)
        })
    }

}