package com.example.cleanarchitech_text_0506.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast


class ForecdTerminationService: Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    override fun onTaskRemoved(rootIntent: Intent) { //핸들링 하는 부분
        Log.w("onTaskRemoved", "onTaskRemoved - 강제 종료 $rootIntent")
        Toast.makeText(this, "onTaskRemoved ", Toast.LENGTH_SHORT).show()
        stopSelf() //서비스 종료
    }
}