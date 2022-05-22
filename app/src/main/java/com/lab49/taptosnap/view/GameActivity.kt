package com.lab49.taptosnap.view

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Base64.NO_WRAP
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.lab49.taptosnap.GameTimer
import com.lab49.taptosnap.R
import com.lab49.taptosnap.data.ResultStatus
import com.lab49.taptosnap.model.ItemMatchRequest
import com.lab49.taptosnap.model.ItemMatchResponse
import com.lab49.taptosnap.view.Helper.Companion.observeOnce
import com.lab49.taptosnap.viewmodel.GameViewModel

class GameActivity: AppCompatActivity(), View.OnClickListener {

    private val MY_TAG = GameActivity::class.java.simpleName

    private lateinit var item1RootView: RelativeLayout
    private lateinit var item2RootView: RelativeLayout
    private lateinit var item3RootView: RelativeLayout
    private lateinit var item4RootView: RelativeLayout

    private lateinit var gameItem1TextView: TextView
    private lateinit var gameItem2TextView: TextView
    private lateinit var gameItem3TextView: TextView
    private lateinit var gameItem4TextView: TextView

    private lateinit var countDownTimer: TextView

    private val REQUEST_IMAGE_CAPTURE_1 = 1
    private val REQUEST_IMAGE_CAPTURE_2 = 2
    private val REQUEST_IMAGE_CAPTURE_3 = 3
    private val REQUEST_IMAGE_CAPTURE_4 = 4

    private var gameTimeElapsed = false
    private val gameTimerObserver = MutableLiveData<Long>()
    private val gameTimer = GameTimer(tickObserver = gameTimerObserver)
    private val gameViewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_layout)

        initViewReference()
        renderItemListResponse()
        initGameTimer()
    }


    private fun initViewReference() {

        item1RootView = findViewById(R.id.game_item_1_container)
        item1RootView.setOnClickListener(this)

        item2RootView = findViewById(R.id.game_item_2_container)
        item2RootView.setOnClickListener(this)

        item3RootView = findViewById(R.id.game_item_3_container)
        item3RootView.setOnClickListener(this)

        item4RootView = findViewById(R.id.game_item_4_container)
        item4RootView.setOnClickListener(this)

        gameItem1TextView = item1RootView.findViewById(R.id.template_item_name)
        gameItem2TextView = item2RootView.findViewById(R.id.template_item_name)
        gameItem3TextView = item3RootView.findViewById(R.id.template_item_name)
        gameItem4TextView = item4RootView.findViewById(R.id.template_item_name)

        countDownTimer = findViewById(R.id.game_text_timer)
    }

    private fun initGameTimer() {
        gameTimer.start()
        gameTimerObserver.observe(this, { secondsRemaining ->
            if (secondsRemaining == 0L) {
                gameTimeElapsed = true
            }
            countDownTimer.text = "$secondsRemaining.toString() seconds to go!"
        })
    }

    private fun renderItemListResponse() {
        gameViewModel.getItemList().let {
            item1RootView.tag = it[0]
            gameItem1TextView.text = it[0].name

            gameItem2TextView.text = it[1].name
            item2RootView.tag = it[1]

            gameItem3TextView.text = it[2].name
            item3RootView.tag = it[2]

            gameItem4TextView.text = it[3].name
            item4RootView.tag = it[3]
        }
    }

    private fun startCameraToCaptureImage(requestId: Int) {
        if (gameTimeElapsed) {
            Toast.makeText(this, "Game timer elapsed. Please re-open app to play again", Toast.LENGTH_LONG).show()
            return
        }
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, requestId)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Error in opening camera", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            val imageByteArray = Helper.bitmapToByteArray(imageBitmap)

            when (requestCode) {
                REQUEST_IMAGE_CAPTURE_1 -> {
                    setCapturedImage(item1RootView, imageBitmap)
                    verifyImageWithServer(item1RootView, ItemMatchRequest(
                            imageLabel = gameItem1TextView.text.toString(),
                            image = Base64.encodeToString(imageByteArray, NO_WRAP)))
                }
                REQUEST_IMAGE_CAPTURE_2 -> {
                    setCapturedImage(item2RootView, imageBitmap)
                    verifyImageWithServer(item2RootView, ItemMatchRequest(
                            imageLabel = gameItem2TextView.text.toString(),
                            image = Base64.encodeToString(imageByteArray, NO_WRAP)))
                }

                REQUEST_IMAGE_CAPTURE_3 -> {
                    setCapturedImage(item3RootView, imageBitmap)
                    verifyImageWithServer(item3RootView, ItemMatchRequest(
                            imageLabel = gameItem3TextView.text.toString(),
                            image = Base64.encodeToString(imageByteArray, NO_WRAP)))
                }
                REQUEST_IMAGE_CAPTURE_4 -> {
                    setCapturedImage(item4RootView, imageBitmap)
                    verifyImageWithServer(item4RootView, ItemMatchRequest(
                            imageLabel = gameItem4TextView.text.toString(),
                            image = Base64.encodeToString(imageByteArray, NO_WRAP)))
                }
                else -> throw RuntimeException("Unknown camera request id received")
            }
        }
    }

    private fun setCapturedImage(rootView: RelativeLayout, imageBitmap: Bitmap) {
        rootView.findViewById<ImageView>(R.id.template_imageview).setImageBitmap(imageBitmap)
    }

    private fun verifyImageWithServer(rootView: RelativeLayout, itemMatchRequest: ItemMatchRequest) {
        Log.d(MY_TAG, "Match Request sent for ${itemMatchRequest.imageLabel}")
        gameViewModel.matchItem(itemMatchRequest)
        .observeOnce(this, { resultStatus ->
            updateUIPostVerify(rootView, resultStatus)
        })
    }

    private fun updateUIPostVerify(rootView: RelativeLayout, resultStatus: ResultStatus) {
        when (resultStatus) {
            is ResultStatus.Success -> {
                Log.d(MY_TAG, "Match response received ${resultStatus.data}")
                if ((resultStatus.data as ItemMatchResponse).matched) {
                    rootView.setBackgroundColor(this.getColor(R.color.green))
                } else {
                    rootView.setBackgroundColor(this.getColor(R.color.red))
                }
            }
            is ResultStatus.Failure -> {
                rootView.setBackgroundColor(this.getColor(R.color.red))
                System.out.println(resultStatus.exception)
                Toast.makeText(this, "Error in verify item", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onClick(v: View?) {
        when ((v as RelativeLayout).id) {
            R.id.game_item_1_container -> startCameraToCaptureImage(REQUEST_IMAGE_CAPTURE_1)
            R.id.game_item_2_container -> startCameraToCaptureImage(REQUEST_IMAGE_CAPTURE_2)
            R.id.game_item_3_container -> startCameraToCaptureImage(REQUEST_IMAGE_CAPTURE_3)
            R.id.game_item_4_container -> startCameraToCaptureImage(REQUEST_IMAGE_CAPTURE_4)
            else -> throw RuntimeException("Unknown view found")
        }
    }

}