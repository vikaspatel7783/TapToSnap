package com.lab49.taptosnap.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.lab49.taptosnap.R
import com.lab49.taptosnap.data.ResultStatus
import com.lab49.taptosnap.viewmodel.WelcomeViewModel

class WelcomeActivity: AppCompatActivity() {

    private val welcomeViewModel: WelcomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_layout)

        findViewById<Button>(R.id.welcome_button_get_started).setOnClickListener {

            welcomeViewModel.getItemList().observe(this, { itemListResponse ->
                when (itemListResponse) {
                    is ResultStatus.Success -> {
                        System.out.println(itemListResponse.data)
                        startActivity(Intent(this, GameActivity::class.java));
                        finish()
                    }

                    is ResultStatus.Failure -> {
                        Toast.makeText(this, "Problem in getting item list", Toast.LENGTH_LONG).show()
                    }
                }
            })
        }
    }
}