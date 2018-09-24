package com.example.yuria.scheduler

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.example.yuria.scheduler.R.id.listView
import com.example.yuria.scheduler.R.id.transition_position
import io.realm.Realm
import io.realm.kotlin.where

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        // Realmクラスのインスタンス作成
        realm = Realm.getDefaultInstance()
        // クエリを発行し，変数に保存
        val schedules = realm.where<Schedule>().findAll()
        // Adapterクラスをインスタンス作成し，上記で取得したスケジュールデータをコンストラクタに渡す
        listView.adapter = ScheduleAdapter(schedules)

        fab.setOnClickListener { view ->
            startActivity<ScheduleEditActivity>()
        }
        // リストビューがタップされた時の処理
        listView.setOnItemClickListener { parent, view, position, id ->
            // タップされたScheduleのインスタンスを取得
            val schedule = parent.getItemAtPosition(position) as Schedule
            // scheduleのidをインテントに格納しScheduleEditActivityに渡す
            startActivity<ScheduleEditActivity>("schedule_id" to schedule.id)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}
