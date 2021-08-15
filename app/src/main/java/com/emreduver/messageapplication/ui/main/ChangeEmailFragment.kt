package com.emreduver.messageapplication.ui.main

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.emreduver.messageapplication.R
import com.emreduver.messageapplication.entities.send.user.ChangeUsernameDto
import com.emreduver.messageapplication.entities.send.user.EmailChangeDto
import com.emreduver.messageapplication.ui.auth.AuthActivity
import com.emreduver.messageapplication.ui.launch.LaunchActivity
import com.emreduver.messageapplication.utilities.HelperService
import com.emreduver.messageapplication.viewmodels.main.ChangeEmailViewModel
import kotlinx.android.synthetic.main.change_email_fragment.*
import kotlinx.android.synthetic.main.change_username_fragment.*

class ChangeEmailFragment : Fragment() {

    companion object {
        fun newInstance() = ChangeEmailFragment()
    }

    private lateinit var viewModel: ChangeEmailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.change_email_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(ChangeEmailViewModel::class.java)
        btnChangeEmail.isClickable = false
        controlEmailFormat()
        btnChangeEmail.setOnClickListener {
            emailChangeRequest()
        }

        super.onActivityCreated(savedInstanceState)
    }

    private fun controlEmailFormat(){
        editTextChangeEmail.editText?.doOnTextChanged { text, start, before, count ->
            if(text?.length==0){
                btnChangeEmail.isClickable = false
                btnChangeEmail.setBackgroundColor(getResources().getColor(R.color.gray))
            }else if (text!!.isNotEmpty() && text.contains("@") && text.takeLast(4).toString() == ".com"){
                editTextChangeEmail.error = null
                btnChangeEmail.setBackgroundColor(getResources().getColor(R.color.blue))
                btnChangeEmail.isClickable = true
            }else if (text.isNotEmpty()){
                btnChangeEmail.isClickable = false
                btnChangeEmail.setBackgroundColor(getResources().getColor(R.color.gray))
                editTextChangeEmail.error = "Lütfen geçerli bir e posta adresi giriniz"
            }
        }
    }

    private fun emailChangeRequest(){
        val id = HelperService.getTokenSharedPreference()!!.UserId
        val username = editTextChangeEmail.editText?.text.toString()

        val emailChangeDto = EmailChangeDto(id,username)

        viewModel.emailChangeRequest(emailChangeDto).observe(viewLifecycleOwner){
            if (it){
                HelperService.deleteTokenSharedPreference()
                val intent = Intent(activity, LaunchActivity::class.java)
                startActivity(intent)
                activity?.finish()
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