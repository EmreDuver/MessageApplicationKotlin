package com.emreduver.messageapplication.ui.main

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.emreduver.messageapplication.R
import com.emreduver.messageapplication.databinding.SettingsFragmentBinding
import com.emreduver.messageapplication.entities.send.user.AddProfilePhotoDto
import com.emreduver.messageapplication.ui.auth.LoginFragmentDirections
import com.emreduver.messageapplication.ui.auth.RegisterFragmentDirections
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
    private var photoPath = ""
    private var birthday:Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.settings_fragment,
            container,
            false
        )

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
        cardViewChangePassword.setOnClickListener {
            val action = SettingsFragmentDirections.actionSettingsFragmentToChangePasswordFragment()
            findNavController().navigate(action)
        }
        cardViewUpdateProfile.setOnClickListener {
            val action = SettingsFragmentDirections.actionSettingsFragmentToUpdateProfileFragment(
                firstname,
                lastname,
                statusMessage,
                birthday
            )
            findNavController().navigate(action)
        }
        imageSettings.setOnClickListener {
            if (checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permissions, PERMISSION_CODE)
            } else {
                addOrDeleteImage()
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
                photoPath = it.PhotoPath
                HelperService.loadImageFromPicasso(photoPath, imageSettings)
            }
        }
    }

    private fun addOrDeleteImage(){

        var options = arrayOf("Galeriden Resim Seç", "Profil Resmini Sil", "İptal")


        if (photoPath.contains("default_profile_photo.png"))
            options = arrayOf("Galeriden Resim Seç", "İptal")

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Profil Resminizi Seçin")
            builder.setItems(options){ dialog, which ->
                when(options[which])
                {
                    "Galeriden Resim Seç" -> {
                        pickImageFromGallery()
                    }
                    "Profil Resmini Sil" -> {
                        val builderDeleteImage = AlertDialog.Builder(requireContext())
                        builderDeleteImage.setTitle("Profil Resminizi Silmek İstiyor musunuz ?")
                        builderDeleteImage.setPositiveButton("EVET") { dialog2, which2 ->
                            deleteProfileImage(HelperService.getTokenSharedPreference()!!.UserId)
                        }
                        builderDeleteImage.setNegativeButton("HAYIR") { dialog2, which2 ->
                            dialog2.dismiss()
                        }
                        builderDeleteImage.show()
                    }
                    "İptal" -> {
                        dialog.dismiss()
                    }
                }
            }
            builder.show()
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
                addProfilePicture(bitMapToString(imageBitmap))
                imageSettings.setImageURI(data.data)
            }
        }
    }

    private fun addProfilePicture(photoBase64: String)
    {
        val addProfilePhotoDto = AddProfilePhotoDto(
            HelperService.getTokenSharedPreference()!!.UserId,
            photoBase64
        )
        viewModel.addProfilePicture(addProfilePhotoDto).observe(viewLifecycleOwner) {
            when(it){
                true -> HelperService.showMessageByToast("Resim değiştirme başarılı")
                false -> errorListener()
            }
        }
    }

    private fun deleteProfileImage(userId: String){
        viewModel.deleteProfilePicture(userId).observe(viewLifecycleOwner) {
            when(it){
                true ->{
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Resim silme başarılı.")
                        .setMessage("Ana ekrana yönlendiriliyorsunuz.")
                        .setPositiveButton("Tamam") { dialog, which ->
                            val action = SettingsFragmentDirections.actionSettingsFragmentToMainScreenFragment()
                            findNavController().navigate(action)
                        }.show()
                }
                false -> errorListener()
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