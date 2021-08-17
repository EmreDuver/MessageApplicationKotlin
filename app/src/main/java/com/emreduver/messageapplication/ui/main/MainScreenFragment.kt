package com.emreduver.messageapplication.ui.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.emreduver.messageapplication.R
import com.emreduver.messageapplication.constants.Api
import com.emreduver.messageapplication.ui.launch.LaunchActivity
import com.emreduver.messageapplication.utilities.HelperService
import com.emreduver.messageapplication.viewmodels.main.MainScreenViewModel
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import kotlinx.android.synthetic.main.main_screen_fragment.*
import java.util.*

class MainScreenFragment : Fragment() {

    private lateinit var viewModel: MainScreenViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_screen_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainScreenViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        cardViewMainScreen.setOnLongClickListener {
            cardViewMainScreen.isChecked = !cardViewMainScreen.isChecked
            true
        }

        cardViewMainScreen.setOnClickListener {
            val action = MainScreenFragmentDirections.actionMainScreenFragmentToMessageFragment("userId","firstname","lastname","photopath")
            findNavController().navigate(action)
        }

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
}