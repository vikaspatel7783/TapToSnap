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

    private lateinit var item1RootView: RelativeLayout
    private lateinit var item2RootView: RelativeLayout
    private lateinit var item3RootView: RelativeLayout
    private lateinit var item4RootView: RelativeLayout

    private lateinit var progressBar1: ProgressBar
    private lateinit var progressBar2: ProgressBar
    private lateinit var progressBar3: ProgressBar
    private lateinit var progressBar4: ProgressBar

    private lateinit var countDownTimer: TextView

    private var gameTimeElapsed = false

    private val lookUp = hashMapOf<Int, RelativeLayout>()
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

        countDownTimer = findViewById(R.id.game_text_timer)
        progressBar1 = item1RootView.findViewById(R.id.progressBar)
        progressBar2 = item2RootView.findViewById(R.id.progressBar)
        progressBar3 = item3RootView.findViewById(R.id.progressBar)
        progressBar4 = item4RootView.findViewById(R.id.progressBar)
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

    private fun renderItemListResponse() {
        gameViewModel.getItemList().let {
            lookUp[it[0].id] = item1RootView
            item1RootView.tag = it[0]
            (item1RootView.findViewById(R.id.template_item_name) as TextView).text = it[0].name

            lookUp[it[1].id] = item2RootView
            item2RootView.tag = it[1]
            (item2RootView.findViewById(R.id.template_item_name) as TextView).text = it[1].name

            lookUp[it[2].id] = item3RootView
            item3RootView.tag = it[2]
            (item3RootView.findViewById(R.id.template_item_name) as TextView).text = it[2].name

            lookUp[it[3].id] = item4RootView
            item4RootView.tag = it[3]
            (item4RootView.findViewById(R.id.template_item_name) as TextView).text = it[3].name
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
        Log.d(MY_TAG, "Match Request sent for ${itemMatchRequest.imageLabel}")
        gameViewModel.matchItem(itemMatchRequest).observeOnce(this, { resultStatus ->
            updateUIPostVerify(rootView, resultStatus)
        })
    }

    private fun updateUIPostVerify(rootView: RelativeLayout, resultStatus: ResultStatus) {
        progressBar3.visibility = View.GONE
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