package com.lab49.taptosnap.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.lab49.taptosnap.R
import com.lab49.taptosnap.data.CachedData
import com.lab49.taptosnap.data.ResultStatus
import com.lab49.taptosnap.model.ItemResponse
import com.lab49.taptosnap.viewmodel.WelcomeViewModel
import java.net.UnknownHostException

class WelcomeActivity: AppCompatActivity() {

    private val welcomeViewModel: WelcomeViewModel by viewModels()
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_layout)
        progressBar = findViewById(R.id.progressBar)

        findViewById<Button>(R.id.welcome_button_get_started).setOnClickListener {

            progressBar.visibility = View.VISIBLE

            welcomeViewModel.getItemList().observe(this, { itemListResponse ->
                progressBar.visibility = View.GONE

                when (itemListResponse) {
                    is ResultStatus.Success -> {
                        CachedData.itemList = itemListResponse.data as List<ItemResponse>
                        startActivity(Intent(this, GameActivity::class.java));
                        finish()
                    }

                    is ResultStatus.Failure -> {
                        if (itemListResponse.exception is UnknownHostException) {
                            Toast.makeText(this, "Please check your Internet connectivity", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this, "Problem in getting item list", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            })
        }
    }

}