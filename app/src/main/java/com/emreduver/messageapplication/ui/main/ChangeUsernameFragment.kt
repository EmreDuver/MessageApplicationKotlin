package com.emreduver.messageapplication.ui.main

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.emreduver.messageapplication.R
import com.emreduver.messageapplication.entities.send.auth.Login
import com.emreduver.messageapplication.entities.send.user.ChangeUsernameDto
import com.emreduver.messageapplication.services.api.AuthService
import com.emreduver.messageapplication.utilities.HelperService
import com.emreduver.messageapplication.viewmodels.main.ChangeUsernameViewModel
import kotlinx.android.synthetic.main.change_username_fragment.*
import kotlinx.android.synthetic.main.login_fragment.*

class ChangeUsernameFragment : Fragment() {

    companion object {
        fun newInstance() = ChangeUsernameFragment()
    }

    private lateinit var viewModel: ChangeUsernameViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.change_username_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(ChangeUsernameViewModel::class.java)

        btnChangeUsername.setOnClickListener {
            changeUsername()
        }

        super.onActivityCreated(savedInstanceState)
    }

    private fun changeUsername(){
        val id = HelperService.getTokenSharedPreference()!!.UserId
        val username = editTextChangeUsername.editText?.text.toString()
        val changeUsernameDto = ChangeUsernameDto(id,username)

        viewModel.changeUsername(changeUsernameDto).observe(viewLifecycleOwner){
            if (it){
                val action = ChangeUsernameFragmentDirections.actionChangeUsernameFragmentToSettingsFragment()
                findNavController().navigate(action)
                findNavController().popBackStack(R.id.changeUsernameFragment,true)
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