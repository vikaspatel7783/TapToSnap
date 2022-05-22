package com.lab49.taptosnap

import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData

class GameTimer(maxMinutes: Int = 2, private val tickObserver: MutableLiveData<Long>): CountDownTimer((maxMinutes * (60 * 1000)).toLong(), 1000) {

    override fun onTick(millisUntilFinished: Long) {
        tickObserver.postValue(millisUntilFinished/1000) // seconds remaining
    }

    override fun onFinish() {
        tickObserver.postValue(0)
    }
}