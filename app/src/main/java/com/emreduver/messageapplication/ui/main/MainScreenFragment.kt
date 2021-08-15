package com.emreduver.messageapplication.ui.main

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.emreduver.messageapplication.R
import com.emreduver.messageapplication.ui.auth.AuthActivity
import com.emreduver.messageapplication.ui.auth.LoginFragmentDirections
import com.emreduver.messageapplication.ui.launch.LaunchActivity
import com.emreduver.messageapplication.utilities.HelperService
import com.emreduver.messageapplication.viewmodels.main.MainScreenViewModel
import kotlinx.android.synthetic.main.main_screen_fragment.*

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

        topAppBar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.searchMainScreen -> {
                    HelperService.showMessageByToast("Search")
                    true
                }
                R.id.logoutMainScreen -> {
                    viewModel.logout()
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