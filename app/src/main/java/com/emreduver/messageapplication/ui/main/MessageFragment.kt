package com.emreduver.messageapplication.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.emreduver.messageapplication.R
import com.emreduver.messageapplication.viewmodels.main.MessageViewModel
import kotlinx.android.synthetic.main.message_fragment.*

class MessageFragment : Fragment() {

    companion object {
        fun newInstance() = MessageFragment()
    }

    private lateinit var viewModel: MessageViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.message_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MessageViewModel::class.java)
        editTextMessage.requestFocus()
    }

}