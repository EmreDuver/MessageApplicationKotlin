package com.emreduver.messageapplication.ui.launch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.emreduver.messageapplication.R
import com.emreduver.messageapplication.ui.auth.AuthActivity
import com.emreduver.messageapplication.ui.main.UserActivity
import com.emreduver.messageapplication.viewmodels.launch.LaunchActivityViewModel

class LaunchActivity : AppCompatActivity() {
    lateinit var viewModel: LaunchActivityViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)
        viewModel = ViewModelProvider(this).get(LaunchActivityViewModel::class.java)

        viewModel.tokenCheck().observe(this, {

            val intent = when (it) {
                true -> {
                    Intent(this@LaunchActivity, UserActivity::class.java)
                }
                false -> {
                    Intent(this@LaunchActivity, AuthActivity::class.java)
                }
            }
            startActivity(intent)
            finish()
        })
    }
}