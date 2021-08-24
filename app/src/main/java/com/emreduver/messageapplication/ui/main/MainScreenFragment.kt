package com.emreduver.messageapplication.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.emreduver.messageapplication.R
import com.emreduver.messageapplication.adapter.MessageAdapter
import com.emreduver.messageapplication.adapter.MessageHistoryAdapter
import com.emreduver.messageapplication.ui.launch.LaunchActivity
import com.emreduver.messageapplication.utilities.HelperService
import com.emreduver.messageapplication.viewmodels.main.MainScreenViewModel
import kotlinx.android.synthetic.main.main_screen_fragment.*
import kotlinx.android.synthetic.main.message_fragment.*

class MainScreenFragment : Fragment() {
    private lateinit var viewModel: MainScreenViewModel
    private lateinit var messageHistoryAdapter: MessageHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_screen_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(MainScreenViewModel::class.java)
        messageHistoryAdapter = MessageHistoryAdapter(arrayListOf())

        recyclerViewMainFragment.layoutManager = LinearLayoutManager(context)
        recyclerViewMainFragment.adapter = messageHistoryAdapter

        getMessageHistory(HelperService.getTokenSharedPreference()!!.UserId)

        topAppBar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.searchMainScreen -> {
                    val action = MainScreenFragmentDirections.actionMainScreenFragmentToUserSearchFragment()
                    findNavController().navigate(action)
                    true
                }
                R.id.logoutMainScreen -> {
                    viewModel.logout()
                    HelperService.deleteTokenSharedPreference()
                    val intent = Intent(activity, LaunchActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                    true
                }
                R.id.settingsMainScreen -> {
                    val action = MainScreenFragmentDirections.actionMainScreenFragmentToSettingsFragment()
                    findNavController().navigate(action)
                    true
                }
                else -> super.onOptionsItemSelected(it)
            }
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun getMessageHistory(userId:String){
        viewModel.getMessageHistory(userId).observe(viewLifecycleOwner){
            when(it){
                true -> {
                    getMessages()
                }
                false -> {
                    errorListener()
                }
            }
        }
    }

    private fun getMessages() {
        viewModel.messageHistory.observe(viewLifecycleOwner){
            if (it.size>0){
                messageHistoryAdapter.messageHistory = it
                messageHistoryAdapter.notifyDataSetChanged()
            }else{
                cardViewNoMessageHistory.visibility = View.VISIBLE
            }
        }
    }

    private fun errorListener() {
        viewModel.errorState.observe(viewLifecycleOwner, {
            HelperService.showMessageByToast(it)
        })
    }
}