package com.emreduver.messageapplication.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.emreduver.messageapplication.R
import com.emreduver.messageapplication.databinding.FragmentUserSearchRecyclerBinding
import com.emreduver.messageapplication.entities.receive.user.UserSearchDto
import com.emreduver.messageapplication.ui.main.UserSearchFragmentDirections
import com.emreduver.messageapplication.utilities.HelperService

class UserSearchAdapter(var userSearchList: ArrayList<UserSearchDto>): RecyclerView.Adapter<UserSearchAdapter.UserSearchViewModel>()  {

    class UserSearchViewModel(var view: FragmentUserSearchRecyclerBinding) : RecyclerView.ViewHolder(view.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserSearchViewModel {
        val inflater = LayoutInflater.from(parent.context)
        val view = DataBindingUtil.inflate<FragmentUserSearchRecyclerBinding>(inflater,R.layout.fragment_user_search_recycler,parent,false)
        return UserSearchViewModel(view)
    }

    override fun onBindViewHolder(holder: UserSearchViewModel, position: Int) {
        holder.view.userSearch = userSearchList[position]

        HelperService.loadImageFromPicasso(userSearchList[position].photoPath , holder.view.imageUserSearchRecyclerView)

        holder.view.cardViewRecyclerView.setOnClickListener {
            val action = UserSearchFragmentDirections.actionUserSearchFragmentToMessageFragment(userSearchList[position].Id,userSearchList[position].firstName,userSearchList[position].lastName,userSearchList[position].photoPath)
            it.findNavController().navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return userSearchList.size
    }
}