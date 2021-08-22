package com.emreduver.messageapplication.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.emreduver.messageapplication.R
import com.emreduver.messageapplication.adapter.MessageAdapter
import com.emreduver.messageapplication.entities.receive.message.Message
import com.emreduver.messageapplication.entities.send.message.FileType
import com.emreduver.messageapplication.entities.send.message.GetMessageDto
import com.emreduver.messageapplication.entities.send.message.SendMessageDto
import com.emreduver.messageapplication.utilities.HelperService
import com.emreduver.messageapplication.viewmodels.main.MessageViewModel
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.message_fragment.*
import java.io.ByteArrayOutputStream

class MessageFragment : Fragment() {
    private lateinit var viewModel: MessageViewModel
    private lateinit var senderUserId: String
    private lateinit var receiverUserId: String
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var getMessageDto: GetMessageDto

    private val SELECT_IMAGE_CODE = 1000
    private val SELECT_VIDEO_CODE = 1001
    private val SELECT_FILE_CODE = 1002
    private val SELECT_AUDIO_CODE = 1003
    private val IMAGE_PERMISSION_CODE = 1100
    private val VIDEO_PERMISSION_CODE = 1101
    private val FILE_PERMISSION_CODE = 1102
    private val AUDIO_PERMISSION_CODE = 1103
    private val LOCATION_PERMISSION_CODE = 1104

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationManager: LocationManager
    private var longitude: Double = 0.0
    private var latitude: Double = 0.0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.message_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(MessageViewModel::class.java)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

        arguments?.let {
            receiverUserId = MessageFragmentArgs.fromBundle(it).userId
            HelperService.loadImageFromPicasso(
                MessageFragmentArgs.fromBundle(it).photoPath,
                imageMessage
            )
            textMessageFirstLastName.text = "${MessageFragmentArgs.fromBundle(it).firstname} ${MessageFragmentArgs.fromBundle(it).lastname}"
        }
        senderUserId = HelperService.getTokenSharedPreference()!!.UserId
        getMessageDto = GetMessageDto(senderUserId, receiverUserId)

        messageAdapter = MessageAdapter(arrayListOf())
        recyclerViewMessageFragment.layoutManager = LinearLayoutManager(context)
        recyclerViewMessageFragment.adapter = messageAdapter

        getMessage(getMessageDto)

        popupMenu()

        editTextMessage.requestFocus()
        view.hideKeyboard()

        cardViewSendMessage.setOnClickListener {
            buttonSendMessage()
        }

        nestedScrollViewMessageFragment.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            nestedScrollViewMessageFragment.post(Runnable {
                nestedScrollViewMessageFragment.scrollTo(0, nestedScrollViewMessageFragment.getChildAt(0).height)
            })
        }

        HelperService.hubConnectionInstance.on("receiveMessage", { receiveMessage ->
            receiveMessage(receiveMessage)
        }, String::class.java)

        HelperService.hubConnectionInstance.on("receiveFile", { receiveFile ->
            messageAdapter.clearData()
            getMessage(getMessageDto)
        }, String::class.java)

        HelperService.hubConnectionInstance.on("userDisconnected", { disconnectedUserId ->
            Log.i("Konsol", disconnectedUserId)
            Log.i("Konsol", receiverUserId)
            if (disconnectedUserId == receiverUserId && textMessageStatus != null) {
                textMessageStatus.text = "Çevrimdışı"
            }
        }, String::class.java)

        HelperService.hubConnectionInstance.on("userConnected", { connectedUserId ->
            Log.i("Konsol", connectedUserId)
            Log.i("Konsol", receiverUserId)
            if (connectedUserId == receiverUserId && textMessageStatus != null) {
                textMessageStatus.text = "Çevrimiçi"
            }

        }, String::class.java)

        super.onViewCreated(view, savedInstanceState)
    }

    private fun buttonSendMessage() {
        val sendMessageDto = SendMessageDto(
            senderUserId,
            receiverUserId,
            editTextMessage.editText?.text.toString(),
            false,
            FileType.NoFile.fileType,
            null,
            null,
            null,
            null,
            null
        )

        val unixTime = System.currentTimeMillis() / 1000L

        val newMessageDto = Message(
            "0",
            senderUserId,
            receiverUserId,
            editTextMessage.editText?.text.toString(),
            0,
            unixTime,
            0,
            false,
            FileType.NoFile.fileType,
            null,
            null,
            null,
            null
        )

        sendMessage(sendMessageDto)
        messageAdapter.messageList.add(newMessageDto)
        nestedScrollViewMessageFragment.post(Runnable {
            nestedScrollViewMessageFragment.scrollTo(0, nestedScrollViewMessageFragment.getChildAt(0).height)
        })
        messageAdapter.notifyDataSetChanged()
        editTextMessage.editText?.text?.clear()
    }

    private fun sendMessage(sendMessageDto: SendMessageDto){

        viewModel.sendMessage(sendMessageDto).observe(viewLifecycleOwner){

            when(it){
                true -> Log.i("OkHttp", "mesaj yollandı")
                false -> errorListener()
            }
        }
    }

    private fun getMessage(getMessageDto: GetMessageDto) {
        viewModel.getMessage(getMessageDto).observe(viewLifecycleOwner) { status ->
            when (status) {
                true -> {
                    getMessages()
                    nestedScrollViewMessageFragment.post(Runnable {
                        nestedScrollViewMessageFragment.scrollTo(0, nestedScrollViewMessageFragment.getChildAt(0).height)
                    })
                }
                false -> {
                    errorListener()
                }
            }
        }
    }

    private fun errorListener() {
        viewModel.errorState.observe(viewLifecycleOwner, {
            HelperService.showMessageByToast(it)
        })
    }

    private fun getMessages(){
        viewModel.messages.observe(viewLifecycleOwner){ messages ->
            messages?.let {
                messageAdapter.updateMessageList(messages)
                nestedScrollViewMessageFragment.post(Runnable {
                    nestedScrollViewMessageFragment.scrollTo(0, nestedScrollViewMessageFragment.getChildAt(0).height)
                })
                recyclerViewMessageFragment.visibility = View.VISIBLE
            }
        }
    }

    private fun receiveMessage(message: String){
        activity?.runOnUiThread{
            val unixTime = System.currentTimeMillis() / 1000L
            val newMessageDto = Message("0", receiverUserId, senderUserId, message, 0, unixTime, 0,false, FileType.NoFile.fileType,null,null,null,null)
            messageAdapter.messageList.add(newMessageDto)
            Log.i("OkHttp", messageAdapter.messageList[messageAdapter.itemCount - 1].toString())
            messageAdapter.notifyDataSetChanged()
            nestedScrollViewMessageFragment.post(Runnable {
                nestedScrollViewMessageFragment.scrollTo(0, nestedScrollViewMessageFragment.getChildAt(0).height)
            })
        }
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun getLocation() {
        if (checkLocationEnabled()) {
            getNewLocationRequest()
        } else {
            HelperService.showMessageByToast("Lütfen konum servisini açın.")
        }
    }

    @SuppressLint("MissingPermission")
    private fun getNewLocationRequest() {
        var locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 100
        locationRequest.fastestInterval = 100

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest, locationCallBack, Looper.getMainLooper()
        )
    }

    private val locationCallBack = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            var lastLocation = p0.lastLocation
            latitude = lastLocation.latitude
            longitude = lastLocation.longitude
            Log.i("OkHttp","Latitude: $latitude")
            Log.i("OkHttp","Longitude: $longitude")
            val sendFileMessageDto = SendMessageDto(
                senderUserId,
                receiverUserId,
                null,
                true,
                FileType.Location.fileType,
                null,
                null,
                null,
                longitude,
                latitude)
            sendMessage(sendFileMessageDto)
            stopLocationRequest()
            messageAdapter.clearData()
            getMessage(getMessageDto)
        }
    }

    private fun stopLocationRequest(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallBack)
    }

    private fun checkLocationEnabled(): Boolean {
        locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isInternetEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        return isLocationEnabled || isInternetEnabled
    }

    private fun selectImage() {
        val intentImage = Intent(Intent.ACTION_PICK)
        intentImage.type = "image/*"
        startActivityForResult(intentImage, SELECT_IMAGE_CODE)
    }

    private fun selectVideo() {
        val intentVideo = Intent(Intent.ACTION_GET_CONTENT)
        intentVideo.type = "video/*"
        startActivityForResult(intentVideo, SELECT_VIDEO_CODE)
    }

    private fun selectFile() {
        val intentFile = Intent(Intent.ACTION_GET_CONTENT)
        intentFile.type = "*/*"
        startActivityForResult(intentFile, SELECT_FILE_CODE)
    }

    private fun selectAudio() {
        val intentAudio = Intent(Intent.ACTION_GET_CONTENT)//MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        intentAudio.type = "audio/*"
        startActivityForResult(intentAudio, SELECT_AUDIO_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<out String>,grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            IMAGE_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectImage()
                } else {
                    HelperService.showMessageByToast("Resim yollamak için dosya izni vermeniz gereklidir.")
                }
            }
            VIDEO_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectImage()
                } else {
                    HelperService.showMessageByToast("Video yollamak için dosya izni vermeniz gereklidir.")
                }
            }
            FILE_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectImage()
                } else {
                    HelperService.showMessageByToast("Dosya yollamak için dosya izni vermeniz gereklidir.")
                }
            }
            AUDIO_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectAudio()
                } else {
                    HelperService.showMessageByToast("Ses yollamak için dosya izni vermeniz gereklidir.")
                }
            }
            LOCATION_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation()
                } else {
                    HelperService.showMessageByToast("Konumunuzu yollamak için konum izni vermeniz gereklidir.")
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null && resultCode == Activity.RESULT_OK) {
            when(requestCode){
                SELECT_IMAGE_CODE -> {
                    data.data!!.let {
                        val sendFileMessageDto = SendMessageDto(
                            senderUserId,
                            receiverUserId,
                            null,
                            true,
                            FileType.Image.fileType,
                            fileToBase64(it),
                            fileName(it),
                            fileExtension(it),
                            null,
                            null)
                        sendMessage(sendFileMessageDto)
                        messageAdapter.clearData()
                        getMessage(getMessageDto)
                    }
                }
                SELECT_VIDEO_CODE -> {
                    data.data!!.let {
                        val sendFileMessageDto = SendMessageDto(
                            senderUserId,
                            receiverUserId,
                            null,
                            true,
                            FileType.Video.fileType,
                            fileToBase64(it),
                            fileName(it),
                            fileExtension(it),
                            null,
                            null)
                        sendMessage(sendFileMessageDto)
                        messageAdapter.clearData()
                        getMessage(getMessageDto)
                    }
                }
                SELECT_FILE_CODE -> {
                    data.data!!.let {
                        val sendFileMessageDto = SendMessageDto(
                            senderUserId,
                            receiverUserId,
                            null,
                            true,
                            FileType.File.fileType,
                            fileToBase64(it),
                            fileName(it),
                            fileExtension(it),
                            null,
                            null)
                        sendMessage(sendFileMessageDto)
                        messageAdapter.clearData()
                        getMessage(getMessageDto)
                    }
                }
                SELECT_AUDIO_CODE -> {
                    data.data!!.let {
                        val sendFileMessageDto = SendMessageDto(
                            senderUserId,
                            receiverUserId,
                            null,
                            true,
                            FileType.Audio.fileType,
                            fileToBase64(it),
                            fileName(it),
                            fileExtension(it),
                            null,
                            null)
                        sendMessage(sendFileMessageDto)
                        messageAdapter.clearData()
                        getMessage(getMessageDto)
                    }
                }
            }
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun fileToBase64(it: Uri): String {
        var os = ByteArrayOutputStream()
        val inputStream = context?.contentResolver?.openInputStream(it)
        val byteArray = inputStream?.readBytes()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    private fun fileName(it: Uri): String {
        return DocumentFile.fromSingleUri(requireContext(), it)?.name.toString().substringBeforeLast('.', "")
    }

    private fun fileExtension(it: Uri): String {
        return DocumentFile.fromSingleUri(requireContext(), it)?.name.toString().substringAfterLast('.', "")
    }


    private fun popupMenu() {
        val popupMenu = PopupMenu(context, cardViewSendFile)
        popupMenu.inflate(R.menu.message_file_menu)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.sendImage -> {
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                        requestPermissions(permissions, IMAGE_PERMISSION_CODE)
                    } else {
                        selectImage()
                    }
                    true
                }
                R.id.sendVideo -> {
                    if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                        requestPermissions(permissions, VIDEO_PERMISSION_CODE)
                    } else {
                        selectVideo()
                    }
                    true
                }
                R.id.sendFile -> {
                    if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                        requestPermissions(permissions, FILE_PERMISSION_CODE)
                    } else {
                        selectFile()
                    }
                    true
                }
                R.id.sendAudio -> {
                    if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                        requestPermissions(permissions, AUDIO_PERMISSION_CODE)
                    } else {
                        selectAudio()
                    }
                    true
                }
                R.id.sendLocation -> {
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED ||
                        ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                        val permissions = arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION)
                        requestPermissions(permissions, LOCATION_PERMISSION_CODE)
                    }else{
                        getLocation()
                    }
                    true
                }
                else ->
                    true
            }
        }

        cardViewSendFile.setOnClickListener {
            try {
                val popup = PopupMenu::class.java.getDeclaredField("mPopup")
                popup.isAccessible = true
                val menu = popup.get(popupMenu)
                menu.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(menu, true)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                popupMenu.show()
            }
        }
    }

}