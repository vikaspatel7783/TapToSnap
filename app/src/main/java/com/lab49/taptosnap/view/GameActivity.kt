package com.lab49.taptosnap.view

import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.lab49.taptosnap.R
import com.lab49.taptosnap.data.CachedData
import com.lab49.taptosnap.viewmodel.GameViewModel
import com.lab49.taptosnap.viewmodel.WelcomeViewModel

class GameActivity: AppCompatActivity() {

    private lateinit var gameItem1TextView: TextView
    private lateinit var gameItem2TextView: TextView
    private lateinit var gameItem3TextView: TextView
    private lateinit var gameItem4TextView: TextView

    private val gameViewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_layout)

        init()
        renderItemListResponse()
    }

    fun init() {
        gameItem1TextView = findViewById<RelativeLayout>(R.id.game_item_1_container).findViewById<TextView>(R.id.template_item_name)
        gameItem2TextView = findViewById<RelativeLayout>(R.id.game_item_2_container).findViewById<TextView>(R.id.template_item_name)
        gameItem3TextView = findViewById<RelativeLayout>(R.id.game_item_3_container).findViewById<TextView>(R.id.template_item_name)
        gameItem4TextView = findViewById<RelativeLayout>(R.id.game_item_4_container).findViewById<TextView>(R.id.template_item_name)
    }

    fun renderItemListResponse() {
        gameViewModel.getItemList().let {
            gameItem1TextView.text = it[0].name
            gameItem2TextView.text = it[1].name
            gameItem3TextView.text = it[2].name
            gameItem4TextView.text = it[3].name
        }
    }
}