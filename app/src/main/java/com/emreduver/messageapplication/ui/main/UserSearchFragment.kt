package com.emreduver.messageapplication.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.emreduver.messageapplication.R
import com.emreduver.messageapplication.adapter.UserSearchAdapter
import com.emreduver.messageapplication.utilities.HelperService
import com.emreduver.messageapplication.viewmodels.main.UserSearchViewModel
import kotlinx.android.synthetic.main.user_search_fragment.*

class UserSearchFragment : Fragment() {
    private lateinit var viewModel: UserSearchViewModel
    private lateinit var userSearchAdapter : UserSearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userSearchAdapter = UserSearchAdapter(arrayListOf())
    }

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.user_search_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(UserSearchViewModel::class.java)
        recyclerViewUserSearch.adapter = userSearchAdapter
        recyclerViewUserSearch.layoutManager = LinearLayoutManager(context)
        clearUserList()

        editTextUserSearch.setEndIconOnClickListener {
            userSearch(editTextUserSearch.editText?.text.toString())
        }

        super.onViewCreated(view, savedInstanceState)
    }


    private fun userSearch(userName:String) {
        Log.i("OkHttp","userSearch'e girdi")
        viewModel.userSearch(userName).observe(viewLifecycleOwner) { status ->
            when (status) {
                true -> {
                    Log.i("OkHttp","true")
                    getUsers()
                }
                false -> {
                    Log.i("OkHttp","Hata")
                    errorListener()
                    clearUserList()
                }
            }
        }
        Log.i("OkHttp","userSearch'den çıktı")
    }

    private fun errorListener() {
        viewModel.errorState.observe(viewLifecycleOwner, {
            HelperService.showMessageByToast(it)
            editTextUserSearch.editText?.setText("")
        })
    }

    private fun getUsers(){
        viewModel.users.observe(viewLifecycleOwner){ users ->
            userSearchAdapter.userSearchList = users
            userSearchAdapter.notifyDataSetChanged()
        }
        Log.i("OkHttp","getUsers'dan çıktı")
    }

    private fun clearUserList(){
        userSearchAdapter.userSearchList.clear()
        userSearchAdapter.notifyDataSetChanged()
    }

}