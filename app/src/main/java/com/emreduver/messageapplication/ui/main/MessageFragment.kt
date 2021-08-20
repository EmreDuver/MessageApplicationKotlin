package com.emreduver.messageapplication.ui.main

import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.emreduver.messageapplication.R
import com.emreduver.messageapplication.adapter.MessageAdapter
import com.emreduver.messageapplication.entities.receive.message.Message
import com.emreduver.messageapplication.entities.send.message.GetMessageDto
import com.emreduver.messageapplication.entities.send.message.SendMessageDto
import com.emreduver.messageapplication.utilities.HelperService
import com.emreduver.messageapplication.viewmodels.main.MessageViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.android.synthetic.main.message_fragment.*


class MessageFragment : Fragment() {
    private lateinit var viewModel: MessageViewModel
    private lateinit var senderUserId: String
    private lateinit var receiverUserId: String
    private lateinit var messageAdapter: MessageAdapter

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

        arguments?.let {
            receiverUserId = MessageFragmentArgs.fromBundle(it).userId
            HelperService.loadImageFromPicasso(
                MessageFragmentArgs.fromBundle(it).photoPath,
                imageMessage
            )
            textMessageFirstLastName.text = "${MessageFragmentArgs.fromBundle(it).firstname} ${MessageFragmentArgs.fromBundle(it).lastname}"
        }
        senderUserId = HelperService.getTokenSharedPreference()!!.UserId

        messageAdapter = MessageAdapter(arrayListOf())
        recyclerViewMessageFragment.layoutManager = LinearLayoutManager(context)
        recyclerViewMessageFragment.adapter = messageAdapter

        val getMessageDto = GetMessageDto(senderUserId, receiverUserId)
        getMessage(getMessageDto)

        editTextMessage.requestFocus()
        view.hideKeyboard()

        cardViewSendMessage.setOnClickListener {
            buttonSendMessage()
        }

        nestedScrollViewMessageFragment.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            nestedScrollViewMessageFragment.post(Runnable {
                nestedScrollViewMessageFragment.scrollTo(
                    0, nestedScrollViewMessageFragment.getChildAt(
                        0
                    ).height
                )
            })
        }

        HelperService.hubConnectionInstance.on("receiveMessage", { message ->
            receiveMessage(message)
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
            editTextMessage.editText?.text.toString()
        )

        val unixTime = System.currentTimeMillis() / 1000L

        val newMessageDto = Message(
            "0",
            senderUserId,
            receiverUserId,
            editTextMessage.editText?.text.toString(),
            0,
            unixTime,
            0
        )

        sendMessage(sendMessageDto)
        messageAdapter.messageList.add(newMessageDto)
        nestedScrollViewMessageFragment.post(Runnable {
            nestedScrollViewMessageFragment.scrollTo(
                0, nestedScrollViewMessageFragment.getChildAt(
                    0
                ).height
            )
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
                        nestedScrollViewMessageFragment.scrollTo(
                            0, nestedScrollViewMessageFragment.getChildAt(
                                0
                            ).height
                        )
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
                    nestedScrollViewMessageFragment.scrollTo(
                        0, nestedScrollViewMessageFragment.getChildAt(
                            0
                        ).height
                    )
                })
                recyclerViewMessageFragment.visibility = View.VISIBLE
            }
        }
    }

    private fun receiveMessage(message: String){
        activity?.runOnUiThread{
            val unixTime = System.currentTimeMillis() / 1000L
            val newMessageDto = Message("0", receiverUserId, senderUserId, message, 0, unixTime, 0)
            messageAdapter.messageList.add(newMessageDto)
            Log.i("OkHttp", messageAdapter.messageList[messageAdapter.itemCount - 1].toString())
            messageAdapter.notifyDataSetChanged()
            nestedScrollViewMessageFragment.post(Runnable {
                nestedScrollViewMessageFragment.scrollTo(
                    0, nestedScrollViewMessageFragment.getChildAt(
                        0
                    ).height
                )
            })
        }
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

}