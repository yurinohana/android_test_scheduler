package com.example.yuria.scheduler

import android.app.Application
import io.realm.Realm

// Realmを使用するApplicationクラス
class MyScheduleApplication  : Application(){
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }
}