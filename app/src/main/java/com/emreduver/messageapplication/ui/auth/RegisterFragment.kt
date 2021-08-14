package com.emreduver.messageapplication.ui.auth

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.emreduver.messageapplication.R
import com.emreduver.messageapplication.entities.send.auth.Register
import com.emreduver.messageapplication.utilities.GlobalApp
import com.emreduver.messageapplication.utilities.HelperService
import com.emreduver.messageapplication.viewmodels.auth.AuthViewModel
import com.emreduver.messageapplication.viewmodels.auth.RegisterViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_user.*
import kotlinx.android.synthetic.main.register_fragment.*

class RegisterFragment : Fragment() {

    private lateinit var viewModel: RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.register_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        btnRegister.setOnClickListener {
            userRegister(it)
        }

        textViewForSignInInSignUp.setOnClickListener {
            val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
            findNavController().navigate(action)
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun userRegister(view: View) {
        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)

        val username = editTextUsernameRegister.editText!!.text.toString()
        val email = editTextEmailRegister.editText!!.text.toString()
        val password = editTextPasswordRegister.editText!!.text.toString()
        val firstname = editTextFirstnameRegister.editText!!.text.toString()
        val lastname = editTextLastnameRegister.editText!!.text.toString()

        val user = Register(firstname, lastname, username, email, password)

        viewModel.register(user).observe(viewLifecycleOwner) {
            if (it) {
                MaterialAlertDialogBuilder(view.context)
                    .setTitle("Kayıt Başarılı")
                    .setMessage("Kaydınız başarılıyla tamamlandı. Uygulamaya giriş yapmak için emailinze gönderilen maili doğrulayınız.")
                    .setPositiveButton("Harika") { dialog, which ->
                        val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
                        findNavController().navigate(action)
                    }
                    .show()
            } else {
                errorListener()
            }
        }
    }

    private fun errorListener() {
        viewModel.errorState.observe(viewLifecycleOwner, {
            HelperService.showErrorMessageByToast(it)
        })
    }
}