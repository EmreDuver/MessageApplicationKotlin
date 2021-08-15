package com.emreduver.messageapplication.ui.main

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.emreduver.messageapplication.R
import com.emreduver.messageapplication.databinding.SettingsFragmentBinding
import com.emreduver.messageapplication.utilities.HelperService
import com.emreduver.messageapplication.viewmodels.main.SettingsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.settings_fragment.*
import java.io.ByteArrayOutputStream

class SettingsFragment : Fragment() {
    private lateinit var viewModel: SettingsViewModel
    private lateinit var dataBinding: SettingsFragmentBinding
    private val SELECT_IMAGE_CODE = 1000
    private val PERMISSION_CODE = 1001
    private var firstname = ""
    private var lastname = ""
    private var statusMessage = ""
    private var birthday:Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(inflater,R.layout.settings_fragment, container, false)

        return dataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        getUser()
        cardViewChangeUsername.setOnClickListener {
            val action = SettingsFragmentDirections.actionSettingsFragmentToChangeUsernameFragment()
            findNavController().navigate(action)
        }
        cardViewChangeEmail.setOnClickListener {
            val action = SettingsFragmentDirections.actionSettingsFragmentToChangeEmailFragment()
            findNavController().navigate(action)
        }
        cardViewUpdateProfile.setOnClickListener {
            val action = SettingsFragmentDirections.actionSettingsFragmentToUpdateProfileFragment(firstname,lastname,statusMessage,birthday)
            findNavController().navigate(action)
        }
        imageSettings.setOnClickListener {
            if (checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED
            ) {
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permissions, PERMISSION_CODE)
            } else {
                pickImageFromGallery()
            }
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun getUser(){
        viewModel.getUser().observe(viewLifecycleOwner) { user ->

            user.let {
                dataBinding.currentUser = it
                firstname = it.Firstname
                lastname = it.Lastname
                birthday = it.BirthDay.time
                statusMessage = it.StatusMessage

                val calendar = Calendar.getInstance()
                calendar.setTimeInMillis(it.BirthDay.time)
                Log.i("OkHttp","Date: ${calendar.time}")
                HelperService.loadImageFromPicasso(it.PhotoPath, imageSettings)
            }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, SELECT_IMAGE_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGallery()
                } else {
                    HelperService.showMessageByToast("Profil resminizi değiştirmeniz için izin vermeniz gereklidir.")
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == SELECT_IMAGE_CODE && data != null){
            imageSettings.setImageURI(data.data)

            val imageUri = data.data

            imageUri?.let{
                val imageBitmap = if (Build.VERSION.SDK_INT >= 28) {
                    ImageDecoder.decodeBitmap(
                        ImageDecoder.createSource(
                            requireContext().contentResolver,
                            it
                        )
                    )
                } else {
                    MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)
                }
                var a = bitMapToString(imageBitmap)
            }
        }
    }

    private fun errorListener() {
        viewModel.errorState.observe(viewLifecycleOwner, {
            HelperService.showMessageByToast(it)
        })
    }

    private fun bitMapToString(bitmap: Bitmap): String {
        val BAOS = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, BAOS)
        val b = BAOS.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

}