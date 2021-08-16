package com.emreduver.messageapplication.ui.main

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.emreduver.messageapplication.R
import com.emreduver.messageapplication.entities.send.auth.Login
import com.emreduver.messageapplication.entities.send.user.UpdateProfileDto
import com.emreduver.messageapplication.ui.auth.RegisterFragmentDirections
import com.emreduver.messageapplication.utilities.HelperService
import com.emreduver.messageapplication.viewmodels.auth.LoginViewModel
import com.emreduver.messageapplication.viewmodels.main.UpdateProfileViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.login_fragment.*
import kotlinx.android.synthetic.main.update_profile_fragment.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class UpdateProfileFragment : Fragment() {
    private lateinit var viewModel: UpdateProfileViewModel
    private var firstname = ""
    private var lastname = ""
    private var statusMessage = ""
    private var updatedBirthday = ""
    private var birthdayTimestamp:Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.update_profile_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(UpdateProfileViewModel::class.java)
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.let {
            firstname = UpdateProfileFragmentArgs.fromBundle(it).firstname
            lastname = UpdateProfileFragmentArgs.fromBundle(it).lastname
            statusMessage = UpdateProfileFragmentArgs.fromBundle(it).statusMessage.toString()
            birthdayTimestamp = UpdateProfileFragmentArgs.fromBundle(it).birthdayTimestamp
        }

        var calendar = Calendar.getInstance()
        calendar.timeInMillis = birthdayTimestamp
        var birthday = "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH)+1}/${calendar.get(Calendar.YEAR)}"
        updatedBirthday = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH)+1}-${calendar.get(Calendar.DAY_OF_MONTH)}"

        editFirstnameUpdateProfile.editText!!.setText(firstname)
        editLastnameUpdateProfile.editText!!.setText(lastname)
        if(!statusMessage.isNullOrEmpty() && statusMessage.toLowerCase() != "null")  editStatusMessageUpdateProfile.editText!!.setText(statusMessage)
        editBirthdayUpdateProfile.editText!!.setText(birthday)


        editBirthdayUpdateProfile.setEndIconOnClickListener{
            val datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Doğum tarihinizi seçin").setSelection(birthdayTimestamp).build()
            datePicker.show(parentFragmentManager, "tag")
            datePicker.addOnPositiveButtonClickListener {
                calendar = Calendar.getInstance()
                calendar.timeInMillis = datePicker.selection!!

                birthday = "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH)+1}/${calendar.get(Calendar.YEAR)}"
                editBirthdayUpdateProfile.editText!!.setText(birthday)
                updatedBirthday = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH)+1}-${calendar.get(Calendar.DAY_OF_MONTH)}"
            }
        }

        btnProfileUpdate.setOnClickListener {
            updateProfile()
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun updateProfile(){
        val updatedFirstname = editFirstnameUpdateProfile.editText?.text.toString()
        val updatedLastname = editLastnameUpdateProfile.editText?.text.toString()
        val updatedStatusMessage = editStatusMessageUpdateProfile.editText?.text.toString()
        val profile = UpdateProfileDto(HelperService.getTokenSharedPreference()!!.UserId,updatedFirstname,updatedLastname,updatedStatusMessage,updatedBirthday)

        viewModel.updateProfile(profile).observe(viewLifecycleOwner){
            if (it){
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Güncelleme başarılı!")
                    .setMessage("Güncelleme başarılıyla tamamlandı.")
                    .setPositiveButton("Harika") { dialog, which ->
                        val action = UpdateProfileFragmentDirections.actionUpdateProfileFragmentToSettingsFragment()
                        findNavController().navigate(action)
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