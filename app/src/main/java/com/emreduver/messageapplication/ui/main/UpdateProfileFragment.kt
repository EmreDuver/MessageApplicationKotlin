package com.emreduver.messageapplication.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.emreduver.messageapplication.R
import com.emreduver.messageapplication.viewmodels.main.UpdateProfileViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.android.synthetic.main.update_profile_fragment.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class UpdateProfileFragment : Fragment() {
    private lateinit var viewModel: UpdateProfileViewModel
    private var firstname = ""
    private var lastname = ""
    private var statusMessage = ""
    private var birthday:Long = 0

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
            birthday = UpdateProfileFragmentArgs.fromBundle(it).birthdayTimestamp
        }

        editBirthDay.setEndIconOnClickListener{
            val datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Doğum tarihinizi seçin").setSelection(birthday).build()
            datePicker.show(parentFragmentManager, "tag")
            datePicker.addOnPositiveButtonClickListener {
                val calendar = Calendar.getInstance()
                val birthday = "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH)+1}/${calendar.get(Calendar.YEAR)}"
                editBirthDay.editText!!.setText(birthday)
            }
        }
        super.onViewCreated(view, savedInstanceState)
    }

}