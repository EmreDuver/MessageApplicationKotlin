package com.emreduver.messageapplication.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.emreduver.messageapplication.R
import com.emreduver.messageapplication.utilities.HelperService
import com.emreduver.messageapplication.viewmodels.main.MessageViewModel
import kotlinx.android.synthetic.main.message_fragment.*

class MessageFragment : Fragment() {
    private lateinit var viewModel: MessageViewModel
    private var id = ""
    private var photoPath = ""
    private var firstname = ""
    private var lastname = ""
    private var status = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.message_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(MessageViewModel::class.java)

        arguments?.let {
            id = MessageFragmentArgs.fromBundle(it).userId
            firstname = MessageFragmentArgs.fromBundle(it).firstname
            lastname = MessageFragmentArgs.fromBundle(it).lastname
            photoPath = MessageFragmentArgs.fromBundle(it).photoPath
        }

        HelperService.loadImageFromPicasso(photoPath,imageMessage)
        textMessageFirstLastName.text = "$firstname $lastname"
        editTextMessage.requestFocus()

        super.onViewCreated(view, savedInstanceState)
    }

}