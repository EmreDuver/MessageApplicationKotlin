package com.emreduver.messageapplication.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.emreduver.messageapplication.R
import com.emreduver.messageapplication.utilities.HelperService

class UserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        HelperService.connectHub()
    }
}