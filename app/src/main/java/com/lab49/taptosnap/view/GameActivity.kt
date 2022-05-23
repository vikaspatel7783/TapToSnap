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
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.lab49.taptosnap.GameTimer
import com.lab49.taptosnap.R
import com.lab49.taptosnap.data.ResultStatus
import com.lab49.taptosnap.model.ItemMatchRequest
import com.lab49.taptosnap.model.ItemMatchResponse
import com.lab49.taptosnap.model.ItemResponse
import com.lab49.taptosnap.view.Helper.Companion.observeOnce
import com.lab49.taptosnap.viewmodel.GameViewModel

class GameActivity: AppCompatActivity(), View.OnClickListener {

    private val MY_TAG = GameActivity::class.java.simpleName

    private lateinit var countDownTimer: TextView
    private var gameTimeElapsed = false
    private val lookUp = hashMapOf<Int, RelativeLayout>()
    private val gameTimerObserver = MutableLiveData<Long>()
    private val gameTimer = GameTimer(tickObserver = gameTimerObserver)
    private val gameViewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_layout)

        renderItemListResponseAndInitViews()
        initGameTimer()
    }

    private fun renderItemListResponseAndInitViews() {
        gameViewModel.getItemList().let {

            val rootView1 = findViewById<RelativeLayout>(R.id.game_item_1_container)
            rootView1.setOnClickListener(this)
            applyValues(it[0], rootView1)

            val rootView2 = findViewById<RelativeLayout>(R.id.game_item_2_container)
            rootView2.setOnClickListener(this)
            applyValues(it[1], rootView2)

            val rootView3 = findViewById<RelativeLayout>(R.id.game_item_3_container)
            rootView3.setOnClickListener(this)
            applyValues(it[2], rootView3)

            val rootView4 = findViewById<RelativeLayout>(R.id.game_item_4_container)
            rootView4.setOnClickListener(this)
            applyValues(it[3], rootView4)
        }
        countDownTimer = findViewById(R.id.game_text_timer)
    }

    private fun applyValues(itemResponse: ItemResponse, rootView: RelativeLayout) {
        lookUp[itemResponse.id] = rootView
        rootView.tag = itemResponse
        (rootView.findViewById(R.id.template_item_name) as TextView).text = itemResponse.name
    }

    private fun initGameTimer() {
        gameTimer.start()
        gameTimerObserver.observe(this, { secondsRemaining ->
            if (secondsRemaining == 0L) {
                gameTimeElapsed = true
            }
            countDownTimer.text = "${secondsRemaining} seconds remains!"
        })
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

            val itemMatchRequest = ItemMatchRequest(
                    imageLabel = getItemName(requestCode),
                    image = Base64.encodeToString(imageByteArray, NO_WRAP))

            setCapturedImage(getItemRootView(requestCode), imageBitmap)
            verifyImageWithServer(getItemRootView(requestCode), itemMatchRequest)
        }
    }

    private fun setCapturedImage(rootView: RelativeLayout, imageBitmap: Bitmap) {
        rootView.findViewById<ImageView>(R.id.template_imageview).setImageBitmap(imageBitmap)
    }

    private fun verifyImageWithServer(rootView: RelativeLayout, itemMatchRequest: ItemMatchRequest) {
        showProgressbar(rootView, true)
        Log.d(MY_TAG, "Match Request sent for ${itemMatchRequest.imageLabel}")
        gameViewModel.matchItem(itemMatchRequest).observeOnce(this, { resultStatus ->
            updateUIPostVerify(rootView, resultStatus)
        })
    }

    private fun updateUIPostVerify(rootView: RelativeLayout, resultStatus: ResultStatus) {
        showProgressbar(rootView, false)
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
                System.out.println(resultStatus.exception)
                Toast.makeText(this, "Error in communicating with server", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showProgressbar(rootView: RelativeLayout, show: Boolean) {
        rootView.findViewById<ProgressBar>(R.id.progressBar).visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun getItemName(itemId: Int): String {
        return ((lookUp[itemId] as RelativeLayout).tag as ItemResponse).name
    }

    private fun getItemRootView(itemId: Int): RelativeLayout {
        return lookUp[itemId]!!
    }

    override fun onClick(v: View?) {
        val itemId = (((v as RelativeLayout).tag) as ItemResponse).id
        startCameraToCaptureImage(itemId)
    }

}