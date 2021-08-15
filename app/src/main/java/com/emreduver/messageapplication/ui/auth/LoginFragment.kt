package com.emreduver.messageapplication.ui.auth

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.emreduver.messageapplication.R
import com.emreduver.messageapplication.entities.send.auth.Login
import com.emreduver.messageapplication.ui.main.UserActivity
import com.emreduver.messageapplication.utilities.HelperService
import com.emreduver.messageapplication.viewmodels.auth.LoginViewModel
import kotlinx.android.synthetic.main.login_fragment.*

class LoginFragment : Fragment() {
    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        textViewCreateAccountLogin.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            findNavController().navigate(action)
        }
        textViewForgetPassword.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToForgetPasswordFragment()
            findNavController().navigate(action)
        }

        btnLoginLogin.setOnClickListener {
            login()
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun login(){
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        val email = editTextEmailLogin.editText?.text.toString()
        val password = editTextPasswordLogin.editText?.text.toString()
        val user = Login(email,password)

        viewModel.login(user).observe(viewLifecycleOwner){
            if (it){
                val intent = Intent(activity, UserActivity::class.java)
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