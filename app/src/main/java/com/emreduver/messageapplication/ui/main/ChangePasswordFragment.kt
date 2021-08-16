package com.emreduver.messageapplication.ui.main

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.emreduver.messageapplication.R
import com.emreduver.messageapplication.entities.send.user.ChangePasswordDto
import com.emreduver.messageapplication.entities.send.user.EmailChangeDto
import com.emreduver.messageapplication.ui.auth.RegisterFragmentDirections
import com.emreduver.messageapplication.ui.launch.LaunchActivity
import com.emreduver.messageapplication.utilities.HelperService
import com.emreduver.messageapplication.viewmodels.main.ChangePasswordViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.change_email_fragment.*
import kotlinx.android.synthetic.main.change_email_fragment.btnChangeEmail
import kotlinx.android.synthetic.main.change_password_fragment.*

class ChangePasswordFragment : Fragment() {

    companion object {
        fun newInstance() = ChangePasswordFragment()
    }

    private lateinit var viewModel: ChangePasswordViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.change_password_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(ChangePasswordViewModel::class.java)
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        btnChangePassword.isClickable = false
        controlPassword()
        btnChangePassword.setOnClickListener {
            changePassword()
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun controlPassword(){

        editTextNewPassword1ChangePassword.editText?.doOnTextChanged { text, start, before, count ->
            if(text?.length==0){
                btnChangePassword.isClickable = false
                btnChangePassword.setBackgroundColor(getResources().getColor(R.color.gray))
                editTextNewPassword1ChangePassword.error = null
            }else if (text?.length!! >0 && text.length<8){
                btnChangePassword.isClickable = false
                btnChangePassword.setBackgroundColor(getResources().getColor(R.color.gray))
                editTextNewPassword1ChangePassword.error = "Yeni şifreniz en az 8 karakter olmalı."
            }else if(text.toString() == editTextOldPasswordChangePassword.editText?.text.toString()){
                editTextNewPassword1ChangePassword.error = "Eski ve yeni şifreniz aynı olamaz."
                btnChangePassword.isClickable = false
                btnChangePassword.setBackgroundColor(getResources().getColor(R.color.gray))
                btnChangePassword.isClickable = true
            }else if (text.toString() != editTextOldPasswordChangePassword.editText?.text.toString()&&text.length>=8){
                editTextNewPassword1ChangePassword.error = null
            }
        }

        editTextNewPassword2ChangePassword.editText?.doOnTextChanged { text, start, before, count ->
            if(text.toString() == editTextNewPassword1ChangePassword.editText?.text.toString() && text.toString() != editTextOldPasswordChangePassword.editText?.text.toString()){
                editTextNewPassword2ChangePassword.error = null
                btnChangePassword.setBackgroundColor(getResources().getColor(R.color.blue))
                btnChangePassword.isClickable = true
            }else{
                editTextNewPassword2ChangePassword.error = "Şifreler aynı değil."
                btnChangePassword.isClickable = false
                btnChangePassword.setBackgroundColor(getResources().getColor(R.color.gray))
            }
        }
    }

    private fun changePassword(){
        val id = HelperService.getTokenSharedPreference()!!.UserId
        val oldPassword = editTextOldPasswordChangePassword.editText?.text.toString()
        val newPassword = editTextNewPassword2ChangePassword.editText?.text.toString()

        val changePasswordDto = ChangePasswordDto(id,oldPassword,newPassword)

        viewModel.changePassword(changePasswordDto).observe(viewLifecycleOwner){
            if (it){
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Şifre değiştirme başarılı.")
                    .setMessage("Şifre değişikliği başarılıyla tamamlandı. Uygulamaya yeniden giriş yapmanız için giriş ekranına yönlendiriliyorsunuz.")
                    .setPositiveButton("Tamam") { dialog, which ->
                        HelperService.deleteTokenSharedPreference()
                        val intent = Intent(activity, LaunchActivity::class.java)
                        startActivity(intent)
                        activity?.finish()
                    }
                    .show()
            }else{
                errorListener()
            }
        }
    }

    private fun errorListener() {
        viewModel.errorState.observe(viewLifecycleOwner, {
            HelperService.showMessageByToast(it)
        })
    }
}